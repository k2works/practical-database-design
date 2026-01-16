package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StockAdjustmentRepository;
import com.example.pms.domain.model.inventory.StockAdjustment;
import com.example.pms.infrastructure.out.persistence.mapper.StockAdjustmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 在庫調整リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class StockAdjustmentRepositoryImpl implements StockAdjustmentRepository {

    private final StockAdjustmentMapper stockAdjustmentMapper;

    @Override
    public void save(StockAdjustment adjustment) {
        if (adjustment.getId() == null) {
            stockAdjustmentMapper.insert(adjustment);
        } else {
            stockAdjustmentMapper.update(adjustment);
        }
    }

    @Override
    public Optional<StockAdjustment> findById(Integer id) {
        return Optional.ofNullable(stockAdjustmentMapper.findById(id));
    }

    @Override
    public Optional<StockAdjustment> findByAdjustmentNumber(String adjustmentNumber) {
        return Optional.ofNullable(stockAdjustmentMapper.findByAdjustmentNumber(adjustmentNumber));
    }

    @Override
    public List<StockAdjustment> findByStocktakingNumber(String stocktakingNumber) {
        return stockAdjustmentMapper.findByStocktakingNumber(stocktakingNumber);
    }

    @Override
    public List<StockAdjustment> findByLocationCode(String locationCode) {
        return stockAdjustmentMapper.findByLocationCode(locationCode);
    }

    @Override
    public List<StockAdjustment> findAll() {
        return stockAdjustmentMapper.findAll();
    }

    @Override
    public void deleteAll() {
        stockAdjustmentMapper.deleteAll();
    }
}
