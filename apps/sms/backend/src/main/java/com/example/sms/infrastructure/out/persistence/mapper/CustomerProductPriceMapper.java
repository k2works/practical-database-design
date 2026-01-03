package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.product.CustomerProductPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 顧客別販売単価マッパー.
 */
@Mapper
public interface CustomerProductPriceMapper {

    void insert(CustomerProductPrice price);

    Optional<CustomerProductPrice> findByKey(@Param("productCode") String productCode,
                                              @Param("partnerCode") String partnerCode,
                                              @Param("startDate") LocalDate startDate);

    List<CustomerProductPrice> findByProduct(String productCode);

    List<CustomerProductPrice> findByPartner(String partnerCode);

    List<CustomerProductPrice> findAll();

    void update(CustomerProductPrice price);

    void deleteByKey(@Param("productCode") String productCode,
                     @Param("partnerCode") String partnerCode,
                     @Param("startDate") LocalDate startDate);

    void deleteByProduct(String productCode);

    void deleteAll();
}
