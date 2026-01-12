package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.OverheadAllocationRepository;
import com.example.pms.domain.model.cost.OverheadAllocation;
import com.example.pms.infrastructure.out.persistence.mapper.OverheadAllocationMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 製造間接費配賦データリポジトリ実装.
 */
@Repository
public class OverheadAllocationRepositoryImpl implements OverheadAllocationRepository {

    private final OverheadAllocationMapper mapper;

    public OverheadAllocationRepositoryImpl(OverheadAllocationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(OverheadAllocation allocation) {
        mapper.insert(allocation);
    }

    @Override
    public Optional<OverheadAllocation> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<OverheadAllocation> findByWorkOrderNumber(String workOrderNumber) {
        return mapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public Optional<OverheadAllocation> findByWorkOrderNumberAndPeriod(
            String workOrderNumber, String accountingPeriod) {
        return Optional.ofNullable(mapper.findByWorkOrderNumberAndPeriod(workOrderNumber, accountingPeriod));
    }

    @Override
    public List<OverheadAllocation> findAll() {
        return mapper.findAll();
    }

    @Override
    public BigDecimal sumByWorkOrderNumber(String workOrderNumber) {
        return mapper.sumByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public void deleteById(Integer id) {
        mapper.deleteById(id);
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
