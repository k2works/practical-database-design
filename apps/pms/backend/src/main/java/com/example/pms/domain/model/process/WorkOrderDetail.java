package com.example.pms.domain.model.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作業指示明細データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetail {
    private Integer id;
    private String workOrderNumber;
    private Integer sequence;
    private String processCode;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private WorkOrder workOrder;
    private Process process;
}
