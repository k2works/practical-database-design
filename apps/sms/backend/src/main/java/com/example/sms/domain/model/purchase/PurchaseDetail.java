package com.example.sms.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 仕入明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetail {
    private Integer id;
    private Integer purchaseId;
    private Integer lineNumber;
    private String productCode;
    private BigDecimal purchaseQuantity;
    private BigDecimal unitPrice;
    private BigDecimal purchaseAmount;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 仕入金額を計算する.
     */
    public void calculatePurchaseAmount() {
        this.purchaseAmount = this.purchaseQuantity.multiply(this.unitPrice);
    }
}
