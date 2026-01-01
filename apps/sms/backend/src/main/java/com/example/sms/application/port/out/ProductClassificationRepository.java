package com.example.sms.application.port.out;

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

    void update(ProductClassification classification);

    void deleteAll();
}
