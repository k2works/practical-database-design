package com.example.sms.domain.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 買掛金残高データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayableBalance {
    private Integer id;
    private String supplierCode;
    private LocalDate yearMonth;
    private BigDecimal previousBalance;
    private BigDecimal currentPurchaseAmount;
    private BigDecimal currentPaymentAmount;
    private BigDecimal currentBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Integer version = 1;

    /**
     * 残高を計算.
     */
    public BigDecimal calculateBalance() {
        BigDecimal prev = previousBalance != null ? previousBalance : BigDecimal.ZERO;
        BigDecimal purchase = currentPurchaseAmount != null ? currentPurchaseAmount : BigDecimal.ZERO;
        BigDecimal payment = currentPaymentAmount != null ? currentPaymentAmount : BigDecimal.ZERO;
        return prev.add(purchase).subtract(payment);
    }

    /**
     * 仕入を加算.
     */
    public void addPurchase(BigDecimal amount) {
        if (this.currentPurchaseAmount == null) {
            this.currentPurchaseAmount = BigDecimal.ZERO;
        }
        this.currentPurchaseAmount = this.currentPurchaseAmount.add(amount);
        this.currentBalance = calculateBalance();
    }

    /**
     * 支払を加算.
     */
    public void addPayment(BigDecimal amount) {
        if (this.currentPaymentAmount == null) {
            this.currentPaymentAmount = BigDecimal.ZERO;
        }
        this.currentPaymentAmount = this.currentPaymentAmount.add(amount);
        this.currentBalance = calculateBalance();
    }
}
