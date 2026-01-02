package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * 商品更新リクエスト DTO.
 */
public record UpdateProductRequest(
    String productFullName,

    @NotBlank(message = "商品名は必須です")
    String productName,

    String productNameKana,

    @NotNull(message = "商品区分は必須です")
    ProductCategory productCategory,

    String modelNumber,

    @NotNull(message = "販売単価は必須です")
    @PositiveOrZero(message = "販売単価は0以上である必要があります")
    BigDecimal sellingPrice,

    @NotNull(message = "仕入単価は必須です")
    @PositiveOrZero(message = "仕入単価は0以上である必要があります")
    BigDecimal purchasePrice,

    @NotNull(message = "税区分は必須です")
    TaxCategory taxCategory,

    String classificationCode,

    Boolean isMiscellaneous,

    Boolean isInventoryManaged,

    Boolean isInventoryAllocated,

    String supplierCode,

    String supplierBranchNumber
) {
}
