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
 * 完成実績データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionResult {
    private Integer id;
    private String completionResultNumber;
    private String workOrderNumber;
    private String itemCode;
    private LocalDate completionDate;
    private BigDecimal completedQuantity;
    private BigDecimal goodQuantity;
    private BigDecimal defectQuantity;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private WorkOrder workOrder;
    private List<InspectionResult> inspectionResults;
}
