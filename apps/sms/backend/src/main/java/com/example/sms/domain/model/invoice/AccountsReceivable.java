package com.example.sms.domain.model.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 売掛金残高エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class AccountsReceivable {
    private Integer id;
    private String customerCode;
    private String customerBranchNumber;
    private LocalDate baseDate;
    @Builder.Default
    private BigDecimal previousMonthBalance = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal currentMonthSales = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal currentMonthReceipts = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal currentMonthBalance = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
