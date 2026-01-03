package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.CustomerProductPriceRepository;
import com.example.sms.domain.model.product.CustomerProductPrice;
import com.example.sms.infrastructure.out.persistence.mapper.CustomerProductPriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 顧客別販売単価リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class CustomerProductPriceRepositoryImpl implements CustomerProductPriceRepository {

    private final CustomerProductPriceMapper customerProductPriceMapper;

    @Override
    public void save(CustomerProductPrice price) {
        customerProductPriceMapper.insert(price);
    }

    @Override
    public Optional<CustomerProductPrice> findByKey(String productCode, String partnerCode, LocalDate startDate) {
        return customerProductPriceMapper.findByKey(productCode, partnerCode, startDate);
    }

    @Override
    public List<CustomerProductPrice> findByProduct(String productCode) {
        return customerProductPriceMapper.findByProduct(productCode);
    }

    @Override
    public List<CustomerProductPrice> findByPartner(String partnerCode) {
        return customerProductPriceMapper.findByPartner(partnerCode);
    }

    @Override
    public List<CustomerProductPrice> findAll() {
        return customerProductPriceMapper.findAll();
    }

    @Override
    public void update(CustomerProductPrice price) {
        customerProductPriceMapper.update(price);
    }

    @Override
    public void deleteByKey(String productCode, String partnerCode, LocalDate startDate) {
        customerProductPriceMapper.deleteByKey(productCode, partnerCode, startDate);
    }

    @Override
    public void deleteByProduct(String productCode) {
        customerProductPriceMapper.deleteByProduct(productCode);
    }

    @Override
    public void deleteAll() {
        customerProductPriceMapper.deleteAll();
    }
}
