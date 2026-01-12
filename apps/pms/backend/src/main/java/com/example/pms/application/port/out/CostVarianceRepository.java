package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.CostVariance;

import java.util.List;
import java.util.Optional;

/**
 * 原価差異データリポジトリ.
 */
public interface CostVarianceRepository {

    void save(CostVariance variance);

    Optional<CostVariance> findById(Integer id);

    Optional<CostVariance> findByWorkOrderNumber(String workOrderNumber);

    List<CostVariance> findByItemCode(String itemCode);

    List<CostVariance> findAll();

    boolean existsByWorkOrderNumber(String workOrderNumber);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
