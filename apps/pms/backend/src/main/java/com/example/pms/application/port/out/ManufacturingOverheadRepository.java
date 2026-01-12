package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.ManufacturingOverhead;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 製造間接費マスタリポジトリ.
 */
public interface ManufacturingOverheadRepository {

    void save(ManufacturingOverhead overhead);

    int update(ManufacturingOverhead overhead);

    Optional<ManufacturingOverhead> findById(Integer id);

    List<ManufacturingOverhead> findByAccountingPeriod(String accountingPeriod);

    Optional<ManufacturingOverhead> findByAccountingPeriodAndCostCategory(
            String accountingPeriod, String costCategory);

    List<ManufacturingOverhead> findAll();

    BigDecimal sumByAccountingPeriod(String accountingPeriod);

    void deleteById(Integer id);

    void deleteByAccountingPeriod(String accountingPeriod);

    void deleteAll();
}
