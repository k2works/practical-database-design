package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.MaterialConsumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 材料消費データ Mapper.
 */
@Mapper
public interface MaterialConsumptionMapper {

    void insert(MaterialConsumption consumption);

    int update(MaterialConsumption consumption);

    MaterialConsumption findById(Integer id);

    List<MaterialConsumption> findByWorkOrderNumber(String workOrderNumber);

    List<MaterialConsumption> findAll();

    BigDecimal sumDirectMaterialCostByWorkOrderNumber(String workOrderNumber);

    BigDecimal sumIndirectMaterialCostByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    void deleteById(Integer id);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
