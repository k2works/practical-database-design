package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.pms.domain.model.process.LaborHours;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 実際原価データエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualCost {
    private Integer id;
    private String workOrderNumber;
    private String itemCode;
    private BigDecimal completedQuantity;
    private BigDecimal actualMaterialCost;
    private BigDecimal actualLaborCost;
    private BigDecimal actualExpense;
    private BigDecimal actualManufacturingCost;
    private BigDecimal unitCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    @Builder.Default
    private List<MaterialConsumption> materialConsumptions = new ArrayList<>();
    @Builder.Default
    private List<LaborHours> laborHours = new ArrayList<>();
    @Builder.Default
    private List<OverheadAllocation> overheadAllocations = new ArrayList<>();

    /**
     * 実際製造原価を計算.
     */
    public BigDecimal calculateActualManufacturingCost() {
        return actualMaterialCost.add(actualLaborCost).add(actualExpense);
    }

    /**
     * 単位原価を計算.
     */
    public BigDecimal calculateUnitCost() {
        if (completedQuantity == null || completedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return actualManufacturingCost.divide(completedQuantity, 4, RoundingMode.HALF_UP);
    }

    /**
     * 原価再計算が必要かチェック.
     */
    public boolean needsRecalculation(BigDecimal newMaterialCost,
                                      BigDecimal newLaborCost,
                                      BigDecimal newExpense) {
        return !actualMaterialCost.equals(newMaterialCost)
                || !actualLaborCost.equals(newLaborCost)
                || !actualExpense.equals(newExpense);
    }
}
