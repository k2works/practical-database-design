package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.CostVariance;

import java.util.List;
import java.util.Optional;

/**
 * 原価差異データリポジトリ.
 */
public interface CostVarianceRepository {

    /**
     * ページネーション付きで原価差異を取得する.
     *
     * @param offset  オフセット
     * @param limit   リミット
     * @param keyword キーワード（オプション）
     * @return 原価差異リスト
     */
    List<CostVariance> findWithPagination(int offset, int limit, String keyword);

    /**
     * 原価差異の件数を取得する.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(String keyword);

    /**
     * 原価差異を更新する.
     *
     * @param variance 原価差異
     * @return 更新件数
     */
    int update(CostVariance variance);

    void save(CostVariance variance);

    Optional<CostVariance> findById(Integer id);

    Optional<CostVariance> findByWorkOrderNumber(String workOrderNumber);

    List<CostVariance> findByItemCode(String itemCode);

    List<CostVariance> findAll();

    boolean existsByWorkOrderNumber(String workOrderNumber);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
