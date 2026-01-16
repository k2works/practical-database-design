package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.infrastructure.out.persistence.mapper.SupplierMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 取引先マスタリポジトリ実装.
 */
@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierMapper supplierMapper;

    public SupplierRepositoryImpl(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    @Override
    public void save(Supplier supplier) {
        supplierMapper.insert(supplier);
    }

    @Override
    public Optional<Supplier> findBySupplierCode(String supplierCode) {
        return supplierMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public Optional<Supplier> findBySupplierCodeAndDate(String supplierCode, LocalDate baseDate) {
        return supplierMapper.findBySupplierCodeAndDate(supplierCode, baseDate);
    }

    @Override
    public List<Supplier> findAll() {
        return supplierMapper.findAll();
    }

    @Override
    public void update(Supplier supplier) {
        supplierMapper.update(supplier);
    }

    @Override
    public void deleteAll() {
        supplierMapper.deleteAll();
    }

    @Override
    public List<Supplier> findWithPagination(String keyword, int limit, int offset) {
        return supplierMapper.findWithPagination(keyword, limit, offset);
    }

    @Override
    public long count(String keyword) {
        return supplierMapper.count(keyword);
    }

    @Override
    public Optional<Supplier> findBySupplierCodeAndEffectiveFrom(String supplierCode, LocalDate effectiveFrom) {
        return supplierMapper.findBySupplierCodeAndEffectiveFrom(supplierCode, effectiveFrom);
    }

    @Override
    public void deleteBySupplierCodeAndEffectiveFrom(String supplierCode, LocalDate effectiveFrom) {
        supplierMapper.deleteBySupplierCodeAndEffectiveFrom(supplierCode, effectiveFrom);
    }
}
