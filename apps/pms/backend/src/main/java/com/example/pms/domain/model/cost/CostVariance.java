package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 原価差異データエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostVariance {
    private Integer id;
    private String workOrderNumber;
    private String itemCode;
    private BigDecimal materialCostVariance;
    private BigDecimal laborCostVariance;
    private BigDecimal expenseVariance;
    private BigDecimal totalVariance;
    private LocalDateTime createdAt;

    /**
     * 有利差異かチェック（マイナスの場合は有利）.
     */
    public boolean isFavorable() {
        return totalVariance != null && totalVariance.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 不利差異かチェック（プラスの場合は不利）.
     */
    public boolean isUnfavorable() {
        return totalVariance != null && totalVariance.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 総差異を計算.
     */
    public BigDecimal calculateTotalVariance() {
        return materialCostVariance.add(laborCostVariance).add(expenseVariance);
    }
}
