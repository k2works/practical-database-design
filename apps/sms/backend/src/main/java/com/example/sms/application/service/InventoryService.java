package com.example.sms.application.service;

import com.example.sms.application.port.in.InventoryUseCase;
import com.example.sms.application.port.in.command.CreateInventoryCommand;
import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.application.port.out.StockMovementRepository;
import com.example.sms.domain.exception.InventoryNotFoundException;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.inventory.StockMovement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 在庫アプリケーションサービス.
 */
@Service
@Transactional
public class InventoryService implements InventoryUseCase {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            StockMovementRepository stockMovementRepository) {
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public Inventory createInventory(CreateInventoryCommand command) {
        Inventory inventory = Inventory.builder()
            .warehouseCode(command.warehouseCode())
            .productCode(command.productCode())
            .locationCode(command.locationCode())
            .currentQuantity(command.currentQuantity() != null ? command.currentQuantity() : BigDecimal.ZERO)
            .allocatedQuantity(command.allocatedQuantity() != null ? command.allocatedQuantity() : BigDecimal.ZERO)
            .orderedQuantity(command.orderedQuantity() != null ? command.orderedQuantity() : BigDecimal.ZERO)
            .lotNumber(command.lotNumber())
            .serialNumber(command.serialNumber())
            .expirationDate(command.expirationDate())
            .build();

        inventoryRepository.save(inventory);
        return inventory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Inventory getInventoryById(Integer id) {
        return inventoryRepository.findById(id)
            .orElseThrow(() -> new InventoryNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Inventory getInventoryByWarehouseAndProduct(String warehouseCode, String productCode) {
        return inventoryRepository.findByWarehouseAndProduct(warehouseCode, productCode)
            .orElseThrow(() -> new InventoryNotFoundException(warehouseCode, productCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getInventoriesByWarehouse(String warehouseCode) {
        return inventoryRepository.findByWarehouseCode(warehouseCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getInventoriesByProduct(String productCode) {
        return inventoryRepository.findByProductCode(productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsByWarehouse(String warehouseCode) {
        return stockMovementRepository.findByWarehouseCode(warehouseCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsByProduct(String productCode) {
        return stockMovementRepository.findByProductCode(productCode);
    }
}
