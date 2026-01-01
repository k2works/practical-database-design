package com.example.sms.domain.model.sales;

import com.example.sms.domain.model.product.TaxCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 受注明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class SalesOrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer lineNumber;
    private String productCode;
    private String productName;
    private BigDecimal orderQuantity;
    @Builder.Default
    private BigDecimal allocatedQuantity = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal shippedQuantity = BigDecimal.ZERO;
    private BigDecimal remainingQuantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    @Builder.Default
    private TaxCategory taxCategory = TaxCategory.EXCLUSIVE;
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("10.00");
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private String warehouseCode;
    private LocalDate requestedDeliveryDate;
    private String remarks;

    /** 楽観ロック用バージョン. */
    @Builder.Default
    private Integer version = 1;
}
