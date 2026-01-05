package com.example.sms.application.port.out;

import com.example.sms.domain.model.product.CustomerProductPrice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 顧客別販売単価リポジトリ（Output Port）.
 */
public interface CustomerProductPriceRepository {

    void save(CustomerProductPrice price);

    Optional<CustomerProductPrice> findByKey(String productCode, String partnerCode, LocalDate startDate);

    List<CustomerProductPrice> findByProduct(String productCode);

    List<CustomerProductPrice> findByPartner(String partnerCode);

    List<CustomerProductPrice> findAll();

    void update(CustomerProductPrice price);

    void deleteByKey(String productCode, String partnerCode, LocalDate startDate);

    void deleteByProduct(String productCode);

    void deleteAll();
}
