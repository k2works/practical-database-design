package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Warehouse;

import java.util.List;
import java.util.Optional;

/**
 * 倉庫リポジトリ（Output Port）.
 */
public interface WarehouseRepository {

    void save(Warehouse warehouse);

    Optional<Warehouse> findByCode(String warehouseCode);

    List<Warehouse> findAll();

    List<Warehouse> findActive();

    /**
     * ページネーション付きで倉庫を検索.
     */
    PageResult<Warehouse> findWithPagination(int page, int size, String keyword);

    void update(Warehouse warehouse);

    void deleteByCode(String warehouseCode);

    void deleteAll();
}
