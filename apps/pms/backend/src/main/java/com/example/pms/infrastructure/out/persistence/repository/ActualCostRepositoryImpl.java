package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ActualCostRepository;
import com.example.pms.domain.model.cost.ActualCost;
import com.example.pms.infrastructure.out.persistence.mapper.ActualCostMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 実際原価データリポジトリ実装.
 */
@Repository
public class ActualCostRepositoryImpl implements ActualCostRepository {

    private final ActualCostMapper mapper;

    public ActualCostRepositoryImpl(ActualCostMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<ActualCost> findWithPagination(int offset, int limit, String keyword) {
        return mapper.findWithPagination(offset, limit, keyword);
    }

    @Override
    public long count(String keyword) {
        return mapper.count(keyword);
    }

    @Override
    public void save(ActualCost actualCost) {
        mapper.insert(actualCost);
    }

    @Override
    public int update(ActualCost actualCost) {
        return mapper.update(actualCost);
    }

    @Override
    public int recalculate(String workOrderNumber, Integer version,
                           BigDecimal actualMaterialCost, BigDecimal actualLaborCost, BigDecimal actualExpense) {
        return mapper.recalculateWithOptimisticLock(
                workOrderNumber, version, actualMaterialCost, actualLaborCost, actualExpense);
    }

    @Override
    public Optional<Integer> findVersionByWorkOrderNumber(String workOrderNumber) {
        return Optional.ofNullable(mapper.findVersionByWorkOrderNumber(workOrderNumber));
    }

    @Override
    public Optional<ActualCost> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public Optional<ActualCost> findByWorkOrderNumber(String workOrderNumber) {
        return Optional.ofNullable(mapper.findByWorkOrderNumber(workOrderNumber));
    }

    @Override
    public Optional<ActualCost> findByWorkOrderNumberWithRelations(String workOrderNumber) {
        return Optional.ofNullable(mapper.findByWorkOrderNumberWithRelations(workOrderNumber));
    }

    @Override
    public List<ActualCost> findAll() {
        return mapper.findAll();
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
