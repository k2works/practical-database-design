package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.SupplyDetailRepository;
import com.example.pms.domain.model.subcontract.SupplyDetail;
import com.example.pms.infrastructure.out.persistence.mapper.SupplyDetailMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 支給明細データリポジトリ実装
 */
@Repository
public class SupplyDetailRepositoryImpl implements SupplyDetailRepository {

    private final SupplyDetailMapper supplyDetailMapper;

    public SupplyDetailRepositoryImpl(SupplyDetailMapper supplyDetailMapper) {
        this.supplyDetailMapper = supplyDetailMapper;
    }

    @Override
    public void save(SupplyDetail supplyDetail) {
        supplyDetailMapper.insert(supplyDetail);
    }

    @Override
    public Optional<SupplyDetail> findById(Integer id) {
        return Optional.ofNullable(supplyDetailMapper.findById(id));
    }

    @Override
    public Optional<SupplyDetail> findBySupplyNumberAndLineNumber(
            String supplyNumber, Integer lineNumber) {
        return Optional.ofNullable(supplyDetailMapper.findBySupplyNumberAndLineNumber(supplyNumber, lineNumber));
    }

    @Override
    public List<SupplyDetail> findBySupplyNumber(String supplyNumber) {
        return supplyDetailMapper.findBySupplyNumber(supplyNumber);
    }

    @Override
    public List<SupplyDetail> findAll() {
        return supplyDetailMapper.findAll();
    }

    @Override
    public void deleteAll() {
        supplyDetailMapper.deleteAll();
    }
}
