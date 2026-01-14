package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.process.LaborHours;
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
 * 工数実績フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaborHoursForm {

    private Integer id;

    private String laborHoursNumber;

    @NotBlank(message = "作業指示番号は必須です")
    private String workOrderNumber;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    private Integer sequence;

    private String processCode;

    private String departmentCode;

    private String employeeCode;

    @NotNull(message = "作業日は必須です")
    private LocalDate workDate;

    @NotNull(message = "工数は必須です")
    @PositiveOrZero(message = "工数は0以上である必要があります")
    private BigDecimal hours;

    private String remarks;

    private Integer version;

    /**
     * フォームをエンティティに変換する.
     *
     * @return LaborHours エンティティ
     */
    public LaborHours toEntity() {
        return LaborHours.builder()
            .id(this.id)
            .laborHoursNumber(this.laborHoursNumber)
            .workOrderNumber(this.workOrderNumber)
            .itemCode(this.itemCode)
            .sequence(this.sequence)
            .processCode(this.processCode)
            .departmentCode(this.departmentCode)
            .employeeCode(this.employeeCode)
            .workDate(this.workDate)
            .hours(this.hours)
            .remarks(this.remarks)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param laborHours LaborHours エンティティ
     * @return LaborHoursForm
     */
    public static LaborHoursForm fromEntity(LaborHours laborHours) {
        return LaborHoursForm.builder()
            .id(laborHours.getId())
            .laborHoursNumber(laborHours.getLaborHoursNumber())
            .workOrderNumber(laborHours.getWorkOrderNumber())
            .itemCode(laborHours.getItemCode())
            .sequence(laborHours.getSequence())
            .processCode(laborHours.getProcessCode())
            .departmentCode(laborHours.getDepartmentCode())
            .employeeCode(laborHours.getEmployeeCode())
            .workDate(laborHours.getWorkDate())
            .hours(laborHours.getHours())
            .remarks(laborHours.getRemarks())
            .version(laborHours.getVersion())
            .build();
    }
}
