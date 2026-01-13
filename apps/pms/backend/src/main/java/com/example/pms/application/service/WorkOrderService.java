package com.example.pms.application.service;

import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.application.port.in.command.CreateWorkOrderCommand;
import com.example.pms.application.port.in.command.RecordCompletionCommand;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.exception.WorkOrderNotFoundException;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 作業指示サービス（Application Service）.
 */
@Service
@Transactional
public class WorkOrderService implements WorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrder getWorkOrder(String workOrderNumber) {
        return workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new WorkOrderNotFoundException(workOrderNumber));
    }

    @Override
    public WorkOrder createWorkOrder(CreateWorkOrderCommand command) {
        String workOrderNumber = generateWorkOrderNumber();

        WorkOrder workOrder = WorkOrder.builder()
            .workOrderNumber(workOrderNumber)
            .orderNumber(command.getOrderNumber())
            .workOrderDate(LocalDate.now())
            .itemCode(command.getItemCode())
            .orderQuantity(command.getOrderQuantity())
            .locationCode(command.getLocationCode())
            .plannedStartDate(command.getPlannedStartDate())
            .plannedEndDate(command.getPlannedEndDate())
            .completedQuantity(BigDecimal.ZERO)
            .totalGoodQuantity(BigDecimal.ZERO)
            .totalDefectQuantity(BigDecimal.ZERO)
            .status(WorkOrderStatus.NOT_STARTED)
            .completedFlag(false)
            .remarks(command.getRemarks())
            .build();

        workOrderRepository.save(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder recordCompletion(String workOrderNumber, RecordCompletionCommand command) {
        WorkOrder workOrder = workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new WorkOrderNotFoundException(workOrderNumber));

        boolean updated = workOrderRepository.updateCompletionQuantities(
            workOrderNumber,
            command.getCompletedQuantity(),
            command.getGoodQuantity(),
            command.getDefectQuantity(),
            workOrder.getVersion());

        if (!updated) {
            throw new IllegalStateException("楽観ロックエラー: 作業指示が他のユーザーによって更新されました");
        }

        return workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new WorkOrderNotFoundException(workOrderNumber));
    }

    @Override
    public WorkOrder updateProgress(String workOrderNumber, WorkOrderStatus status) {
        WorkOrder workOrder = workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new WorkOrderNotFoundException(workOrderNumber));

        workOrder.setStatus(status);
        if (status == WorkOrderStatus.IN_PROGRESS && workOrder.getActualStartDate() == null) {
            workOrder.setActualStartDate(LocalDate.now());
        }
        if (status == WorkOrderStatus.COMPLETED) {
            workOrder.setActualEndDate(LocalDate.now());
            workOrder.setCompletedFlag(true);
        }

        workOrderRepository.save(workOrder);
        return workOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> getWorkOrdersByStatus(WorkOrderStatus status) {
        return workOrderRepository.findByStatus(status);
    }

    private String generateWorkOrderNumber() {
        return "WO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
    }
}
