package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作業指示 Mapper.
 */
@Mapper
public interface WorkOrderMapper {

    void insert(WorkOrder workOrder);

    WorkOrder findById(Integer id);

    WorkOrder findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrder> findByOrderNumber(String orderNumber);

    List<WorkOrder> findByStatus(@Param("status") WorkOrderStatus status);

    List<WorkOrder> findAll();

    void deleteAll();
}
