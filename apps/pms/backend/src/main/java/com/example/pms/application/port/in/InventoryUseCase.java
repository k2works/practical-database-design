package com.example.pms.application.port.in;

import com.example.pms.domain.model.inventory.Stock;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

/**
 * 在庫ユースケース（Input Port）.
 */
public interface InventoryUseCase {

    /**
     * 在庫一覧を取得する.
     *
     * @param query 検索条件
     * @return 在庫リスト
     */
    List<Stock> getInventory(InventoryQuery query);

    /**
     * 在庫サマリーを取得する.
     *
     * @return サマリーリスト
     */
    List<InventorySummary> getInventorySummary();

    /**
     * 在庫不足品目を取得する.
     *
     * @return 在庫不足品目リスト
     */
    List<InventorySummary> getShortageItems();

    /**
     * 品目コードと場所コードで在庫を取得する.
     *
     * @param itemCode 品目コード
     * @param locationCode 場所コード
     * @return 在庫
     */
    Stock getStock(String itemCode, String locationCode);

    /**
     * 在庫検索条件.
     */
    @Value
    @Builder
    class InventoryQuery {
        String itemCode;
        String locationCode;
    }

    /**
     * 在庫サマリー.
     */
    @Value
    @Builder
    class InventorySummary {
        String itemCode;
        String itemName;
        BigDecimal totalQuantity;
        BigDecimal safetyStock;
        StockState stockState;

        /**
         * 在庫状態.
         */
        public enum StockState {
            NORMAL, SHORTAGE, EXCESS
        }
    }
}
