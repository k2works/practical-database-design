package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.MaterialConsumptionRepository;
import com.example.pms.domain.model.cost.MaterialConsumption;
import com.example.pms.infrastructure.out.persistence.mapper.MaterialConsumptionMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 材料消費データリポジトリ実装.
 */
@Repository
public class MaterialConsumptionRepositoryImpl implements MaterialConsumptionRepository {

    private final MaterialConsumptionMapper mapper;

    public MaterialConsumptionRepositoryImpl(MaterialConsumptionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(MaterialConsumption consumption) {
        mapper.insert(consumption);
    }

    @Override
    public int update(MaterialConsumption consumption) {
        return mapper.update(consumption);
    }

    @Override
    public Optional<MaterialConsumption> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<MaterialConsumption> findByWorkOrderNumber(String workOrderNumber) {
        return mapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public List<MaterialConsumption> findAll() {
        return mapper.findAll();
    }

    @Override
    public BigDecimal sumDirectMaterialCostByWorkOrderNumber(String workOrderNumber) {
        return mapper.sumDirectMaterialCostByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public BigDecimal sumIndirectMaterialCostByPeriod(LocalDate startDate, LocalDate endDate) {
        return mapper.sumIndirectMaterialCostByPeriod(startDate, endDate);
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
