package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.WarehouseRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Warehouse;
import com.example.sms.infrastructure.out.persistence.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 倉庫リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseMapper warehouseMapper;

    @Override
    public void save(Warehouse warehouse) {
        warehouseMapper.insert(warehouse);
    }

    @Override
    public Optional<Warehouse> findByCode(String warehouseCode) {
        return warehouseMapper.findByCode(warehouseCode);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseMapper.findAll();
    }

    @Override
    public List<Warehouse> findActive() {
        return warehouseMapper.findActive();
    }

    @Override
    public PageResult<Warehouse> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Warehouse> warehouses = warehouseMapper.findWithPagination(offset, size, keyword);
        long totalElements = warehouseMapper.count(keyword);
        return new PageResult<>(warehouses, page, size, totalElements);
    }

    @Override
    public void update(Warehouse warehouse) {
        warehouseMapper.update(warehouse);
    }

    @Override
    public void deleteByCode(String warehouseCode) {
        warehouseMapper.deleteByCode(warehouseCode);
    }

    @Override
    public void deleteAll() {
        warehouseMapper.deleteAll();
    }
}
