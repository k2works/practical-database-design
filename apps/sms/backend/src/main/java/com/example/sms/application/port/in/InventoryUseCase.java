package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateInventoryCommand;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.inventory.StockMovement;

import java.util.List;

/**
 * 在庫ユースケース（Input Port）.
 */
public interface InventoryUseCase {

    /**
     * 在庫を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された在庫
     */
    Inventory createInventory(CreateInventoryCommand command);

    /**
     * 全在庫を取得する.
     *
     * @return 在庫リスト
     */
    List<Inventory> getAllInventories();

    /**
     * IDで在庫を取得する.
     *
     * @param id 在庫ID
     * @return 在庫
     */
    Inventory getInventoryById(Integer id);

    /**
     * 倉庫コードと商品コードで在庫を取得する.
     *
     * @param warehouseCode 倉庫コード
     * @param productCode 商品コード
     * @return 在庫
     */
    Inventory getInventoryByWarehouseAndProduct(String warehouseCode, String productCode);

    /**
     * 倉庫コードで在庫を検索する.
     *
     * @param warehouseCode 倉庫コード
     * @return 在庫リスト
     */
    List<Inventory> getInventoriesByWarehouse(String warehouseCode);

    /**
     * 商品コードで在庫を検索する.
     *
     * @param productCode 商品コード
     * @return 在庫リスト
     */
    List<Inventory> getInventoriesByProduct(String productCode);

    /**
     * 入出庫履歴を取得する.
     *
     * @return 入出庫履歴リスト
     */
    List<StockMovement> getAllStockMovements();

    /**
     * 倉庫コードで入出庫履歴を検索する.
     *
     * @param warehouseCode 倉庫コード
     * @return 入出庫履歴リスト
     */
    List<StockMovement> getStockMovementsByWarehouse(String warehouseCode);

    /**
     * 商品コードで入出庫履歴を検索する.
     *
     * @param productCode 商品コード
     * @return 入出庫履歴リスト
     */
    List<StockMovement> getStockMovementsByProduct(String productCode);
}
