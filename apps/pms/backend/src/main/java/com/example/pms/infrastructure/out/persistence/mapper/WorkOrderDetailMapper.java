package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.WorkOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作業指示明細 Mapper.
 */
@Mapper
public interface WorkOrderDetailMapper {

    void insert(WorkOrderDetail workOrderDetail);

    WorkOrderDetail findById(Integer id);

    WorkOrderDetail findByWorkOrderNumberAndSequence(
            @Param("workOrderNumber") String workOrderNumber,
            @Param("sequence") Integer sequence);

    List<WorkOrderDetail> findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrderDetail> findAll();

    void deleteAll();
}
