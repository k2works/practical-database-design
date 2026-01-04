package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateStocktakingCommand;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingStatus;

import java.util.List;

/**
 * 棚卸ユースケース（Input Port）.
 */
public interface StocktakingUseCase {

    /**
     * 棚卸を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された棚卸
     */
    Stocktaking createStocktaking(CreateStocktakingCommand command);

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
    Stocktaking getStocktakingByNumber(String stocktakingNumber);

    /**
     * 棚卸番号で棚卸（明細含む）を取得する.
     *
     * @param stocktakingNumber 棚卸番号
     * @return 棚卸（明細含む）
     */
    Stocktaking getStocktakingWithDetails(String stocktakingNumber);

    /**
     * ステータスで棚卸を検索する.
     *
     * @param status 棚卸ステータス
     * @return 棚卸リスト
     */
    List<Stocktaking> getStocktakingsByStatus(StocktakingStatus status);

    /**
     * 倉庫コードで棚卸を検索する.
     *
     * @param warehouseCode 倉庫コード
     * @return 棚卸リスト
     */
    List<Stocktaking> getStocktakingsByWarehouse(String warehouseCode);
}
