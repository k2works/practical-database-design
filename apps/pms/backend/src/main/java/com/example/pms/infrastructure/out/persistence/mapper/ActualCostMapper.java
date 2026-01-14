package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.ActualCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 実際原価データ Mapper.
 */
@Mapper
public interface ActualCostMapper {

    /**
     * ページネーション付きで実際原価を取得する.
     */
    List<ActualCost> findWithPagination(@Param("offset") int offset,
                                         @Param("limit") int limit,
                                         @Param("keyword") String keyword);

    /**
     * 実際原価の件数を取得する.
     */
    long count(@Param("keyword") String keyword);

    void insert(ActualCost actualCost);

    int update(ActualCost actualCost);

    /**
     * 原価再計算による更新（楽観ロック対応）.
     */
    int recalculateWithOptimisticLock(@Param("workOrderNumber") String workOrderNumber,
                                       @Param("version") Integer version,
                                       @Param("actualMaterialCost") BigDecimal actualMaterialCost,
                                       @Param("actualLaborCost") BigDecimal actualLaborCost,
                                       @Param("actualExpense") BigDecimal actualExpense);

    /**
     * バージョン番号を取得.
     */
    Integer findVersionByWorkOrderNumber(String workOrderNumber);

    ActualCost findById(Integer id);

    ActualCost findByWorkOrderNumber(String workOrderNumber);

    ActualCost findByWorkOrderNumberWithRelations(String workOrderNumber);

    List<ActualCost> findAll();

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
