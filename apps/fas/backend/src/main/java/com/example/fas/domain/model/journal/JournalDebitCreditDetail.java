package com.example.fas.domain.model.journal;

import com.example.fas.domain.model.account.DebitCreditType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳貸借明細エンティティ.
 * 借方・貸方の詳細情報を管理する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalDebitCreditDetail {
    private String journalVoucherNumber;
    private Integer lineNumber;
    private DebitCreditType debitCreditType;
    private String accountCode;
    private String subAccountCode;
    private String departmentCode;
    private String projectCode;
    private BigDecimal amount;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private BigDecimal baseCurrencyAmount;
    private TaxType taxType;
    private Integer taxRate;
    private TaxCalculationType taxCalcType;
    private LocalDate dueDate;
    private Boolean cashFlowFlag;
    private String segmentCode;
    private String counterAccountCode;
    private String counterSubAccountCode;
    private String tagCode;
    private String tagContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Integer version = 1;
}
