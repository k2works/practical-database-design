package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.product.ProductClassification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * ページネーション付きで商品分類を検索.
     */
    List<ProductClassification> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する商品分類の件数を取得.
     */
    long count(@Param("keyword") String keyword);

    void update(ProductClassification classification);

    void deleteAll();
}
