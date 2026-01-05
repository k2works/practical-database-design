package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;

import java.util.List;
import java.util.Optional;

/**
 * 商品リポジトリ（Output Port）.
 */
public interface ProductRepository {

    void save(Product product);

    Optional<Product> findByCode(String productCode);

    List<Product> findAll();

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByClassificationCode(String classificationCode);

    /**
     * ページネーション付きで商品を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param category カテゴリ（null可）
     * @param keyword キーワード（null可）
     * @return ページ結果
     */
    PageResult<Product> findWithPagination(int page, int size, ProductCategory category, String keyword);

    void update(Product product);

    void deleteByCode(String productCode);

    void deleteAll();
}
