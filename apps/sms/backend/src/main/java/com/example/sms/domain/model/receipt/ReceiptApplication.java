package com.example.sms.domain.model.receipt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入金消込明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class ReceiptApplication {
    private Integer id;
    private Integer receiptId;
    private Integer lineNumber;
    private Integer invoiceId;
    private LocalDate applicationDate;
    private BigDecimal appliedAmount;
    private String remarks;
    @Builder.Default
    private Integer version = 1;
    private LocalDateTime createdAt;
}
