package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;

import java.util.List;
import java.util.Optional;

/**
 * 棚卸リポジトリ.
 */
public interface StocktakingRepository {

    void save(Stocktaking stocktaking);

    Optional<Stocktaking> findById(Integer id);

    Optional<Stocktaking> findByStocktakingNumber(String stocktakingNumber);

    /**
     * 棚卸番号で検索（明細を含む）.
     *
     * @param stocktakingNumber 棚卸番号
     * @return 明細を含む棚卸データ
     */
    Optional<Stocktaking> findByStocktakingNumberWithDetails(String stocktakingNumber);

    List<Stocktaking> findByLocationCode(String locationCode);

    List<Stocktaking> findByStatus(StocktakingStatus status);

    List<Stocktaking> findAll();

    /**
     * ページネーション付きで検索.
     *
     * @param offset オフセット
     * @param limit 件数
     * @param keyword キーワード（オプション）
     * @return 棚卸リスト
     */
    List<Stocktaking> findWithPagination(int offset, int limit, String keyword);

    /**
     * 件数をカウント.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(String keyword);

    /**
     * 棚卸番号で削除.
     *
     * @param stocktakingNumber 棚卸番号
     */
    void deleteByStocktakingNumber(String stocktakingNumber);

    void deleteAll();
}
