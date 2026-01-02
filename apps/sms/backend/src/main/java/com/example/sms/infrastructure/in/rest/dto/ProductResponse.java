package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;

import java.math.BigDecimal;

/**
 * 商品レスポンス DTO.
 */
public record ProductResponse(
    String productCode,
    String productFullName,
    String productName,
    String productNameKana,
    ProductCategory productCategory,
    String modelNumber,
    BigDecimal sellingPrice,
    BigDecimal purchasePrice,
    TaxCategory taxCategory,
    String classificationCode,
    boolean isMiscellaneous,
    boolean isInventoryManaged,
    boolean isInventoryAllocated,
    String supplierCode,
    String supplierBranchNumber
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param product 商品ドメインモデル
     * @return 商品レスポンス DTO
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getProductCode(),
            product.getProductFullName(),
            product.getProductName(),
            product.getProductNameKana(),
            product.getProductCategory(),
            product.getModelNumber(),
            product.getSellingPrice(),
            product.getPurchasePrice(),
            product.getTaxCategory(),
            product.getClassificationCode(),
            product.isMiscellaneous(),
            product.isInventoryManaged(),
            product.isInventoryAllocated(),
            product.getSupplierCode(),
            product.getSupplierBranchNumber()
        );
    }
}
