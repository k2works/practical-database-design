package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.ManufacturingOverhead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 製造間接費マスタ Mapper.
 */
@Mapper
public interface ManufacturingOverheadMapper {

    void insert(ManufacturingOverhead overhead);

    int update(ManufacturingOverhead overhead);

    ManufacturingOverhead findById(Integer id);

    List<ManufacturingOverhead> findByAccountingPeriod(String accountingPeriod);

    ManufacturingOverhead findByAccountingPeriodAndCostCategory(
            @Param("accountingPeriod") String accountingPeriod,
            @Param("costCategory") String costCategory);

    List<ManufacturingOverhead> findAll();

    BigDecimal sumByAccountingPeriod(String accountingPeriod);

    void deleteById(Integer id);

    void deleteByAccountingPeriod(String accountingPeriod);

    void deleteAll();
}
