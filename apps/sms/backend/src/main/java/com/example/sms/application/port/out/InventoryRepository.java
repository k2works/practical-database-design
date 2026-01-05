package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 在庫リポジトリ（Output Port）.
 */
public interface InventoryRepository {

    void save(Inventory inventory);

    Optional<Inventory> findById(Integer id);

    Optional<Inventory> findByWarehouseAndProduct(String warehouseCode, String productCode);

    Optional<Inventory> findByWarehouseProductAndLocation(String warehouseCode, String productCode, String locationCode);

    List<Inventory> findByWarehouseCode(String warehouseCode);

    List<Inventory> findByProductCode(String productCode);

    List<Inventory> findByExpirationDateBefore(LocalDate date);

    List<Inventory> findAll();

    PageResult<Inventory> findWithPagination(int page, int size, String keyword, String warehouseCode);

    void update(Inventory inventory);

    void allocate(Integer id, BigDecimal quantity);

    void deallocate(Integer id, BigDecimal quantity);

    void receive(Integer id, BigDecimal quantity, LocalDate receiptDate);

    void ship(Integer id, BigDecimal quantity, LocalDate shipmentDate);

    void updateCurrentQuantity(Integer id, BigDecimal newQuantity, String reason);

    void deleteById(Integer id);

    void deleteAll();
}
