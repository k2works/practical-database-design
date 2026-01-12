package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.CostVarianceRepository;
import com.example.pms.domain.model.cost.CostVariance;
import com.example.pms.infrastructure.out.persistence.mapper.CostVarianceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 原価差異データリポジトリ実装.
 */
@Repository
public class CostVarianceRepositoryImpl implements CostVarianceRepository {

    private final CostVarianceMapper mapper;

    public CostVarianceRepositoryImpl(CostVarianceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(CostVariance variance) {
        mapper.insert(variance);
    }

    @Override
    public Optional<CostVariance> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public Optional<CostVariance> findByWorkOrderNumber(String workOrderNumber) {
        return Optional.ofNullable(mapper.findByWorkOrderNumber(workOrderNumber));
    }

    @Override
    public List<CostVariance> findByItemCode(String itemCode) {
        return mapper.findByItemCode(itemCode);
    }

    @Override
    public List<CostVariance> findAll() {
        return mapper.findAll();
    }

    @Override
    public boolean existsByWorkOrderNumber(String workOrderNumber) {
        return mapper.existsByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public void deleteByWorkOrderNumber(String workOrderNumber) {
        mapper.deleteByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public void deleteAll() {
        mapper.deleteAll();
    }
}
