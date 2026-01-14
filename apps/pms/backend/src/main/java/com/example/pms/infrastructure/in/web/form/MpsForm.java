package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 基準生産計画フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpsForm {

    private Integer id;

    private String mpsNumber;

    @NotNull(message = "計画日は必須です")
    private LocalDate planDate;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "計画数量は必須です")
    @Positive(message = "計画数量は正の数である必要があります")
    private BigDecimal planQuantity;

    @NotNull(message = "納期は必須です")
    private LocalDate dueDate;

    private String locationCode;

    private String remarks;

    private PlanStatus status;

    private Integer version;

    /**
     * フォームをエンティティに変換する.
     *
     * @return MasterProductionSchedule エンティティ
     */
    public MasterProductionSchedule toEntity() {
        return MasterProductionSchedule.builder()
            .id(this.id)
            .mpsNumber(this.mpsNumber)
            .planDate(this.planDate)
            .itemCode(this.itemCode)
            .planQuantity(this.planQuantity)
            .dueDate(this.dueDate)
            .status(this.status)
            .locationCode(this.locationCode)
            .remarks(this.remarks)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param mps MasterProductionSchedule エンティティ
     * @return MpsForm
     */
    public static MpsForm fromEntity(MasterProductionSchedule mps) {
        return MpsForm.builder()
            .id(mps.getId())
            .mpsNumber(mps.getMpsNumber())
            .planDate(mps.getPlanDate())
            .itemCode(mps.getItemCode())
            .planQuantity(mps.getPlanQuantity())
            .dueDate(mps.getDueDate())
            .status(mps.getStatus())
            .locationCode(mps.getLocationCode())
            .remarks(mps.getRemarks())
            .version(mps.getVersion())
            .build();
    }
}
