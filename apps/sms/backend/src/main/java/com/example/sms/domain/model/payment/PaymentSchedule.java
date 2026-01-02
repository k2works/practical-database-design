package com.example.sms.domain.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支払予定データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSchedule {
    private Integer id;
    private String supplierCode;
    private LocalDate paymentDueDate;
    private BigDecimal scheduledAmount;
    private PaymentMethod paymentMethod;
    @Builder.Default
    private Boolean paidFlag = false;
    private Integer paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 支払済みとしてマーク.
     */
    public void markAsPaid(Integer paymentId) {
        this.paidFlag = true;
        this.paymentId = paymentId;
    }

    /**
     * 支払期限が過ぎているかどうか.
     */
    public boolean isOverdue() {
        return !paidFlag && LocalDate.now().isAfter(paymentDueDate);
    }
}
