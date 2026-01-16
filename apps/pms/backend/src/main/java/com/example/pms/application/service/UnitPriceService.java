package com.example.pms.application.service;

import com.example.pms.application.port.in.UnitPriceUseCase;
import com.example.pms.application.port.in.command.CreateUnitPriceCommand;
import com.example.pms.application.port.in.command.UpdateUnitPriceCommand;
import com.example.pms.application.port.out.UnitPriceRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unitprice.UnitPrice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 単価サービス（Application Service）.
 */
@Service
@Transactional
public class UnitPriceService implements UnitPriceUseCase {

    private final UnitPriceRepository unitPriceRepository;

    public UnitPriceService(UnitPriceRepository unitPriceRepository) {
        this.unitPriceRepository = unitPriceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UnitPrice> getUnitPrices(int page, int size, String itemCode) {
        int offset = page * size;
        List<UnitPrice> prices = unitPriceRepository.findWithPagination(itemCode, size, offset);
        long totalElements = unitPriceRepository.count(itemCode);
        return new PageResult<>(prices, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitPrice> getAllUnitPrices() {
        return unitPriceRepository.findAll();
    }

    @Override
    public UnitPrice createUnitPrice(CreateUnitPriceCommand command) {
        UnitPrice unitPrice = UnitPrice.builder()
            .itemCode(command.itemCode())
            .supplierCode(command.supplierCode())
            .effectiveFrom(command.effectiveFrom())
            .effectiveTo(command.effectiveTo())
            .price(command.price())
            .currencyCode(command.currencyCode())
            .build();
        unitPriceRepository.save(unitPrice);
        return unitPrice;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnitPrice> getUnitPrice(String itemCode, String supplierCode, LocalDate effectiveFrom) {
        return unitPriceRepository.findByKey(itemCode, supplierCode, effectiveFrom);
    }

    @Override
    public UnitPrice updateUnitPrice(String itemCode, String supplierCode, LocalDate effectiveFrom, UpdateUnitPriceCommand command) {
        UnitPrice unitPrice = UnitPrice.builder()
            .itemCode(itemCode)
            .supplierCode(supplierCode)
            .effectiveFrom(effectiveFrom)
            .effectiveTo(command.effectiveTo())
            .price(command.price())
            .currencyCode(command.currencyCode())
            .build();
        unitPriceRepository.update(unitPrice);
        return unitPrice;
    }

    @Override
    public void deleteUnitPrice(String itemCode, String supplierCode, LocalDate effectiveFrom) {
        unitPriceRepository.deleteByKey(itemCode, supplierCode, effectiveFrom);
    }
}
