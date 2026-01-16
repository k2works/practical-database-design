package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stock;

import java.util.List;
import java.util.Optional;

/**
 * 在庫照会ユースケース（Input Port）.
 */
public interface StockUseCase {

    /**
     * 在庫一覧を取得する（ページネーション対応）.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword 検索キーワード（場所コード、品目コードで検索）
     * @return 在庫のページ結果
     */
    PageResult<Stock> getStockList(int page, int size, String keyword);

    /**
     * すべての在庫を取得する.
     *
     * @return 在庫リスト
     */
    List<Stock> getAllStocks();

    /**
     * 在庫を取得する.
     *
     * @param id 在庫ID
     * @return 在庫情報
     */
    Optional<Stock> getStock(Integer id);

    /**
     * 場所と品目で在庫を取得する.
     *
     * @param locationCode 場所コード
     * @param itemCode     品目コード
     * @return 在庫情報
     */
    Optional<Stock> getStockByLocationAndItem(String locationCode, String itemCode);
}
