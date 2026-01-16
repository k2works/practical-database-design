package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.process.CompletionResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 完成実績フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionResultForm {

    private Integer id;

    private String completionResultNumber;

    @NotBlank(message = "作業指示番号は必須です")
    private String workOrderNumber;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "完成日は必須です")
    private LocalDate completionDate;

    @NotNull(message = "完成数量は必須です")
    @PositiveOrZero(message = "完成数量は0以上である必要があります")
    private BigDecimal completedQuantity;

    @PositiveOrZero(message = "良品数は0以上である必要があります")
    private BigDecimal goodQuantity;

    @PositiveOrZero(message = "不良品数は0以上である必要があります")
    private BigDecimal defectQuantity;

    private String remarks;

    private Integer version;

    /**
     * フォームをエンティティに変換する.
     *
     * @return CompletionResult エンティティ
     */
    public CompletionResult toEntity() {
        return CompletionResult.builder()
            .id(this.id)
            .completionResultNumber(this.completionResultNumber)
            .workOrderNumber(this.workOrderNumber)
            .itemCode(this.itemCode)
            .completionDate(this.completionDate)
            .completedQuantity(this.completedQuantity)
            .goodQuantity(this.goodQuantity)
            .defectQuantity(this.defectQuantity)
            .remarks(this.remarks)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param completionResult CompletionResult エンティティ
     * @return CompletionResultForm
     */
    public static CompletionResultForm fromEntity(CompletionResult completionResult) {
        return CompletionResultForm.builder()
            .id(completionResult.getId())
            .completionResultNumber(completionResult.getCompletionResultNumber())
            .workOrderNumber(completionResult.getWorkOrderNumber())
            .itemCode(completionResult.getItemCode())
            .completionDate(completionResult.getCompletionDate())
            .completedQuantity(completionResult.getCompletedQuantity())
            .goodQuantity(completionResult.getGoodQuantity())
            .defectQuantity(completionResult.getDefectQuantity())
            .remarks(completionResult.getRemarks())
            .version(completionResult.getVersion())
            .build();
    }
}
