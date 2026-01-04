package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateProductCommand;
import com.example.sms.application.port.in.command.UpdateProductCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;

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
     * ページネーション付きで商品を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param category カテゴリ（null可）
     * @param keyword キーワード（null可）
     * @return ページ結果
     */
    PageResult<Product> getProducts(int page, int size, ProductCategory category, String keyword);

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

    /**
     * 分類コードで商品を取得する.
     *
     * @param classificationCode 分類コード
     * @return 商品リスト
     */
    List<Product> getProductsByClassification(String classificationCode);
}
