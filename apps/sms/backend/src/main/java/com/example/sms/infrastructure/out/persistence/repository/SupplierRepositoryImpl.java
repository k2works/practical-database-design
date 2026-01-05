package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.SupplierRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Supplier;
import com.example.sms.infrastructure.out.persistence.mapper.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 仕入先リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierMapper supplierMapper;

    @Override
    public void save(Supplier supplier) {
        supplierMapper.insert(supplier);
    }

    @Override
    public Optional<Supplier> findByCodeAndBranch(String supplierCode, String branchNumber) {
        return supplierMapper.findByCodeAndBranch(supplierCode, branchNumber);
    }

    @Override
    public List<Supplier> findByCode(String supplierCode) {
        return supplierMapper.findByCode(supplierCode);
    }

    @Override
    public List<Supplier> findAll() {
        return supplierMapper.findAll();
    }

    @Override
    public PageResult<Supplier> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Supplier> suppliers = supplierMapper.findWithPagination(offset, size, keyword);
        long totalElements = supplierMapper.count(keyword);
        return new PageResult<>(suppliers, page, size, totalElements);
    }

    @Override
    public void update(Supplier supplier) {
        supplierMapper.update(supplier);
    }

    @Override
    public void deleteByCodeAndBranch(String supplierCode, String branchNumber) {
        supplierMapper.deleteByCodeAndBranch(supplierCode, branchNumber);
    }

    @Override
    public void deleteAll() {
        supplierMapper.deleteAll();
    }
}
