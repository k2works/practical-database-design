package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.MovementType;
import com.example.sms.domain.model.inventory.StockMovement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 入出庫履歴リポジトリ（Output Port）.
 */
public interface StockMovementRepository {

    void save(StockMovement movement);

    PageResult<StockMovement> findWithPagination(int page, int size, String warehouseCode, String productCode);

    Optional<StockMovement> findById(Integer id);

    List<StockMovement> findByWarehouseCode(String warehouseCode);

    List<StockMovement> findByProductCode(String productCode);

    List<StockMovement> findByWarehouseAndProduct(String warehouseCode, String productCode);

    List<StockMovement> findByMovementType(MovementType movementType);

    List<StockMovement> findByMovementDateTimeBetween(LocalDateTime from, LocalDateTime to);

    List<StockMovement> findAll();

    void deleteById(Integer id);

    void deleteAll();
}
