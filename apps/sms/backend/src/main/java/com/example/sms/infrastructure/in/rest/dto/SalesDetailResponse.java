package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.SalesDetail;

import java.math.BigDecimal;

/**
 * 売上明細レスポンス DTO.
 */
public record SalesDetailResponse(
    Integer id,
    Integer lineNumber,
    Integer orderDetailId,
    Integer shipmentDetailId,
    String productCode,
    String productName,
    BigDecimal salesQuantity,
    String unit,
    BigDecimal unitPrice,
    BigDecimal amount,
    TaxCategory taxCategory,
    BigDecimal taxRate,
    BigDecimal taxAmount,
    String remarks
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param detail 売上明細ドメインモデル
     * @return 売上明細レスポンス DTO
     */
    public static SalesDetailResponse from(SalesDetail detail) {
        return new SalesDetailResponse(
            detail.getId(),
            detail.getLineNumber(),
            detail.getOrderDetailId(),
            detail.getShipmentDetailId(),
            detail.getProductCode(),
            detail.getProductName(),
            detail.getSalesQuantity(),
            detail.getUnit(),
            detail.getUnitPrice(),
            detail.getAmount(),
            detail.getTaxCategory(),
            detail.getTaxRate(),
            detail.getTaxAmount(),
            detail.getRemarks()
        );
    }
}
