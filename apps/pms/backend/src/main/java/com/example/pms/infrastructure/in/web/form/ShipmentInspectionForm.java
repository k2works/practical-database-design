package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ShipmentInspection;
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
 * 出荷検査実績フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentInspectionForm {

    private String inspectionNumber;

    @NotBlank(message = "出荷番号は必須です")
    private String shipmentNumber;

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
     * @return ShipmentInspection エンティティ
     */
    public ShipmentInspection toEntity() {
        return ShipmentInspection.builder()
            .inspectionNumber(this.inspectionNumber)
            .shipmentNumber(this.shipmentNumber)
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
     * @param inspection ShipmentInspection エンティティ
     * @return ShipmentInspectionForm
     */
    public static ShipmentInspectionForm fromEntity(ShipmentInspection inspection) {
        return ShipmentInspectionForm.builder()
            .inspectionNumber(inspection.getInspectionNumber())
            .shipmentNumber(inspection.getShipmentNumber())
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
