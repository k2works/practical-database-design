package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ProcessInspection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工程検査（不良管理）フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInspectionForm {

    private String inspectionNumber;

    @NotBlank(message = "作業指示番号は必須です")
    private String workOrderNumber;

    @NotBlank(message = "工程コードは必須です")
    private String processCode;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "検査日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate inspectionDate;

    private String inspectorCode;

    @NotNull(message = "検査数量は必須です")
    private BigDecimal inspectionQuantity;

    @NotNull(message = "合格数は必須です")
    private BigDecimal passedQuantity;

    @NotNull(message = "不合格数は必須です")
    private BigDecimal failedQuantity;

    @NotNull(message = "判定は必須です")
    private InspectionJudgment judgment;

    private String remarks;

    /**
     * フォームをエンティティに変換する.
     *
     * @return ProcessInspection エンティティ
     */
    public ProcessInspection toEntity() {
        return ProcessInspection.builder()
            .inspectionNumber(this.inspectionNumber)
            .workOrderNumber(this.workOrderNumber)
            .processCode(this.processCode)
            .itemCode(this.itemCode)
            .inspectionDate(this.inspectionDate)
            .inspectorCode(this.inspectorCode)
            .inspectionQuantity(this.inspectionQuantity)
            .passedQuantity(this.passedQuantity)
            .failedQuantity(this.failedQuantity)
            .judgment(this.judgment)
            .remarks(this.remarks)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param inspection ProcessInspection エンティティ
     * @return ProcessInspectionForm
     */
    public static ProcessInspectionForm fromEntity(ProcessInspection inspection) {
        return ProcessInspectionForm.builder()
            .inspectionNumber(inspection.getInspectionNumber())
            .workOrderNumber(inspection.getWorkOrderNumber())
            .processCode(inspection.getProcessCode())
            .itemCode(inspection.getItemCode())
            .inspectionDate(inspection.getInspectionDate())
            .inspectorCode(inspection.getInspectorCode())
            .inspectionQuantity(inspection.getInspectionQuantity())
            .passedQuantity(inspection.getPassedQuantity())
            .failedQuantity(inspection.getFailedQuantity())
            .judgment(inspection.getJudgment())
            .remarks(inspection.getRemarks())
            .build();
    }
}
