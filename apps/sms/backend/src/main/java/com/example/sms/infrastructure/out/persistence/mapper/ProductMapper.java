package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.product.Product;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

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

    /**
     * ページネーション付きで商品を取得.
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param category カテゴリ（null可）
     * @param keyword キーワード（null可）
     * @return 商品リスト
     */
    List<Product> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("category") String category,
            @Param("keyword") String keyword);

    /**
     * 商品の総件数を取得.
     *
     * @param category カテゴリ（null可）
     * @param keyword キーワード（null可）
     * @return 総件数
     */
    long count(@Param("category") String category, @Param("keyword") String keyword);

    void update(Product product);

    void deleteByCode(String productCode);

    void deleteAll();
}
