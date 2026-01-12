package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.OverheadAllocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 製造間接費配賦データ Mapper.
 */
@Mapper
public interface OverheadAllocationMapper {

    void insert(OverheadAllocation allocation);

    OverheadAllocation findById(Integer id);

    List<OverheadAllocation> findByWorkOrderNumber(String workOrderNumber);

    OverheadAllocation findByWorkOrderNumberAndPeriod(
            @Param("workOrderNumber") String workOrderNumber,
            @Param("accountingPeriod") String accountingPeriod);

    List<OverheadAllocation> findAll();

    BigDecimal sumByWorkOrderNumber(String workOrderNumber);

    void deleteById(Integer id);

    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();
}
