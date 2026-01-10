package com.example.fas.application.service;

import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.DebitCreditCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.JournalDetailCommand;
import com.example.fas.application.port.in.dto.JournalImportResult;
import com.example.fas.application.port.in.dto.JournalImportResult.ImportError;
import com.example.fas.application.port.in.dto.JournalResponse;
import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.model.common.PageResult;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.example.fas.domain.exception.JournalAlreadyCancelledException;
import com.example.fas.domain.exception.JournalBalanceException;
import com.example.fas.domain.exception.JournalNotFoundException;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import com.example.fas.domain.model.journal.JournalVoucherType;
import com.example.fas.domain.model.journal.TaxCalculationType;
import com.example.fas.domain.model.journal.TaxType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 仕訳アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings({
    "PMD.AvoidInstantiatingObjectsInLoops",
    "PMD.ExcessiveImports",
    "PMD.CouplingBetweenObjects",
    "PMD.GodClass"
})
public class JournalApplicationService implements JournalUseCase {

    private static final int MIN_CSV_COLUMNS = 6;

    private final JournalRepository journalRepository;
    private final JournalImportHelper journalImportHelper;

    @Override
    public JournalResponse getJournal(String voucherNumber) {
        Journal journal = journalRepository.findWithDetails(voucherNumber)
                .orElseThrow(() -> new JournalNotFoundException(voucherNumber));
        return JournalResponse.from(journal);
    }

    @Override
    public List<JournalResponse> getJournalsByDateRange(LocalDate fromDate, LocalDate toDate) {
        return journalRepository.findByPostingDateBetween(fromDate, toDate).stream()
                .map(JournalResponse::from)
                .toList();
    }

    @Override
    public List<JournalResponse> getJournalsByAccountCode(String accountCode) {
        return journalRepository.findByAccountCode(accountCode).stream()
                .map(JournalResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public JournalResponse createJournal(CreateJournalCommand command) {
        Journal journal = buildJournalFromCommand(command);

        if (!journal.isBalanced()) {
            throw new JournalBalanceException(
                    journal.getDebitTotal(), journal.getCreditTotal());
        }

        journalRepository.save(journal);

        return JournalResponse.from(journal);
    }

    @Override
    @Transactional
    public JournalResponse cancelJournal(String voucherNumber) {
        Journal original = journalRepository.findWithDetails(voucherNumber)
                .orElseThrow(() -> new JournalNotFoundException(voucherNumber));

        if (Boolean.TRUE.equals(original.getRedSlipFlag())) {
            throw new JournalAlreadyCancelledException(voucherNumber);
        }

        Journal reversal = createReversalJournal(original);
        journalRepository.save(reversal);

        return JournalResponse.from(reversal);
    }

    @Override
    @Transactional
    public void deleteJournal(String voucherNumber) {
        journalRepository.findByVoucherNumber(voucherNumber)
                .orElseThrow(() -> new JournalNotFoundException(voucherNumber));
        journalRepository.delete(voucherNumber);
    }

    @Override
    public PageResult<JournalResponse> getJournals(int page, int size,
            LocalDate fromDate, LocalDate toDate, String keyword) {
        PageResult<Journal> pageResult = journalRepository.findWithPagination(
                page, size, fromDate, toDate, keyword);
        return new PageResult<>(
                pageResult.getContent().stream()
                        .map(JournalResponse::from)
                        .toList(),
                pageResult.getPage(),
                pageResult.getSize(),
                pageResult.getTotalElements());
    }

    @Override
    @SuppressWarnings({
        "PMD.CognitiveComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.AssignmentInOperand",
        "PMD.AvoidCatchingGenericException"
    })
    public JournalImportResult importJournalsFromCsv(InputStream inputStream,
            boolean skipHeaderLine, boolean skipEmptyLines) {
        List<ImportError> errors = new ArrayList<>();
        int totalCount = 0;
        int successCount = 0;
        int skippedCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            // まず全行を読み込んでパース
            List<CsvLineRecord> records = new ArrayList<>();
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // ヘッダー行スキップ
                if (skipHeaderLine && lineNumber == 1) {
                    continue;
                }

                // 空行スキップ
                if (skipEmptyLines && line.isBlank()) {
                    skippedCount++;
                    continue;
                }

                try {
                    CsvLineRecord record = parseCsvLineToRecord(line, lineNumber);
                    records.add(record);
                } catch (Exception e) {
                    errors.add(ImportError.builder()
                            .lineNumber(lineNumber)
                            .message(e.getMessage())
                            .lineContent(line.length() > 100 ? line.substring(0, 100) + "..." : line)
                            .build());
                }
            }

            // 起票日＋摘要でグループ化
            java.util.Map<String, List<CsvLineRecord>> grouped = records.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            r -> r.postingDate().toString() + "|" + r.lineSummary(),
                            java.util.LinkedHashMap::new,
                            java.util.stream.Collectors.toList()));

            totalCount = grouped.size();

            // グループごとに仕訳を作成（各仕訳は独立したトランザクション）
            for (var entry : grouped.entrySet()) {
                List<CsvLineRecord> groupRecords = entry.getValue();
                try {
                    CreateJournalCommand command = buildJournalCommandFromRecords(groupRecords);
                    journalImportHelper.createJournalInNewTransaction(command);
                    successCount++;
                } catch (Exception e) {
                    int firstLineNumber = groupRecords.get(0).lineNumber();
                    errors.add(ImportError.builder()
                            .lineNumber(firstLineNumber)
                            .message(e.getMessage())
                            .lineContent(groupRecords.stream()
                                    .map(r -> "行" + r.lineNumber())
                                    .collect(java.util.stream.Collectors.joining(", ")))
                            .build());
                }
            }
        } catch (java.io.IOException e) {
            errors.add(ImportError.builder()
                    .lineNumber(0)
                    .message("ファイル読み込みエラー: " + e.getMessage())
                    .build());
        }

        return JournalImportResult.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .skippedCount(skippedCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    /**
     * CSV 行をパースしてレコードを生成.
     */
    private record CsvLineRecord(
            int lineNumber,
            LocalDate postingDate,
            String debitCreditType,
            String accountCode,
            String subAccountCode,
            String departmentCode,
            BigDecimal amount,
            String lineSummary
    ) { }

    /**
     * CSV 行をパースしてレコードを生成.
     * CSV フォーマット: 起票日,貸借区分,勘定科目コード,補助科目コード,部門コード,金額,摘要
     */
    @SuppressWarnings("PMD.PrematureDeclaration")
    private CsvLineRecord parseCsvLineToRecord(String line, int lineNumber) {
        String[] columns = line.split(",", -1);
        if (columns.length < MIN_CSV_COLUMNS) {
            throw new IllegalArgumentException(
                    "列数が不足しています（必要: 6列以上、実際: " + columns.length + "列）");
        }

        LocalDate postingDate = parseDate(columns[0].trim(), lineNumber);
        String debitCreditType = columns[1].trim();
        String accountCode = columns[2].trim();
        String subAccountCode = columns[3].trim();
        String departmentCode = columns[4].trim();
        BigDecimal amount = parseAmount(columns[5].trim(), lineNumber);
        String lineSummary = columns.length > MIN_CSV_COLUMNS ? columns[6].trim() : "";

        if (accountCode.isEmpty()) {
            throw new IllegalArgumentException("勘定科目コードは必須です");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金額は正の数で指定してください");
        }

        return new CsvLineRecord(lineNumber, postingDate, debitCreditType,
                accountCode, subAccountCode, departmentCode, amount, lineSummary);
    }

    /**
     * グループ化されたレコードから仕訳登録コマンドを生成.
     */
    private CreateJournalCommand buildJournalCommandFromRecords(List<CsvLineRecord> records) {
        CsvLineRecord first = records.get(0);

        List<DebitCreditCommand> dcCommands = new ArrayList<>();
        for (CsvLineRecord record : records) {
            dcCommands.add(DebitCreditCommand.builder()
                    .debitCreditType(record.debitCreditType())
                    .accountCode(record.accountCode())
                    .subAccountCode(record.subAccountCode().isEmpty() ? null : record.subAccountCode())
                    .departmentCode(record.departmentCode().isEmpty() ? null : record.departmentCode())
                    .amount(record.amount())
                    .build());
        }

        JournalDetailCommand detailCommand = JournalDetailCommand.builder()
                .lineSummary(first.lineSummary())
                .debitCreditDetails(dcCommands)
                .build();

        return CreateJournalCommand.builder()
                .postingDate(first.postingDate())
                .entryDate(LocalDate.now())
                .voucherType("NORMAL")
                .details(List.of(detailCommand))
                .build();
    }

    private LocalDate parseDate(String dateStr, int lineNumber) {
        if (dateStr.isEmpty()) {
            throw new IllegalArgumentException("起票日は必須です");
        }
        try {
            // yyyy/MM/dd または yyyy-MM-dd 形式をサポート
            if (dateStr.contains("/")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } else {
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "日付形式が不正です（行 " + lineNumber + "）: " + dateStr, e);
        }
    }

    private BigDecimal parseAmount(String amountStr, int lineNumber) {
        if (amountStr.isEmpty()) {
            return null;
        }
        try {
            // カンマ区切りを除去
            return new BigDecimal(amountStr.replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "金額形式が不正です（行 " + lineNumber + "）: " + amountStr, e);
        }
    }

    private Journal buildJournalFromCommand(CreateJournalCommand command) {
        String voucherNumber = generateVoucherNumber();
        LocalDateTime now = LocalDateTime.now();

        List<JournalDetail> details = new ArrayList<>();
        int lineNumber = 1;

        for (JournalDetailCommand detailCmd : command.getDetails()) {
            List<JournalDebitCreditDetail> dcDetails = new ArrayList<>();

            for (DebitCreditCommand dcCmd : detailCmd.getDebitCreditDetails()) {
                dcDetails.add(buildDebitCreditDetail(voucherNumber, lineNumber, dcCmd, now));
            }

            details.add(JournalDetail.builder()
                    .journalVoucherNumber(voucherNumber)
                    .lineNumber(lineNumber)
                    .lineSummary(detailCmd.getLineSummary())
                    .debitCreditDetails(dcDetails)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
            lineNumber++;
        }

        JournalVoucherType voucherType = parseVoucherType(command.getVoucherType());
        if (voucherType == null) {
            voucherType = JournalVoucherType.NORMAL;
        }

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(command.getPostingDate())
                .entryDate(command.getEntryDate() != null
                        ? command.getEntryDate() : LocalDate.now())
                .voucherType(voucherType)
                .closingJournalFlag(command.getClosingJournalFlag())
                .singleEntryFlag(command.getSingleEntryFlag())
                .periodicPostingFlag(command.getPeriodicPostingFlag())
                .employeeCode(command.getEmployeeCode())
                .departmentCode(command.getDepartmentCode())
                .redSlipFlag(false)
                .details(details)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private JournalDebitCreditDetail buildDebitCreditDetail(
            String voucherNumber, int lineNumber, DebitCreditCommand cmd, LocalDateTime now) {
        return JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(lineNumber)
                .debitCreditType(DebitCreditType.fromDisplayName(cmd.getDebitCreditType()))
                .accountCode(cmd.getAccountCode())
                .subAccountCode(cmd.getSubAccountCode())
                .departmentCode(cmd.getDepartmentCode())
                .amount(cmd.getAmount())
                .currencyCode(cmd.getCurrencyCode())
                .exchangeRate(cmd.getExchangeRate())
                .baseCurrencyAmount(cmd.getBaseCurrencyAmount())
                .taxType(parseTaxType(cmd.getTaxType()))
                .taxRate(cmd.getTaxRate())
                .taxCalcType(parseTaxCalcType(cmd.getTaxCalcType()))
                .dueDate(cmd.getDueDate())
                .cashFlowFlag(cmd.getCashFlowFlag())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Journal createReversalJournal(Journal original) {
        String voucherNumber = generateVoucherNumber();
        LocalDateTime now = LocalDateTime.now();

        List<JournalDetail> reversalDetails = new ArrayList<>();

        for (JournalDetail origDetail : original.getDetails()) {
            List<JournalDebitCreditDetail> reversalDcDetails = new ArrayList<>();

            for (JournalDebitCreditDetail origDc : origDetail.getDebitCreditDetails()) {
                reversalDcDetails.add(JournalDebitCreditDetail.builder()
                        .journalVoucherNumber(voucherNumber)
                        .lineNumber(origDc.getLineNumber())
                        .debitCreditType(origDc.getDebitCreditType() == DebitCreditType.DEBIT
                                ? DebitCreditType.CREDIT : DebitCreditType.DEBIT)
                        .accountCode(origDc.getAccountCode())
                        .subAccountCode(origDc.getSubAccountCode())
                        .departmentCode(origDc.getDepartmentCode())
                        .amount(origDc.getAmount())
                        .currencyCode(origDc.getCurrencyCode())
                        .exchangeRate(origDc.getExchangeRate())
                        .baseCurrencyAmount(origDc.getBaseCurrencyAmount())
                        .taxType(origDc.getTaxType())
                        .taxRate(origDc.getTaxRate())
                        .taxCalcType(origDc.getTaxCalcType())
                        .dueDate(origDc.getDueDate())
                        .cashFlowFlag(origDc.getCashFlowFlag())
                        .createdAt(now)
                        .updatedAt(now)
                        .build());
            }

            reversalDetails.add(JournalDetail.builder()
                    .journalVoucherNumber(voucherNumber)
                    .lineNumber(origDetail.getLineNumber())
                    .lineSummary(origDetail.getLineSummary() + "（取消）")
                    .debitCreditDetails(reversalDcDetails)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(LocalDate.now())
                .entryDate(LocalDate.now())
                .voucherType(original.getVoucherType())
                .closingJournalFlag(original.getClosingJournalFlag())
                .singleEntryFlag(original.getSingleEntryFlag())
                .periodicPostingFlag(original.getPeriodicPostingFlag())
                .employeeCode(original.getEmployeeCode())
                .departmentCode(original.getDepartmentCode())
                .redSlipFlag(true)
                .details(reversalDetails)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private String generateVoucherNumber() {
        return "J" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private JournalVoucherType parseVoucherType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return JournalVoucherType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private TaxType parseTaxType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return TaxType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private TaxCalculationType parseTaxCalcType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return TaxCalculationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public long countJournalsByDateRange(LocalDate fromDate, LocalDate toDate) {
        return journalRepository.countByPostingDateBetween(fromDate, toDate);
    }
}
