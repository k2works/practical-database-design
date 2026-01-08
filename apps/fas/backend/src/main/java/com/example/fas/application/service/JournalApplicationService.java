package com.example.fas.application.service;

import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.dto.CreateJournalCommand;
import com.example.fas.application.port.in.dto.CreateJournalCommand.DebitCreditCommand;
import com.example.fas.application.port.in.dto.CreateJournalCommand.JournalDetailCommand;
import com.example.fas.application.port.in.dto.JournalResponse;
import com.example.fas.application.port.out.JournalRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class JournalApplicationService implements JournalUseCase {

    private final JournalRepository journalRepository;

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
}
