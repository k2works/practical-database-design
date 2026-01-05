package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.inventory.MovementType;
import com.example.sms.domain.model.inventory.StockMovement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 入出庫履歴マッパー.
 */
@Mapper
public interface StockMovementMapper {

    void insert(StockMovement movement);

    Optional<StockMovement> findById(@Param("id") Integer id);

    List<StockMovement> findByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    List<StockMovement> findByProductCode(@Param("productCode") String productCode);

    List<StockMovement> findByWarehouseAndProduct(
            @Param("warehouseCode") String warehouseCode,
            @Param("productCode") String productCode);

    List<StockMovement> findByMovementType(@Param("movementType") MovementType movementType);

    List<StockMovement> findByMovementDateTimeBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    List<StockMovement> findAll();

    void deleteById(@Param("id") Integer id);

    void deleteAll();
}
