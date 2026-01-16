package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.cost.ActualCost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 製造原価フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualCostForm {

    @NotBlank(message = "作業指示番号は必須です")
    private String workOrderNumber;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "完成数量は必須です")
    private BigDecimal completedQuantity;

    @NotNull(message = "実際材料費は必須です")
    private BigDecimal actualMaterialCost;

    @NotNull(message = "実際労務費は必須です")
    private BigDecimal actualLaborCost;

    @NotNull(message = "実際経費は必須です")
    private BigDecimal actualExpense;

    private String remarks;

    /**
     * フォームをエンティティに変換する.
     *
     * @return ActualCost エンティティ
     */
    public ActualCost toEntity() {
        return ActualCost.builder()
            .workOrderNumber(this.workOrderNumber)
            .itemCode(this.itemCode)
            .completedQuantity(this.completedQuantity)
            .actualMaterialCost(this.actualMaterialCost)
            .actualLaborCost(this.actualLaborCost)
            .actualExpense(this.actualExpense)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param actualCost ActualCost エンティティ
     * @return ActualCostForm
     */
    public static ActualCostForm fromEntity(ActualCost actualCost) {
        return ActualCostForm.builder()
            .workOrderNumber(actualCost.getWorkOrderNumber())
            .itemCode(actualCost.getItemCode())
            .completedQuantity(actualCost.getCompletedQuantity())
            .actualMaterialCost(actualCost.getActualMaterialCost())
            .actualLaborCost(actualCost.getActualLaborCost())
            .actualExpense(actualCost.getActualExpense())
            .build();
    }
}
