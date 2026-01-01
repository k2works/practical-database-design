package com.example.sms.domain.model.sales;

import com.example.sms.domain.model.product.TaxCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 売上明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class SalesDetail {
    private Integer id;
    private Integer salesId;
    private Integer lineNumber;
    private Integer orderDetailId;
    private Integer shipmentDetailId;
    private String productCode;
    private String productName;
    private BigDecimal salesQuantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    @Builder.Default
    private TaxCategory taxCategory = TaxCategory.EXCLUSIVE;
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("10.00");
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private String remarks;
}
