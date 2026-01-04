package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.infrastructure.out.persistence.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 在庫リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryMapper inventoryMapper;

    @Override
    public void save(Inventory inventory) {
        inventoryMapper.insert(inventory);
    }

    @Override
    public Optional<Inventory> findById(Integer id) {
        return inventoryMapper.findById(id);
    }

    @Override
    public Optional<Inventory> findByWarehouseAndProduct(String warehouseCode, String productCode) {
        List<Inventory> inventories = inventoryMapper.findByWarehouseAndProduct(warehouseCode, productCode);
        return inventories.isEmpty() ? Optional.empty() : Optional.of(inventories.get(0));
    }

    @Override
    public Optional<Inventory> findByWarehouseProductAndLocation(
            String warehouseCode, String productCode, String locationCode) {
        List<Inventory> inventories = inventoryMapper
                .findByWarehouseProductAndLocation(warehouseCode, productCode, locationCode);
        return inventories.isEmpty() ? Optional.empty() : Optional.of(inventories.get(0));
    }

    @Override
    public List<Inventory> findByWarehouseCode(String warehouseCode) {
        return inventoryMapper.findByWarehouseCode(warehouseCode);
    }

    @Override
    public List<Inventory> findByProductCode(String productCode) {
        return inventoryMapper.findByProductCode(productCode);
    }

    @Override
    public List<Inventory> findByExpirationDateBefore(LocalDate date) {
        return inventoryMapper.findByExpirationDateBefore(date);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryMapper.findAll();
    }

    @Override
    public PageResult<Inventory> findWithPagination(int page, int size, String keyword, String warehouseCode) {
        int offset = page * size;
        List<Inventory> content = inventoryMapper.findWithPagination(offset, size, keyword, warehouseCode);
        long totalElements = inventoryMapper.count(keyword, warehouseCode);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional
    public void update(Inventory inventory) {
        int updatedCount = inventoryMapper.updateWithOptimisticLock(inventory);

        if (updatedCount == 0) {
            Integer currentVersion = inventoryMapper.findVersionById(inventory.getId());

            if (currentVersion == null) {
                throw new OptimisticLockException("在庫", inventory.getId());
            } else {
                throw new OptimisticLockException(
                        "在庫",
                        inventory.getId(),
                        inventory.getVersion(),
                        currentVersion
                );
            }
        }
    }

    @Override
    public void allocate(Integer id, BigDecimal quantity) {
        int updated = inventoryMapper.allocate(id, quantity);
        if (updated == 0) {
            throw new IllegalStateException("引当に失敗しました。有効在庫不足の可能性があります。");
        }
    }

    @Override
    public void deallocate(Integer id, BigDecimal quantity) {
        int updated = inventoryMapper.deallocate(id, quantity);
        if (updated == 0) {
            throw new IllegalStateException("引当解除に失敗しました。");
        }
    }

    @Override
    public void receive(Integer id, BigDecimal quantity, LocalDate receiptDate) {
        inventoryMapper.receive(id, quantity, receiptDate);
    }

    @Override
    public void ship(Integer id, BigDecimal quantity, LocalDate shipmentDate) {
        int updated = inventoryMapper.ship(id, quantity, shipmentDate);
        if (updated == 0) {
            throw new IllegalStateException("出庫に失敗しました。在庫不足の可能性があります。");
        }
    }

    @Override
    public void updateCurrentQuantity(Integer id, BigDecimal newQuantity, String reason) {
        inventoryMapper.updateCurrentQuantity(id, newQuantity);
    }

    @Override
    public void deleteById(Integer id) {
        inventoryMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        inventoryMapper.deleteAll();
    }
}
