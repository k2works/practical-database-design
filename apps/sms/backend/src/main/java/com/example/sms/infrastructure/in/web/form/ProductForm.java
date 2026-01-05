package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateProductCommand;
import com.example.sms.application.port.in.command.UpdateProductCommand;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品登録・編集フォーム.
 */
@Data
public class ProductForm {

    @NotBlank(message = "商品コードは必須です")
    @Size(max = 20, message = "商品コードは20文字以内で入力してください")
    private String productCode;

    @Size(max = 100, message = "商品正式名は100文字以内で入力してください")
    private String productFullName;

    @NotBlank(message = "商品名は必須です")
    @Size(max = 50, message = "商品名は50文字以内で入力してください")
    private String productName;

    @Size(max = 50, message = "商品名カナは50文字以内で入力してください")
    private String productNameKana;

    @NotNull(message = "商品区分は必須です")
    private ProductCategory productCategory;

    @Size(max = 50, message = "型番は50文字以内で入力してください")
    private String modelNumber;

    @PositiveOrZero(message = "販売単価は0以上で入力してください")
    private BigDecimal sellingPrice;

    @PositiveOrZero(message = "仕入単価は0以上で入力してください")
    private BigDecimal purchasePrice;

    private TaxCategory taxCategory;

    private String classificationCode;

    private Boolean isMiscellaneous;

    private Boolean isInventoryManaged;

    private Boolean isInventoryAllocated;

    private String supplierCode;

    private String supplierBranchNumber;

    /**
     * デフォルトコンストラクタ.
     */
    public ProductForm() {
        this.sellingPrice = BigDecimal.ZERO;
        this.purchasePrice = BigDecimal.ZERO;
        this.taxCategory = TaxCategory.EXCLUSIVE;
        this.isMiscellaneous = false;
        this.isInventoryManaged = true;
        this.isInventoryAllocated = true;
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateProductCommand toCreateCommand() {
        return new CreateProductCommand(
            this.productCode,
            this.productFullName,
            this.productName,
            this.productNameKana,
            this.productCategory,
            this.modelNumber,
            this.sellingPrice,
            this.purchasePrice,
            this.taxCategory,
            this.classificationCode,
            this.isMiscellaneous,
            this.isInventoryManaged,
            this.isInventoryAllocated,
            this.supplierCode,
            this.supplierBranchNumber
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateProductCommand toUpdateCommand() {
        return new UpdateProductCommand(
            this.productFullName,
            this.productName,
            this.productNameKana,
            this.productCategory,
            this.modelNumber,
            this.sellingPrice,
            this.purchasePrice,
            this.taxCategory,
            this.classificationCode,
            this.isMiscellaneous,
            this.isInventoryManaged,
            this.isInventoryAllocated,
            this.supplierCode,
            this.supplierBranchNumber
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param product 商品エンティティ
     * @return フォーム
     */
    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.setProductCode(product.getProductCode());
        form.setProductFullName(product.getProductFullName());
        form.setProductName(product.getProductName());
        form.setProductNameKana(product.getProductNameKana());
        form.setProductCategory(product.getProductCategory());
        form.setModelNumber(product.getModelNumber());
        form.setSellingPrice(product.getSellingPrice());
        form.setPurchasePrice(product.getPurchasePrice());
        form.setTaxCategory(product.getTaxCategory());
        form.setClassificationCode(product.getClassificationCode());
        form.setIsMiscellaneous(product.isMiscellaneous());
        form.setIsInventoryManaged(product.isInventoryManaged());
        form.setIsInventoryAllocated(product.isInventoryAllocated());
        form.setSupplierCode(product.getSupplierCode());
        form.setSupplierBranchNumber(product.getSupplierBranchNumber());
        return form;
    }
}
