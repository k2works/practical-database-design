package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ManufacturingOverheadRepository;
import com.example.pms.domain.model.cost.ManufacturingOverhead;
import com.example.pms.infrastructure.out.persistence.mapper.ManufacturingOverheadMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 製造間接費マスタリポジトリ実装.
 */
@Repository
public class ManufacturingOverheadRepositoryImpl implements ManufacturingOverheadRepository {

    private final ManufacturingOverheadMapper mapper;

    public ManufacturingOverheadRepositoryImpl(ManufacturingOverheadMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(ManufacturingOverhead overhead) {
        mapper.insert(overhead);
    }

    @Override
    public int update(ManufacturingOverhead overhead) {
        return mapper.update(overhead);
    }

    @Override
    public Optional<ManufacturingOverhead> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<ManufacturingOverhead> findByAccountingPeriod(String accountingPeriod) {
        return mapper.findByAccountingPeriod(accountingPeriod);
    }

    @Override
    public Optional<ManufacturingOverhead> findByAccountingPeriodAndCostCategory(
            String accountingPeriod, String costCategory) {
        return Optional.ofNullable(mapper.findByAccountingPeriodAndCostCategory(accountingPeriod, costCategory));
    }

    @Override
    public List<ManufacturingOverhead> findAll() {
        return mapper.findAll();
    }

    @Override
    public BigDecimal sumByAccountingPeriod(String accountingPeriod) {
        return mapper.sumByAccountingPeriod(accountingPeriod);
    }

    @Override
    public void deleteById(Integer id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteByAccountingPeriod(String accountingPeriod) {
        mapper.deleteByAccountingPeriod(accountingPeriod);
    }

    @Override
    public void deleteAll() {
        mapper.deleteAll();
    }
}
