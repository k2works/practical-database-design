package com.example.pms.domain.model.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 完成検査結果データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionResult {
    private Integer id;
    private String completionResultNumber;
    private String defectCode;
    private BigDecimal quantity;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private CompletionResult completionResult;
}
