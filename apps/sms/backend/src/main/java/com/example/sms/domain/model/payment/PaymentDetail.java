package com.example.sms.domain.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支払明細データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetail {
    private Integer id;
    private Integer paymentId;
    private Integer lineNumber;
    private String purchaseNumber;
    private LocalDate purchaseDate;
    private BigDecimal purchaseAmount;
    private BigDecimal taxAmount;
    private BigDecimal paymentTargetAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
