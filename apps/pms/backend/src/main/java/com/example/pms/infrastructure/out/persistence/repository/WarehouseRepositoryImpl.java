package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WarehouseRepository;
import com.example.pms.domain.model.inventory.Warehouse;
import com.example.pms.infrastructure.out.persistence.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 倉庫マスタリポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseMapper warehouseMapper;

    @Override
    public void save(Warehouse warehouse) {
        Warehouse existing = warehouseMapper.findByWarehouseCode(warehouse.getWarehouseCode());
        if (existing == null) {
            warehouseMapper.insert(warehouse);
        } else {
            warehouseMapper.update(warehouse);
        }
    }

    @Override
    public Optional<Warehouse> findByWarehouseCode(String warehouseCode) {
        return Optional.ofNullable(warehouseMapper.findByWarehouseCode(warehouseCode));
    }

    @Override
    public List<Warehouse> findByDepartmentCode(String departmentCode) {
        return warehouseMapper.findByDepartmentCode(departmentCode);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseMapper.findAll();
    }

    @Override
    public void deleteAll() {
        warehouseMapper.deleteAll();
    }
}
