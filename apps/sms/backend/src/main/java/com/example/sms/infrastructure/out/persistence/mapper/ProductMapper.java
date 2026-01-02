package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.product.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 商品マッパー.
 */
@Mapper
public interface ProductMapper {

    void insert(Product product);

    Optional<Product> findByCode(String productCode);

    List<Product> findAll();

    List<Product> findByCategory(String category);

    List<Product> findByClassificationCode(String classificationCode);

    void update(Product product);

    void deleteByCode(String productCode);

    void deleteAll();
}
