package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.ProductHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 商品マスタ履歴リポジトリ（Output Port）.
 */
public interface ProductHistoryRepository {

    void save(ProductHistory history);

    Optional<ProductHistory> findById(Integer id);

    List<ProductHistory> findByProductCode(String productCode);

    Optional<ProductHistory> findByProductCodeAndValidDate(String productCode, LocalDate targetDate);

    List<ProductHistory> findAll();

    void update(ProductHistory history);

    void deleteById(Integer id);

    void deleteByProductCode(String productCode);

    void deleteAll();
}
