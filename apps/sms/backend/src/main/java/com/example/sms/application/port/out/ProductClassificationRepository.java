package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.ProductClassification;

import java.util.List;
import java.util.Optional;

/**
 * 商品分類リポジトリ（Output Port）.
 */
public interface ProductClassificationRepository {

    void save(ProductClassification classification);

    Optional<ProductClassification> findByCode(String classificationCode);

    List<ProductClassification> findAll();

    List<ProductClassification> findChildren(String parentPath);

    /**
     * ページネーション付きで商品分類を検索.
     */
    PageResult<ProductClassification> findWithPagination(int page, int size, String keyword);

    void update(ProductClassification classification);

    void deleteAll();
}
