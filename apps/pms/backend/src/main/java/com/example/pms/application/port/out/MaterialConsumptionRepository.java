package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.MaterialConsumption;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 材料消費データリポジトリ.
 */
public interface MaterialConsumptionRepository {

    void save(MaterialConsumption consumption);

    int update(MaterialConsumption consumption);

    Optional<MaterialConsumption> findById(Integer id);

    List<MaterialConsumption> findByWorkOrderNumber(String workOrderNumber);

    List<MaterialConsumption> findAll();

    BigDecimal sumDirectMaterialCostByWorkOrderNumber(String workOrderNumber);

    BigDecimal sumIndirectMaterialCostByPeriod(LocalDate startDate, LocalDate endDate);

    void deleteById(Integer id);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
