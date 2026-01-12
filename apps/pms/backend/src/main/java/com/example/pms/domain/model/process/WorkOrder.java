package com.example.pms.domain.model.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作業指示データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {
    private Integer id;
    private String workOrderNumber;
    private String orderNumber;
    private LocalDate workOrderDate;
    private String itemCode;
    private BigDecimal orderQuantity;
    private String locationCode;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private BigDecimal completedQuantity;
    private BigDecimal totalGoodQuantity;
    private BigDecimal totalDefectQuantity;
    private WorkOrderStatus status;
    private Boolean completedFlag;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private List<WorkOrderDetail> details;
}
