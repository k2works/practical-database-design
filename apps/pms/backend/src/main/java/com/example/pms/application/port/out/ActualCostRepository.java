package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.ActualCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 実際原価データリポジトリ.
 */
public interface ActualCostRepository {

    void save(ActualCost actualCost);

    int update(ActualCost actualCost);

    /**
     * 原価再計算（楽観ロック対応）.
     *
     * @param workOrderNumber    作業指示番号
     * @param version            現在のバージョン
     * @param actualMaterialCost 実際材料費
     * @param actualLaborCost    実際労務費
     * @param actualExpense      実際経費
     * @return 更新件数（0の場合は楽観ロック失敗）
     */
    int recalculate(String workOrderNumber, Integer version,
                    BigDecimal actualMaterialCost, BigDecimal actualLaborCost, BigDecimal actualExpense);

    /**
     * バージョン番号を取得.
     */
    Optional<Integer> findVersionByWorkOrderNumber(String workOrderNumber);

    Optional<ActualCost> findById(Integer id);

    Optional<ActualCost> findByWorkOrderNumber(String workOrderNumber);

    Optional<ActualCost> findByWorkOrderNumberWithRelations(String workOrderNumber);

    List<ActualCost> findAll();

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
