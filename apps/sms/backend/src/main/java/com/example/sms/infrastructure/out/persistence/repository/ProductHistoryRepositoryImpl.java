package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ProductHistoryRepository;
import com.example.sms.domain.model.common.ProductHistory;
import com.example.sms.infrastructure.out.persistence.mapper.ProductHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 商品マスタ履歴リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ProductHistoryRepositoryImpl implements ProductHistoryRepository {

    private final ProductHistoryMapper productHistoryMapper;

    @Override
    public void save(ProductHistory history) {
        productHistoryMapper.insert(history);
    }

    @Override
    public Optional<ProductHistory> findById(Integer id) {
        return productHistoryMapper.findById(id);
    }

    @Override
    public List<ProductHistory> findByProductCode(String productCode) {
        return productHistoryMapper.findByProductCode(productCode);
    }

    @Override
    public Optional<ProductHistory> findByProductCodeAndValidDate(String productCode, LocalDate targetDate) {
        return productHistoryMapper.findByProductCodeAndValidDate(productCode, targetDate);
    }

    @Override
    public List<ProductHistory> findAll() {
        return productHistoryMapper.findAll();
    }

    @Override
    public void update(ProductHistory history) {
        productHistoryMapper.update(history);
    }

    @Override
    public void deleteById(Integer id) {
        productHistoryMapper.deleteById(id);
    }

    @Override
    public void deleteByProductCode(String productCode) {
        productHistoryMapper.deleteByProductCode(productCode);
    }

    @Override
    public void deleteAll() {
        productHistoryMapper.deleteAll();
    }
}
