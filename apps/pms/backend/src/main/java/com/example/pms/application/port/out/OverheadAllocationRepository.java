package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.OverheadAllocation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 製造間接費配賦データリポジトリ.
 */
public interface OverheadAllocationRepository {

    void save(OverheadAllocation allocation);

    Optional<OverheadAllocation> findById(Integer id);

    List<OverheadAllocation> findByWorkOrderNumber(String workOrderNumber);

    Optional<OverheadAllocation> findByWorkOrderNumberAndPeriod(
            String workOrderNumber, String accountingPeriod);

    List<OverheadAllocation> findAll();

    BigDecimal sumByWorkOrderNumber(String workOrderNumber);

    void deleteById(Integer id);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
