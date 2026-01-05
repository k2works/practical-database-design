package com.example.sms.application.port.in;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Warehouse;

import java.util.List;

/**
 * 倉庫ユースケース（Input Port）.
 */
public interface WarehouseUseCase {

    Warehouse createWarehouse(Warehouse warehouse);

    Warehouse updateWarehouse(String warehouseCode, Warehouse warehouse);

    List<Warehouse> getAllWarehouses();

    List<Warehouse> getActiveWarehouses();

    /**
     * ページネーション付きで倉庫を取得.
     */
    PageResult<Warehouse> getWarehouses(int page, int size, String keyword);

    Warehouse getWarehouseByCode(String warehouseCode);

    void deleteWarehouse(String warehouseCode);
}
