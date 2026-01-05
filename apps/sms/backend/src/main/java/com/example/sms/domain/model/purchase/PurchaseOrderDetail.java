package com.example.sms.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 発注明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class PurchaseOrderDetail {
    private Integer id;
    private Integer purchaseOrderId;
    private Integer lineNumber;
    private String productCode;
    private BigDecimal orderQuantity;
    private BigDecimal unitPrice;
    private BigDecimal orderAmount;
    private LocalDate expectedDeliveryDate;
    @Builder.Default
    private BigDecimal receivedQuantity = BigDecimal.ZERO;
    private BigDecimal remainingQuantity;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 発注金額を計算する.
     */
    public void calculateOrderAmount() {
        this.orderAmount = this.orderQuantity.multiply(this.unitPrice);
    }

    /**
     * 残数量を計算する.
     */
    public void calculateRemainingQuantity() {
        this.remainingQuantity = this.orderQuantity
                .subtract(this.receivedQuantity != null ? this.receivedQuantity : BigDecimal.ZERO);
    }

    /**
     * 入荷可能数量かどうか.
     *
     * @param quantity 入荷数量
     * @return 入荷可能な場合true
     */
    public boolean canReceive(BigDecimal quantity) {
        return quantity.compareTo(BigDecimal.ZERO) > 0
                && quantity.compareTo(this.remainingQuantity) <= 0;
    }
}
