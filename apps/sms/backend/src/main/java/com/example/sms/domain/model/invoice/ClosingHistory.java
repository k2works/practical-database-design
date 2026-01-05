package com.example.sms.domain.model.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 請求締履歴エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosingHistory {
    private Integer id;
    private String customerCode;
    private String customerBranchNumber;
    private String closingYearMonth;
    private LocalDate closingDate;
    private Integer salesCount;
    private BigDecimal salesTotal;
    private BigDecimal taxTotal;
    private Integer invoiceId;
    private LocalDateTime processedAt;
}
