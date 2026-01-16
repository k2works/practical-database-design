package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.Warehouse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 倉庫マスタ Mapper.
 */
@Mapper
public interface WarehouseMapper {

    void insert(Warehouse warehouse);

    void update(Warehouse warehouse);

    Warehouse findByWarehouseCode(String warehouseCode);

    List<Warehouse> findByDepartmentCode(String departmentCode);

    List<Warehouse> findAll();

    void deleteAll();
}
