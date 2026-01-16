package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 標準原価マスタエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardCost {
    private Integer id;
    private String itemCode;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private BigDecimal standardMaterialCost;
    private BigDecimal standardLaborCost;
    private BigDecimal standardExpense;
    private BigDecimal standardManufacturingCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    /**
     * 指定日時点で有効かチェック.
     */
    public boolean isValidAt(LocalDate targetDate) {
        if (targetDate.isBefore(effectiveStartDate)) {
            return false;
        }
        return effectiveEndDate == null || !targetDate.isAfter(effectiveEndDate);
    }

    /**
     * 標準製造原価を計算.
     */
    public BigDecimal calculateStandardManufacturingCost() {
        return standardMaterialCost.add(standardLaborCost).add(standardExpense);
    }
}
