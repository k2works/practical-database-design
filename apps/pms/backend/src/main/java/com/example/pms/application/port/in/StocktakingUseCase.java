package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stocktaking;

import java.util.List;
import java.util.Optional;

/**
 * 棚卸ユースケース（Input Port）.
 */
public interface StocktakingUseCase {

    /**
     * ページネーション付きで棚卸一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<Stocktaking> getStocktakingList(int page, int size, String keyword);

    /**
     * 全棚卸を取得する.
     *
     * @return 棚卸リスト
     */
    List<Stocktaking> getAllStocktakings();

    /**
     * 棚卸番号で棚卸を取得する.
     *
     * @param stocktakingNumber 棚卸番号
     * @return 棚卸
     */
    Optional<Stocktaking> getStocktaking(String stocktakingNumber);

    /**
     * 棚卸を登録する.
     *
     * @param stocktaking 棚卸
     * @return 登録した棚卸
     */
    Stocktaking createStocktaking(Stocktaking stocktaking);

    /**
     * 棚卸を更新する.
     *
     * @param stocktakingNumber 棚卸番号
     * @param stocktaking 棚卸
     * @return 更新した棚卸
     */
    Stocktaking updateStocktaking(String stocktakingNumber, Stocktaking stocktaking);

    /**
     * 棚卸を削除する.
     *
     * @param stocktakingNumber 棚卸番号
     */
    void deleteStocktaking(String stocktakingNumber);
}
