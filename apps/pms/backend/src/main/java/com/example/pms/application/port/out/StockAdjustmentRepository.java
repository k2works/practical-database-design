package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.StockAdjustment;

import java.util.List;
import java.util.Optional;

/**
 * 在庫調整リポジトリ.
 */
public interface StockAdjustmentRepository {

    void save(StockAdjustment adjustment);

    Optional<StockAdjustment> findById(Integer id);

    Optional<StockAdjustment> findByAdjustmentNumber(String adjustmentNumber);

    List<StockAdjustment> findByStocktakingNumber(String stocktakingNumber);

    List<StockAdjustment> findByLocationCode(String locationCode);

    List<StockAdjustment> findAll();

    void deleteAll();
}
