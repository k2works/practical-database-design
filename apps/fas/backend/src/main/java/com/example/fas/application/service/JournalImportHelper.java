package com.example.fas.application.service;

import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.DebitCreditCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.JournalDetailCommand;
import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.exception.JournalBalanceException;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 仕訳取込ヘルパー（個別トランザクション用）.
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class JournalImportHelper {

    private final JournalRepository journalRepository;

    /**
     * 新規トランザクションで仕訳を作成.
     * 各仕訳が独立したトランザクションで処理され、
     * 1件の失敗が他の仕訳に影響しない.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createJournalInNewTransaction(CreateJournalCommand command) {
        Journal journal = buildJournalFromCommand(command);

        if (!journal.isBalanced()) {
            throw new JournalBalanceException(
                    journal.getDebitTotal(), journal.getCreditTotal());
        }

        journalRepository.save(journal);
    }

    private Journal buildJournalFromCommand(CreateJournalCommand command) {
        String voucherNumber = generateVoucherNumber();
        LocalDateTime now = LocalDateTime.now();

        List<JournalDetail> details = new ArrayList<>();
        int lineNumber = 1;

        for (JournalDetailCommand detailCmd : command.details()) {
            List<JournalDebitCreditDetail> dcDetails = new ArrayList<>();

            for (DebitCreditCommand dcCmd : detailCmd.debitCreditDetails()) {
                dcDetails.add(buildDebitCreditDetail(voucherNumber, lineNumber, dcCmd, now));
            }

            details.add(JournalDetail.builder()
                    .journalVoucherNumber(voucherNumber)
                    .lineNumber(lineNumber)
                    .lineSummary(detailCmd.lineSummary())
                    .debitCreditDetails(dcDetails)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
            lineNumber++;
        }

        JournalVoucherType voucherType = parseVoucherType(command.voucherType());
        if (voucherType == null) {
            voucherType = JournalVoucherType.NORMAL;
        }

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(command.postingDate())
                .entryDate(command.entryDate() != null
                        ? command.entryDate() : LocalDate.now())
                .voucherType(voucherType)
                .closingJournalFlag(command.closingJournalFlag())
                .singleEntryFlag(command.singleEntryFlag())
                .periodicPostingFlag(command.periodicPostingFlag())
                .employeeCode(command.employeeCode())
                .departmentCode(command.departmentCode())
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
                .debitCreditType(DebitCreditType.fromDisplayName(cmd.debitCreditType()))
                .accountCode(cmd.accountCode())
                .subAccountCode(cmd.subAccountCode())
                .departmentCode(cmd.departmentCode())
                .amount(cmd.amount())
                .currencyCode(cmd.currencyCode())
                .exchangeRate(cmd.exchangeRate())
                .baseCurrencyAmount(cmd.baseCurrencyAmount())
                .taxType(parseTaxType(cmd.taxType()))
                .taxRate(cmd.taxRate())
                .taxCalcType(parseTaxCalcType(cmd.taxCalcType()))
                .dueDate(cmd.dueDate())
                .cashFlowFlag(cmd.cashFlowFlag())
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
