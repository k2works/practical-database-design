package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 在庫情報リポジトリ.
 */
public interface StockRepository {

    void save(Stock stock);

    Optional<Stock> findById(Integer id);

    Optional<Stock> findByLocationAndItem(String locationCode, String itemCode);

    List<Stock> findByLocation(String locationCode);

    List<Stock> findByItem(String itemCode);

    List<Stock> findAll();

    /**
     * ページネーション対応の在庫一覧取得.
     *
     * @param offset  オフセット
     * @param limit   取得件数
     * @param keyword 検索キーワード
     * @return 在庫リスト
     */
    List<Stock> findWithPagination(int offset, int limit, String keyword);

    /**
     * 在庫件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(String keyword);

    void deleteAll();

    /**
     * 在庫を増加（楽観ロック対応）.
     *
     * @param locationCode    場所コード
     * @param itemCode        品目コード
     * @param quantity        増加数量
     * @param expectedVersion 期待するバージョン
     * @return 更新成功時 true、バージョン競合時 false
     */
    boolean increase(String locationCode, String itemCode, BigDecimal quantity, Integer expectedVersion);

    /**
     * 在庫を減少（楽観ロック対応）.
     *
     * @param locationCode    場所コード
     * @param itemCode        品目コード
     * @param quantity        減少数量
     * @param expectedVersion 期待するバージョン
     * @return 更新成功時 true、バージョン競合時 false
     */
    boolean decrease(String locationCode, String itemCode, BigDecimal quantity, Integer expectedVersion);

    /**
     * 在庫を調整（楽観ロック対応）.
     *
     * @param stock           在庫情報
     * @param expectedVersion 期待するバージョン
     * @return 更新成功時 true、バージョン競合時 false
     */
    boolean adjust(Stock stock, Integer expectedVersion);
}
