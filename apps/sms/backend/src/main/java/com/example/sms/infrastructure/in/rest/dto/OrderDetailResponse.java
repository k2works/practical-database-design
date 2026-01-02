package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.SalesOrderDetail;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 受注明細レスポンス DTO.
 */
public record OrderDetailResponse(
    Integer id,
    Integer lineNumber,
    String productCode,
    String productName,
    BigDecimal orderQuantity,
    BigDecimal allocatedQuantity,
    BigDecimal shippedQuantity,
    BigDecimal remainingQuantity,
    String unit,
    BigDecimal unitPrice,
    BigDecimal amount,
    TaxCategory taxCategory,
    BigDecimal taxRate,
    BigDecimal taxAmount,
    String warehouseCode,
    LocalDate requestedDeliveryDate,
    String remarks,
    Integer version
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param detail 受注明細ドメインモデル
     * @return 受注明細レスポンス DTO
     */
    public static OrderDetailResponse from(SalesOrderDetail detail) {
        return new OrderDetailResponse(
            detail.getId(),
            detail.getLineNumber(),
            detail.getProductCode(),
            detail.getProductName(),
            detail.getOrderQuantity(),
            detail.getAllocatedQuantity(),
            detail.getShippedQuantity(),
            detail.getRemainingQuantity(),
            detail.getUnit(),
            detail.getUnitPrice(),
            detail.getAmount(),
            detail.getTaxCategory(),
            detail.getTaxRate(),
            detail.getTaxAmount(),
            detail.getWarehouseCode(),
            detail.getRequestedDeliveryDate(),
            detail.getRemarks(),
            detail.getVersion()
        );
    }
}
