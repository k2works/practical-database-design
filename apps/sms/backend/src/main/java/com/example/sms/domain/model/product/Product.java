package com.example.sms.domain.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Product {
    private String productCode;
    private String productFullName;
    private String productName;
    private String productNameKana;
    private ProductCategory productCategory;
    private String modelNumber;
    @Builder.Default
    private BigDecimal sellingPrice = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal purchasePrice = BigDecimal.ZERO;
    private TaxCategory taxCategory;
    private String classificationCode;
    @Builder.Default
    private boolean isMiscellaneous = false;
    @Builder.Default
    private boolean isInventoryManaged = true;
    @Builder.Default
    private boolean isInventoryAllocated = true;
    private String supplierCode;
    private String supplierBranchNumber;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
