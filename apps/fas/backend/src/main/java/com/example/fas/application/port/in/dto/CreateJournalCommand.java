package com.example.fas.application.port.in.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳登録コマンド DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJournalCommand {

    @NotNull(message = "計上日は必須です")
    private LocalDate postingDate;

    private LocalDate entryDate;
    private String voucherType;
    private Boolean closingJournalFlag;
    private Boolean singleEntryFlag;
    private Boolean periodicPostingFlag;
    private String employeeCode;
    private String departmentCode;

    @NotEmpty(message = "仕訳明細は1件以上必要です")
    @Valid
    private List<JournalDetailCommand> details;

    /**
     * 仕訳明細コマンド.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JournalDetailCommand {
        private String lineSummary;

        @NotEmpty(message = "借方・貸方明細は1件以上必要です")
        @Valid
        private List<DebitCreditCommand> debitCreditDetails;
    }

    /**
     * 借方・貸方明細コマンド.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DebitCreditCommand {

        @NotBlank(message = "借方・貸方区分は必須です")
        private String debitCreditType;

        @NotBlank(message = "勘定科目コードは必須です")
        private String accountCode;

        private String subAccountCode;
        private String departmentCode;

        @NotNull(message = "金額は必須です")
        private BigDecimal amount;

        private String currencyCode;
        private BigDecimal exchangeRate;
        private BigDecimal baseCurrencyAmount;
        private String taxType;
        private Integer taxRate;
        private String taxCalcType;
        private LocalDate dueDate;
        private Boolean cashFlowFlag;
    }
}
