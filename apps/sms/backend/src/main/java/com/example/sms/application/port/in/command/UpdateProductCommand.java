package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;

import java.math.BigDecimal;

/**
 * 商品更新コマンド.
 */
public record UpdateProductCommand(
    String productFullName,
    String productName,
    String productNameKana,
    ProductCategory productCategory,
    String modelNumber,
    BigDecimal sellingPrice,
    BigDecimal purchasePrice,
    TaxCategory taxCategory,
    String classificationCode,
    Boolean isMiscellaneous,
    Boolean isInventoryManaged,
    Boolean isInventoryAllocated,
    String supplierCode,
    String supplierBranchNumber
) {
}
