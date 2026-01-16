package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.WorkOrderDetail;

import java.util.List;
import java.util.Optional;

/**
 * 作業指示明細リポジトリ.
 */
public interface WorkOrderDetailRepository {

    void save(WorkOrderDetail workOrderDetail);

    Optional<WorkOrderDetail> findById(Integer id);

    Optional<WorkOrderDetail> findByWorkOrderNumberAndSequence(String workOrderNumber, Integer sequence);

    List<WorkOrderDetail> findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrderDetail> findAll();

    void deleteAll();
}
