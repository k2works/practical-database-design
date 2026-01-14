package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.CostVariance;

import java.util.List;
import java.util.Optional;

/**
 * 原価差異ユースケース（Input Port）.
 */
public interface CostVarianceUseCase {

    /**
     * ページネーション付きで原価差異一覧を取得する.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<CostVariance> getCostVarianceList(int page, int size, String keyword);

    /**
     * 全原価差異を取得する.
     *
     * @return 原価差異リスト
     */
    List<CostVariance> getAllCostVariances();

    /**
     * 作業指示番号で原価差異を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 原価差異
     */
    Optional<CostVariance> getCostVariance(String workOrderNumber);

    /**
     * 原価差異を登録する.
     *
     * @param costVariance 原価差異
     * @return 登録した原価差異
     */
    CostVariance createCostVariance(CostVariance costVariance);

    /**
     * 原価差異を更新する.
     *
     * @param workOrderNumber 作業指示番号
     * @param costVariance    原価差異
     * @return 更新した原価差異
     */
    CostVariance updateCostVariance(String workOrderNumber, CostVariance costVariance);

    /**
     * 原価差異を削除する.
     *
     * @param workOrderNumber 作業指示番号
     */
    void deleteCostVariance(String workOrderNumber);
}
