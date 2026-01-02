package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateProductCommand;
import com.example.sms.application.port.in.command.UpdateProductCommand;
import com.example.sms.domain.model.product.Product;

import java.util.List;

/**
 * 商品ユースケース（Input Port）.
 */
public interface ProductUseCase {

    /**
     * 商品を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された商品
     */
    Product createProduct(CreateProductCommand command);

    /**
     * 商品を更新する.
     *
     * @param productCode 商品コード
     * @param command 更新コマンド
     * @return 更新された商品
     */
    Product updateProduct(String productCode, UpdateProductCommand command);

    /**
     * 全商品を取得する.
     *
     * @return 商品リスト
     */
    List<Product> getAllProducts();

    /**
     * 商品コードで商品を取得する.
     *
     * @param productCode 商品コード
     * @return 商品
     */
    Product getProductByCode(String productCode);

    /**
     * 商品を削除する.
     *
     * @param productCode 商品コード
     */
    void deleteProduct(String productCode);
}
