package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.inventory.Warehouse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 倉庫マッパー.
 */
@Mapper
public interface WarehouseMapper {

    void insert(Warehouse warehouse);

    Optional<Warehouse> findByCode(String warehouseCode);

    List<Warehouse> findAll();

    List<Warehouse> findActive();

    int update(Warehouse warehouse);

    void deleteByCode(String warehouseCode);

    void deleteAll();
}
