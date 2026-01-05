package com.example.sms.domain.model.sales;

import com.example.sms.domain.model.product.TaxCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 見積明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDetail {
    private Integer id;
    private Integer quotationId;
    private Integer lineNumber;
    private String productCode;
    private String productName;
    private BigDecimal quantity;
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
