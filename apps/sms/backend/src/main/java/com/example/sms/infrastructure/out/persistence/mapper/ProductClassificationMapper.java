package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.product.ProductClassification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 商品分類マッパー.
 */
@Mapper
public interface ProductClassificationMapper {

    void insert(ProductClassification classification);

    Optional<ProductClassification> findByCode(String classificationCode);

    List<ProductClassification> findAll();

    List<ProductClassification> findByPathPrefix(String pathPrefix);

    void update(ProductClassification classification);

    void deleteAll();
}
