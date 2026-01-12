package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * 作業指示リポジトリ.
 */
public interface WorkOrderRepository {

    void save(WorkOrder workOrder);

    Optional<WorkOrder> findById(Integer id);

    Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrder> findByOrderNumber(String orderNumber);

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findAll();

    void deleteAll();
}
