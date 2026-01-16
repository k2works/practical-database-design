package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.CostVariance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 原価差異データ Mapper.
 */
@Mapper
public interface CostVarianceMapper {

    /**
     * ページネーション付きで原価差異を取得する.
     */
    List<CostVariance> findWithPagination(@Param("offset") int offset,
                                           @Param("limit") int limit,
                                           @Param("keyword") String keyword);

    /**
     * 原価差異の件数を取得する.
     */
    long count(@Param("keyword") String keyword);

    /**
     * 原価差異を更新する.
     */
    int update(CostVariance variance);

    void insert(CostVariance variance);

    CostVariance findById(Integer id);

    CostVariance findByWorkOrderNumber(String workOrderNumber);

    List<CostVariance> findByItemCode(String itemCode);

    List<CostVariance> findAll();

    boolean existsByWorkOrderNumber(String workOrderNumber);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
