package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.common.ProductHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 商品マスタ履歴マッパー.
 */
@Mapper
public interface ProductHistoryMapper {

    void insert(ProductHistory history);

    Optional<ProductHistory> findById(@Param("id") Integer id);

    List<ProductHistory> findByProductCode(@Param("productCode") String productCode);

    Optional<ProductHistory> findByProductCodeAndValidDate(
            @Param("productCode") String productCode,
            @Param("targetDate") LocalDate targetDate);

    List<ProductHistory> findAll();

    void update(ProductHistory history);

    void deleteById(@Param("id") Integer id);

    void deleteByProductCode(@Param("productCode") String productCode);

    void deleteAll();
}
