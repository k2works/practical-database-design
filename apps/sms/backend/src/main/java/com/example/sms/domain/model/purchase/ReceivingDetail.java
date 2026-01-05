package com.example.sms.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入荷明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class ReceivingDetail {
    private Integer id;
    private Integer receivingId;
    private Integer lineNumber;
    private Integer purchaseOrderDetailId;
    private String productCode;
    private BigDecimal receivingQuantity;
    @Builder.Default
    private BigDecimal inspectedQuantity = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal acceptedQuantity = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal rejectedQuantity = BigDecimal.ZERO;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 検品を完了する.
     *
     * @param acceptedQty 合格数量
     * @param rejectedQty 不合格数量
     */
    public void completeInspection(BigDecimal acceptedQty, BigDecimal rejectedQty) {
        this.inspectedQuantity = acceptedQty.add(rejectedQty);
        this.acceptedQuantity = acceptedQty;
        this.rejectedQuantity = rejectedQty;
    }
}
