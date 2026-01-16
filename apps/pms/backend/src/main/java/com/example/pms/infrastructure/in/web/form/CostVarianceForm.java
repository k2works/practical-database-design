package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.cost.CostVariance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 原価差異フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostVarianceForm {

    @NotBlank(message = "作業指示番号は必須です")
    private String workOrderNumber;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "材料費差異は必須です")
    private BigDecimal materialCostVariance;

    @NotNull(message = "労務費差異は必須です")
    private BigDecimal laborCostVariance;

    @NotNull(message = "経費差異は必須です")
    private BigDecimal expenseVariance;

    /**
     * フォームをエンティティに変換する.
     *
     * @return CostVariance エンティティ
     */
    public CostVariance toEntity() {
        return CostVariance.builder()
            .workOrderNumber(this.workOrderNumber)
            .itemCode(this.itemCode)
            .materialCostVariance(this.materialCostVariance)
            .laborCostVariance(this.laborCostVariance)
            .expenseVariance(this.expenseVariance)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param costVariance CostVariance エンティティ
     * @return CostVarianceForm
     */
    public static CostVarianceForm fromEntity(CostVariance costVariance) {
        return CostVarianceForm.builder()
            .workOrderNumber(costVariance.getWorkOrderNumber())
            .itemCode(costVariance.getItemCode())
            .materialCostVariance(costVariance.getMaterialCostVariance())
            .laborCostVariance(costVariance.getLaborCostVariance())
            .expenseVariance(costVariance.getExpenseVariance())
            .build();
    }
}
