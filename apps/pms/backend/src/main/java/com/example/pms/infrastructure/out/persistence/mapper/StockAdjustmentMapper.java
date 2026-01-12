package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.StockAdjustment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 在庫調整 Mapper.
 */
@Mapper
public interface StockAdjustmentMapper {

    void insert(StockAdjustment adjustment);

    void update(StockAdjustment adjustment);

    StockAdjustment findById(Integer id);

    StockAdjustment findByAdjustmentNumber(String adjustmentNumber);

    List<StockAdjustment> findByStocktakingNumber(String stocktakingNumber);

    List<StockAdjustment> findByLocationCode(String locationCode);

    List<StockAdjustment> findAll();

    void deleteAll();
}
