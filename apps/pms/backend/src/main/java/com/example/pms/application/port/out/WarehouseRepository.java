package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.Warehouse;

import java.util.List;
import java.util.Optional;

/**
 * 倉庫マスタリポジトリ.
 */
public interface WarehouseRepository {

    void save(Warehouse warehouse);

    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    List<Warehouse> findByDepartmentCode(String departmentCode);

    List<Warehouse> findAll();

    void deleteAll();
}
