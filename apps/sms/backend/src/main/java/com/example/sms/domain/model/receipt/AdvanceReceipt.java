package com.example.sms.domain.model.receipt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 前受金エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class AdvanceReceipt {
    private Integer id;
    private String advanceReceiptNumber;
    private LocalDate occurredDate;
    private String customerCode;
    private String customerBranchNumber;
    private Integer receiptId;
    private BigDecimal advanceAmount;
    @Builder.Default
    private BigDecimal usedAmount = BigDecimal.ZERO;
    private BigDecimal balance;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
