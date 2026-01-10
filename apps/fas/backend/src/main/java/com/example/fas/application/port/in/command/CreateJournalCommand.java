package com.example.fas.application.port.in.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 仕訳登録コマンド.
 */
public record CreateJournalCommand(
        @NotNull(message = "計上日は必須です")
        LocalDate postingDate,

        LocalDate entryDate,
        String voucherType,
        Boolean closingJournalFlag,
        Boolean singleEntryFlag,
        Boolean periodicPostingFlag,
        String employeeCode,
        String departmentCode,

        @NotEmpty(message = "仕訳明細は1件以上必要です")
        @Valid
        List<JournalDetailCommand> details
) {
    /**
     * 仕訳明細コマンド.
     */
    public record JournalDetailCommand(
            String lineSummary,

            @NotEmpty(message = "借方・貸方明細は1件以上必要です")
            @Valid
            List<DebitCreditCommand> debitCreditDetails
    ) {
    }

    /**
     * 借方・貸方明細コマンド.
     */
    public record DebitCreditCommand(
            @NotBlank(message = "借方・貸方区分は必須です")
            String debitCreditType,

            @NotBlank(message = "勘定科目コードは必須です")
            String accountCode,

            String subAccountCode,
            String departmentCode,

            @NotNull(message = "金額は必須です")
            BigDecimal amount,

            String currencyCode,
            BigDecimal exchangeRate,
            BigDecimal baseCurrencyAmount,
            String taxType,
            Integer taxRate,
            String taxCalcType,
            LocalDate dueDate,
            Boolean cashFlowFlag
    ) {
    }
}
