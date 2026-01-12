package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.CostVariance;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 原価差異データ Mapper.
 */
@Mapper
public interface CostVarianceMapper {

    void insert(CostVariance variance);

    CostVariance findById(Integer id);

    CostVariance findByWorkOrderNumber(String workOrderNumber);

    List<CostVariance> findByItemCode(String itemCode);

    List<CostVariance> findAll();

    boolean existsByWorkOrderNumber(String workOrderNumber);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
