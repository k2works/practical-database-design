package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.inventory.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * ページネーション付きで倉庫を検索.
     */
    List<Warehouse> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する倉庫の件数を取得.
     */
    long count(@Param("keyword") String keyword);

    int update(Warehouse warehouse);

    void deleteByCode(String warehouseCode);

    void deleteAll();
}
