package com.example.sms.application.port.in;

import com.example.sms.domain.model.product.CustomerProductPrice;

import java.time.LocalDate;
import java.util.List;

/**
 * 顧客別販売単価ユースケース（Input Port）.
 */
public interface CustomerProductPriceUseCase {

    List<CustomerProductPrice> getAllPrices();

    CustomerProductPrice getPrice(String productCode, String partnerCode, LocalDate startDate);

    List<CustomerProductPrice> getPricesByProduct(String productCode);

    List<CustomerProductPrice> getPricesByPartner(String partnerCode);

    void createPrice(CustomerProductPrice price);

    void updatePrice(CustomerProductPrice price);

    void deletePrice(String productCode, String partnerCode, LocalDate startDate);
}
