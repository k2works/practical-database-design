package com.example.pms.application.service;

import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.application.port.in.command.CreateWorkOrderCommand;
import com.example.pms.application.port.in.command.RecordCompletionCommand;
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
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        String workOrderNumber = generateWorkOrderNumber();
        workOrder.setWorkOrderNumber(workOrderNumber);
        workOrder.setCreatedBy("system");
        workOrder.setUpdatedBy("system");

        // 空文字を null に変換（外部キー制約対応）
        if (workOrder.getLocationCode() != null && workOrder.getLocationCode().isEmpty()) {
            workOrder.setLocationCode(null);
        }

        if (workOrder.getCompletedQuantity() == null) {
            workOrder.setCompletedQuantity(BigDecimal.ZERO);
        }
        if (workOrder.getTotalGoodQuantity() == null) {
            workOrder.setTotalGoodQuantity(BigDecimal.ZERO);
        }
        if (workOrder.getTotalDefectQuantity() == null) {
            workOrder.setTotalDefectQuantity(BigDecimal.ZERO);
        }
        if (workOrder.getStatus() == null) {
            workOrder.setStatus(WorkOrderStatus.NOT_STARTED);
        }
        if (workOrder.getCompletedFlag() == null) {
            workOrder.setCompletedFlag(false);
        }
        if (workOrder.getVersion() == null) {
            workOrder.setVersion(1);
        }

        workOrderRepository.save(workOrder);
        return workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new IllegalStateException("作業指示の登録に失敗しました"));
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

    @Override
    public WorkOrder updateWorkOrder(String workOrderNumber, WorkOrder workOrder) {
        WorkOrder existing = workOrderRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new WorkOrderNotFoundException(workOrderNumber));

        existing.setOrderNumber(workOrder.getOrderNumber());
        existing.setWorkOrderDate(workOrder.getWorkOrderDate());
        existing.setItemCode(workOrder.getItemCode());
        existing.setOrderQuantity(workOrder.getOrderQuantity());
        // 空文字を null に変換（外部キー制約対応）
        String locationCode = workOrder.getLocationCode();
        existing.setLocationCode(locationCode != null && locationCode.isEmpty() ? null : locationCode);
        existing.setPlannedStartDate(workOrder.getPlannedStartDate());
        existing.setPlannedEndDate(workOrder.getPlannedEndDate());
        existing.setActualStartDate(workOrder.getActualStartDate());
        existing.setActualEndDate(workOrder.getActualEndDate());
        existing.setCompletedQuantity(workOrder.getCompletedQuantity());
        existing.setTotalGoodQuantity(workOrder.getTotalGoodQuantity());
        existing.setTotalDefectQuantity(workOrder.getTotalDefectQuantity());
        existing.setStatus(workOrder.getStatus());
        existing.setCompletedFlag(workOrder.getCompletedFlag());
        existing.setRemarks(workOrder.getRemarks());
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
