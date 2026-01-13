package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作業指示レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderResponse {
    Integer id;
    String workOrderNumber;
    String orderNumber;
    LocalDate workOrderDate;
    String itemCode;
    BigDecimal orderQuantity;
    String locationCode;
    LocalDate plannedStartDate;
    LocalDate plannedEndDate;
    LocalDate actualStartDate;
    LocalDate actualEndDate;
    BigDecimal completedQuantity;
    BigDecimal totalGoodQuantity;
    BigDecimal totalDefectQuantity;
    WorkOrderStatus status;
    String statusDisplayName;
    Boolean completedFlag;
    String remarks;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param workOrder 作業指示
     * @return WorkOrderResponse
     */
    public static WorkOrderResponse from(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
            .id(workOrder.getId())
            .workOrderNumber(workOrder.getWorkOrderNumber())
            .orderNumber(workOrder.getOrderNumber())
            .workOrderDate(workOrder.getWorkOrderDate())
            .itemCode(workOrder.getItemCode())
            .orderQuantity(workOrder.getOrderQuantity())
            .locationCode(workOrder.getLocationCode())
            .plannedStartDate(workOrder.getPlannedStartDate())
            .plannedEndDate(workOrder.getPlannedEndDate())
            .actualStartDate(workOrder.getActualStartDate())
            .actualEndDate(workOrder.getActualEndDate())
            .completedQuantity(workOrder.getCompletedQuantity())
            .totalGoodQuantity(workOrder.getTotalGoodQuantity())
            .totalDefectQuantity(workOrder.getTotalDefectQuantity())
            .status(workOrder.getStatus())
            .statusDisplayName(workOrder.getStatus() != null ? workOrder.getStatus().getDisplayName() : null)
            .completedFlag(workOrder.getCompletedFlag())
            .remarks(workOrder.getRemarks())
            .createdAt(workOrder.getCreatedAt())
            .updatedAt(workOrder.getUpdatedAt())
            .build();
    }
}
