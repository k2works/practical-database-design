package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import com.example.pms.infrastructure.out.persistence.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 作業指示リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class WorkOrderRepositoryImpl implements WorkOrderRepository {

    private final WorkOrderMapper workOrderMapper;

    @Override
    public void save(WorkOrder workOrder) {
        workOrderMapper.insert(workOrder);
    }

    @Override
    public Optional<WorkOrder> findById(Integer id) {
        return Optional.ofNullable(workOrderMapper.findById(id));
    }

    @Override
    public Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber) {
        return Optional.ofNullable(workOrderMapper.findByWorkOrderNumber(workOrderNumber));
    }

    @Override
    public List<WorkOrder> findByOrderNumber(String orderNumber) {
        return workOrderMapper.findByOrderNumber(orderNumber);
    }

    @Override
    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        return workOrderMapper.findByStatus(status);
    }

    @Override
    public List<WorkOrder> findAll() {
        return workOrderMapper.findAll();
    }

    @Override
    public void deleteAll() {
        workOrderMapper.deleteAll();
    }

    @Override
    public boolean updateCompletionQuantities(String workOrderNumber, BigDecimal completedQuantity,
                                              BigDecimal goodQuantity, BigDecimal defectQuantity,
                                              Integer expectedVersion) {
        int updatedRows = workOrderMapper.updateCompletionQuantities(
                workOrderNumber, completedQuantity, goodQuantity, defectQuantity, expectedVersion);
        return updatedRows > 0;
    }
}
