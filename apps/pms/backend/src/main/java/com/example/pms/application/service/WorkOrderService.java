package com.example.pms.application.service;

import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.application.port.in.command.CreateWorkOrderCommand;
import com.example.pms.application.port.in.command.RecordCompletionCommand;
import com.example.pms.application.port.in.command.UpdateWorkOrderCommand;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.exception.WorkOrderNotFoundException;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
    public PageResult<WorkOrder> getWorkOrderList(int page, int size, String keyword) {
        int offset = page * size;
        List<WorkOrder> content = workOrderRepository.findWithPagination(offset, size, keyword);
        long totalElements = workOrderRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkOrder> getWorkOrder(String workOrderNumber) {
        return workOrderRepository.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public WorkOrder createWorkOrder(CreateWorkOrderCommand command) {
        String workOrderNumber = generateWorkOrderNumber();

        WorkOrder workOrder = WorkOrder.builder()
            .workOrderNumber(workOrderNumber)
            .orderNumber(command.orderNumber())
            .workOrderDate(LocalDate.now())
            .itemCode(command.itemCode())
            .orderQuantity(command.orderQuantity())
            .locationCode(command.locationCode())
            .plannedStartDate(command.plannedStartDate())
            .plannedEndDate(command.plannedEndDate())
            .completedQuantity(BigDecimal.ZERO)
            .totalGoodQuantity(BigDecimal.ZERO)
            .totalDefectQuantity(BigDecimal.ZERO)
            .status(WorkOrderStatus.NOT_STARTED)
            .completedFlag(false)
            .remarks(command.remarks())
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
            command.completedQuantity(),
            command.goodQuantity(),
            command.defectQuantity(),
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

    @Override
    public WorkOrder updateWorkOrder(String workOrderNumber, UpdateWorkOrderCommand command) {
        WorkOrder existing = workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new WorkOrderNotFoundException(workOrderNumber));

        existing.setOrderNumber(command.orderNumber());
        existing.setWorkOrderDate(command.workOrderDate());
        existing.setItemCode(command.itemCode());
        existing.setOrderQuantity(command.orderQuantity());
        // 空文字を null に変換（外部キー制約対応）
        String locationCode = command.locationCode();
        existing.setLocationCode(locationCode != null && locationCode.isEmpty() ? null : locationCode);
        existing.setPlannedStartDate(command.plannedStartDate());
        existing.setPlannedEndDate(command.plannedEndDate());
        existing.setActualStartDate(command.actualStartDate());
        existing.setActualEndDate(command.actualEndDate());
        existing.setCompletedQuantity(command.completedQuantity());
        existing.setTotalGoodQuantity(command.totalGoodQuantity());
        existing.setTotalDefectQuantity(command.totalDefectQuantity());
        existing.setStatus(command.status());
        existing.setCompletedFlag(command.completedFlag());
        existing.setRemarks(command.remarks());
        existing.setUpdatedBy("system");

        workOrderRepository.update(existing);
        return workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new IllegalStateException("作業指示の更新に失敗しました"));
    }

    @Override
    public void deleteWorkOrder(String workOrderNumber) {
        workOrderRepository.deleteByWorkOrderNumber(workOrderNumber);
    }

    private String generateWorkOrderNumber() {
        String datePrefix = "WO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<WorkOrder> allWorkOrders = workOrderRepository.findAll();

        int maxSeq = allWorkOrders.stream()
            .map(WorkOrder::getWorkOrderNumber)
            .filter(num -> num != null && num.startsWith(datePrefix))
            .map(num -> {
                String seqStr = num.substring(datePrefix.length());
                try {
                    return Integer.parseInt(seqStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max(Integer::compareTo)
            .orElse(0);

        return datePrefix + String.format("%04d", maxSeq + 1);
    }
}
