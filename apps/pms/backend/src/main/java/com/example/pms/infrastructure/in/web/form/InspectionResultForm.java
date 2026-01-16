package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.process.InspectionResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 検査実績フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionResultForm {

    private Integer id;

    @NotBlank(message = "完成実績番号は必須です")
    private String completionResultNumber;

    @NotBlank(message = "欠点コードは必須です")
    private String defectCode;

    @PositiveOrZero(message = "数量は0以上である必要があります")
    private BigDecimal quantity;

    /**
     * フォームをエンティティに変換する.
     *
     * @return InspectionResult エンティティ
     */
    public InspectionResult toEntity() {
        return InspectionResult.builder()
            .id(this.id)
            .completionResultNumber(this.completionResultNumber)
            .defectCode(this.defectCode)
            .quantity(this.quantity)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param inspectionResult InspectionResult エンティティ
     * @return InspectionResultForm
     */
    public static InspectionResultForm fromEntity(InspectionResult inspectionResult) {
        return InspectionResultForm.builder()
            .id(inspectionResult.getId())
            .completionResultNumber(inspectionResult.getCompletionResultNumber())
            .defectCode(inspectionResult.getDefectCode())
            .quantity(inspectionResult.getQuantity())
            .build();
    }
}
