package com.example.sms.application.port.out;

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

    void update(Product product);

    void deleteByCode(String productCode);

    void deleteAll();
}
