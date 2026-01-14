package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作業指示フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderForm {

    private Integer id;

    private String workOrderNumber;

    @NotBlank(message = "オーダ番号は必須です")
    private String orderNumber;

    @NotNull(message = "作業指示日は必須です")
    private LocalDate workOrderDate;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "作業指示数は必須です")
    @Positive(message = "作業指示数は正の数である必要があります")
    private BigDecimal orderQuantity;

    @NotBlank(message = "場所コードは必須です")
    private String locationCode;

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private BigDecimal completedQuantity;

    private BigDecimal totalGoodQuantity;

    private BigDecimal totalDefectQuantity;

    @NotNull(message = "ステータスは必須です")
    private WorkOrderStatus status;

    @Builder.Default
    private Boolean completedFlag = false;

    private String remarks;

    private Integer version;

    /**
     * フォームをエンティティに変換する.
     *
     * @return WorkOrder エンティティ
     */
    public WorkOrder toEntity() {
        return WorkOrder.builder()
            .id(this.id)
            .workOrderNumber(this.workOrderNumber)
            .orderNumber(this.orderNumber)
            .workOrderDate(this.workOrderDate)
            .itemCode(this.itemCode)
            .orderQuantity(this.orderQuantity)
            .locationCode(this.locationCode)
            .plannedStartDate(this.plannedStartDate)
            .plannedEndDate(this.plannedEndDate)
            .actualStartDate(this.actualStartDate)
            .actualEndDate(this.actualEndDate)
            .completedQuantity(this.completedQuantity)
            .totalGoodQuantity(this.totalGoodQuantity)
            .totalDefectQuantity(this.totalDefectQuantity)
            .status(this.status)
            .completedFlag(this.completedFlag)
            .remarks(this.remarks)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param workOrder WorkOrder エンティティ
     * @return WorkOrderForm
     */
    public static WorkOrderForm fromEntity(WorkOrder workOrder) {
        return WorkOrderForm.builder()
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
            .completedFlag(workOrder.getCompletedFlag())
            .remarks(workOrder.getRemarks())
            .version(workOrder.getVersion())
            .build();
    }
}
