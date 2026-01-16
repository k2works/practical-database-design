package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.ActualCost;

import java.util.List;
import java.util.Optional;

/**
 * 実際原価（製造原価）ユースケース（Input Port）.
 */
public interface ActualCostUseCase {

    /**
     * ページネーション付きで実際原価一覧を取得する.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<ActualCost> getActualCostList(int page, int size, String keyword);

    /**
     * 全実際原価を取得する.
     *
     * @return 実際原価リスト
     */
    List<ActualCost> getAllActualCosts();

    /**
     * 作業指示番号で実際原価を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 実際原価
     */
    Optional<ActualCost> getActualCost(String workOrderNumber);

    /**
     * 実際原価を登録する.
     *
     * @param actualCost 実際原価
     * @return 登録した実際原価
     */
    ActualCost createActualCost(ActualCost actualCost);

    /**
     * 実際原価を更新する.
     *
     * @param workOrderNumber 作業指示番号
     * @param actualCost      実際原価
     * @return 更新した実際原価
     */
    ActualCost updateActualCost(String workOrderNumber, ActualCost actualCost);

    /**
     * 実際原価を削除する.
     *
     * @param workOrderNumber 作業指示番号
     */
    void deleteActualCost(String workOrderNumber);
}
