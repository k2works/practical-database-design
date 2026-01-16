package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.UnitPriceRepository;
import com.example.pms.domain.model.unitprice.UnitPrice;
import com.example.pms.infrastructure.out.persistence.mapper.UnitPriceMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UnitPriceRepositoryImpl implements UnitPriceRepository {

    private final UnitPriceMapper unitPriceMapper;

    public UnitPriceRepositoryImpl(UnitPriceMapper unitPriceMapper) {
        this.unitPriceMapper = unitPriceMapper;
    }

    @Override
    public void save(UnitPrice unitPrice) {
        unitPriceMapper.insert(unitPrice);
    }

    @Override
    public Optional<UnitPrice> findByItemCodeAndSupplierCode(String itemCode, String supplierCode) {
        return unitPriceMapper.findByItemCodeAndSupplierCode(itemCode, supplierCode);
    }

    @Override
    public Optional<UnitPrice> findByItemCodeAndSupplierCodeAndDate(String itemCode, String supplierCode, LocalDate baseDate) {
        return unitPriceMapper.findByItemCodeAndSupplierCodeAndDate(itemCode, supplierCode, baseDate);
    }

    @Override
    public List<UnitPrice> findByItemCode(String itemCode) {
        return unitPriceMapper.findByItemCode(itemCode);
    }

    @Override
    public List<UnitPrice> findAll() {
        return unitPriceMapper.findAll();
    }

    @Override
    public void update(UnitPrice unitPrice) {
        unitPriceMapper.update(unitPrice);
    }

    @Override
    public void deleteAll() {
        unitPriceMapper.deleteAll();
    }

    @Override
    public List<UnitPrice> findWithPagination(String itemCode, int limit, int offset) {
        return unitPriceMapper.findWithPagination(itemCode, limit, offset);
    }

    @Override
    public long count(String itemCode) {
        return unitPriceMapper.count(itemCode);
    }

    @Override
    public Optional<UnitPrice> findByKey(String itemCode, String supplierCode, LocalDate effectiveFrom) {
        return unitPriceMapper.findByKey(itemCode, supplierCode, effectiveFrom);
    }

    @Override
    public void deleteByKey(String itemCode, String supplierCode, LocalDate effectiveFrom) {
        unitPriceMapper.deleteByKey(itemCode, supplierCode, effectiveFrom);
    }
}
