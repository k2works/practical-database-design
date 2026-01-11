package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.SupplyRepository;
import com.example.pms.domain.model.subcontract.Supply;
import com.example.pms.infrastructure.out.persistence.mapper.SupplyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 支給データリポジトリ実装
 */
@Repository
public class SupplyRepositoryImpl implements SupplyRepository {

    private final SupplyMapper supplyMapper;

    public SupplyRepositoryImpl(SupplyMapper supplyMapper) {
        this.supplyMapper = supplyMapper;
    }

    @Override
    public void save(Supply supply) {
        supplyMapper.insert(supply);
    }

    @Override
    public Optional<Supply> findById(Integer id) {
        return Optional.ofNullable(supplyMapper.findById(id));
    }

    @Override
    public Optional<Supply> findBySupplyNumber(String supplyNumber) {
        return Optional.ofNullable(supplyMapper.findBySupplyNumber(supplyNumber));
    }

    @Override
    public List<Supply> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return supplyMapper.findByPurchaseOrderNumber(purchaseOrderNumber);
    }

    @Override
    public List<Supply> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber) {
        return supplyMapper.findByPurchaseOrderNumberAndLineNumber(purchaseOrderNumber, lineNumber);
    }

    @Override
    public List<Supply> findBySupplierCode(String supplierCode) {
        return supplyMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<Supply> findAll() {
        return supplyMapper.findAll();
    }

    @Override
    public void deleteAll() {
        supplyMapper.deleteAll();
    }
}
