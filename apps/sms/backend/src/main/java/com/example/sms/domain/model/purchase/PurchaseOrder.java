package com.example.sms.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 発注エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class PurchaseOrder {
    private Integer id;
    private String purchaseOrderNumber;
    private String supplierCode;
    @Builder.Default
    private String supplierBranchNumber = "00";
    private LocalDate orderDate;
    private LocalDate desiredDeliveryDate;
    @Builder.Default
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    private String purchaserCode;
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /** 楽観ロック用バージョン. */
    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<PurchaseOrderDetail> details = new ArrayList<>();

    /**
     * 発注合計金額を再計算する.
     */
    public void recalculateTotalAmount() {
        if (details != null && !details.isEmpty()) {
            this.totalAmount = details.stream()
                    .map(PurchaseOrderDetail::getOrderAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 消費税10%
            this.taxAmount = this.totalAmount.multiply(new BigDecimal("0.10"));
        }
    }

    /**
     * 全明細が入荷完了かどうか.
     *
     * @return 全明細が入荷完了の場合true
     */
    public boolean isAllReceived() {
        if (details == null || details.isEmpty()) {
            return false;
        }
        return details.stream()
                .allMatch(d -> d.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0);
    }

    /**
     * 一部入荷があるかどうか.
     *
     * @return 一部入荷がある場合true
     */
    public boolean hasPartialReceipt() {
        if (details == null || details.isEmpty()) {
            return false;
        }
        return details.stream()
                .anyMatch(d -> d.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0
                        && d.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0);
    }
}
