package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.StockMovementRepository;
import com.example.sms.domain.model.inventory.MovementType;
import com.example.sms.domain.model.inventory.StockMovement;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 入出庫履歴リポジトリ実装.
 * TODO: マッパー実装後に本実装を行う
 */
@Repository
public class StockMovementRepositoryImpl implements StockMovementRepository {

    @Override
    public void save(StockMovement movement) {
        // TODO: 実装
    }

    @Override
    public Optional<StockMovement> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<StockMovement> findByWarehouseCode(String warehouseCode) {
        return Collections.emptyList();
    }

    @Override
    public List<StockMovement> findByProductCode(String productCode) {
        return Collections.emptyList();
    }

    @Override
    public List<StockMovement> findByWarehouseAndProduct(String warehouseCode, String productCode) {
        return Collections.emptyList();
    }

    @Override
    public List<StockMovement> findByMovementType(MovementType movementType) {
        return Collections.emptyList();
    }

    @Override
    public List<StockMovement> findByMovementDateTimeBetween(LocalDateTime from, LocalDateTime to) {
        return Collections.emptyList();
    }

    @Override
    public List<StockMovement> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void deleteById(Integer id) {
        // TODO: 実装
    }

    @Override
    public void deleteAll() {
        // TODO: 実装
    }
}
