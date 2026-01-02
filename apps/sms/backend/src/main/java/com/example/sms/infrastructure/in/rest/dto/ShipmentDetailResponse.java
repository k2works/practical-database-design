package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.shipping.ShipmentDetail;

import java.math.BigDecimal;

/**
 * 出荷明細レスポンス DTO.
 */
public record ShipmentDetailResponse(
    Integer id,
    Integer lineNumber,
    Integer orderDetailId,
    String productCode,
    String productName,
    BigDecimal shippedQuantity,
    String unit,
    BigDecimal unitPrice,
    BigDecimal amount,
    TaxCategory taxCategory,
    BigDecimal taxRate,
    BigDecimal taxAmount,
    String warehouseCode,
    String remarks
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param detail 出荷明細ドメインモデル
     * @return 出荷明細レスポンス DTO
     */
    public static ShipmentDetailResponse from(ShipmentDetail detail) {
        return new ShipmentDetailResponse(
            detail.getId(),
            detail.getLineNumber(),
            detail.getOrderDetailId(),
            detail.getProductCode(),
            detail.getProductName(),
            detail.getShippedQuantity(),
            detail.getUnit(),
            detail.getUnitPrice(),
            detail.getAmount(),
            detail.getTaxCategory(),
            detail.getTaxRate(),
            detail.getTaxAmount(),
            detail.getWarehouseCode(),
            detail.getRemarks()
        );
    }
}
