package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.StockMovementRepository;
import com.example.sms.domain.model.inventory.MovementType;
import com.example.sms.domain.model.inventory.StockMovement;
import com.example.sms.infrastructure.out.persistence.mapper.StockMovementMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 入出庫履歴リポジトリ実装.
 */
@Repository
public class StockMovementRepositoryImpl implements StockMovementRepository {

    private final StockMovementMapper stockMovementMapper;

    public StockMovementRepositoryImpl(StockMovementMapper stockMovementMapper) {
        this.stockMovementMapper = stockMovementMapper;
    }

    @Override
    public void save(StockMovement movement) {
        stockMovementMapper.insert(movement);
    }

    @Override
    public Optional<StockMovement> findById(Integer id) {
        return stockMovementMapper.findById(id);
    }

    @Override
    public List<StockMovement> findByWarehouseCode(String warehouseCode) {
        return stockMovementMapper.findByWarehouseCode(warehouseCode);
    }

    @Override
    public List<StockMovement> findByProductCode(String productCode) {
        return stockMovementMapper.findByProductCode(productCode);
    }

    @Override
    public List<StockMovement> findByWarehouseAndProduct(String warehouseCode, String productCode) {
        return stockMovementMapper.findByWarehouseAndProduct(warehouseCode, productCode);
    }

    @Override
    public List<StockMovement> findByMovementType(MovementType movementType) {
        return stockMovementMapper.findByMovementType(movementType);
    }

    @Override
    public List<StockMovement> findByMovementDateTimeBetween(LocalDateTime from, LocalDateTime to) {
        return stockMovementMapper.findByMovementDateTimeBetween(from, to);
    }

    @Override
    public List<StockMovement> findAll() {
        return stockMovementMapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        stockMovementMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        stockMovementMapper.deleteAll();
    }
}
