package com.example.sms.application.service;

import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.application.port.out.WarehouseRepository;
import com.example.sms.domain.exception.DuplicateWarehouseException;
import com.example.sms.domain.exception.WarehouseNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 倉庫アプリケーションサービス.
 */
@Service
@Transactional
public class WarehouseService implements WarehouseUseCase {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Warehouse createWarehouse(Warehouse warehouse) {
        warehouseRepository.findByCode(warehouse.getWarehouseCode())
            .ifPresent(existing -> {
                throw new DuplicateWarehouseException(warehouse.getWarehouseCode());
            });

        warehouseRepository.save(warehouse);
        return warehouse;
    }

    @Override
    public Warehouse updateWarehouse(String warehouseCode, Warehouse warehouse) {
        Warehouse existing = warehouseRepository.findByCode(warehouseCode)
            .orElseThrow(() -> new WarehouseNotFoundException(warehouseCode));

        Warehouse updated = Warehouse.builder()
            .warehouseCode(warehouseCode)
            .warehouseName(coalesce(warehouse.getWarehouseName(), existing.getWarehouseName()))
            .warehouseNameKana(coalesce(warehouse.getWarehouseNameKana(), existing.getWarehouseNameKana()))
            .warehouseType(coalesce(warehouse.getWarehouseType(), existing.getWarehouseType()))
            .postalCode(coalesce(warehouse.getPostalCode(), existing.getPostalCode()))
            .address(coalesce(warehouse.getAddress(), existing.getAddress()))
            .phoneNumber(coalesce(warehouse.getPhoneNumber(), existing.getPhoneNumber()))
            .activeFlag(coalesce(warehouse.getActiveFlag(), existing.getActiveFlag()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .build();

        warehouseRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findActive();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Warehouse> getWarehouses(int page, int size, String keyword) {
        return warehouseRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseByCode(String warehouseCode) {
        return warehouseRepository.findByCode(warehouseCode)
            .orElseThrow(() -> new WarehouseNotFoundException(warehouseCode));
    }

    @Override
    public void deleteWarehouse(String warehouseCode) {
        warehouseRepository.findByCode(warehouseCode)
            .orElseThrow(() -> new WarehouseNotFoundException(warehouseCode));

        warehouseRepository.deleteByCode(warehouseCode);
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
