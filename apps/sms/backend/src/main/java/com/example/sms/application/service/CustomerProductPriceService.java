package com.example.sms.application.service;

import com.example.sms.application.port.in.CustomerProductPriceUseCase;
import com.example.sms.application.port.out.CustomerProductPriceRepository;
import com.example.sms.domain.exception.CustomerProductPriceNotFoundException;
import com.example.sms.domain.model.product.CustomerProductPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 顧客別販売単価サービス.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerProductPriceService implements CustomerProductPriceUseCase {

    private final CustomerProductPriceRepository customerProductPriceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerProductPrice> getAllPrices() {
        return customerProductPriceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProductPrice getPrice(String productCode, String partnerCode, LocalDate startDate) {
        return customerProductPriceRepository.findByKey(productCode, partnerCode, startDate)
                .orElseThrow(() -> new CustomerProductPriceNotFoundException(
                        productCode, partnerCode, startDate.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerProductPrice> getPricesByProduct(String productCode) {
        return customerProductPriceRepository.findByProduct(productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerProductPrice> getPricesByPartner(String partnerCode) {
        return customerProductPriceRepository.findByPartner(partnerCode);
    }

    @Override
    public void createPrice(CustomerProductPrice price) {
        customerProductPriceRepository.save(price);
    }

    @Override
    public void updatePrice(CustomerProductPrice price) {
        customerProductPriceRepository.update(price);
    }

    @Override
    public void deletePrice(String productCode, String partnerCode, LocalDate startDate) {
        customerProductPriceRepository.deleteByKey(productCode, partnerCode, startDate);
    }
}
