package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.inventory.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 在庫マッパー.
 */
@Mapper
public interface InventoryMapper {

    void insert(Inventory inventory);

    Optional<Inventory> findById(Integer id);

    List<Inventory> findByWarehouseAndProduct(
            @Param("warehouseCode") String warehouseCode,
            @Param("productCode") String productCode);

    List<Inventory> findByWarehouseProductAndLocation(
            @Param("warehouseCode") String warehouseCode,
            @Param("productCode") String productCode,
            @Param("locationCode") String locationCode);

    List<Inventory> findByWarehouseCode(String warehouseCode);

    List<Inventory> findByProductCode(String productCode);

    List<Inventory> findByExpirationDateBefore(LocalDate date);

    List<Inventory> findAll();

    Integer findVersionById(Integer id);

    int updateWithOptimisticLock(Inventory inventory);

    int allocate(@Param("id") Integer id, @Param("quantity") BigDecimal quantity);

    int deallocate(@Param("id") Integer id, @Param("quantity") BigDecimal quantity);

    void receive(
            @Param("id") Integer id,
            @Param("quantity") BigDecimal quantity,
            @Param("receiptDate") LocalDate receiptDate);

    int ship(
            @Param("id") Integer id,
            @Param("quantity") BigDecimal quantity,
            @Param("shipmentDate") LocalDate shipmentDate);

    void updateCurrentQuantity(
            @Param("id") Integer id,
            @Param("newQuantity") BigDecimal newQuantity);

    void deleteById(Integer id);

    void deleteAll();
}
