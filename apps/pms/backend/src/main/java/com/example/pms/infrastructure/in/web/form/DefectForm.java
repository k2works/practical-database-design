package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.defect.Defect;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 欠点マスタフォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectForm {

    @NotBlank(message = "欠点コードは必須です")
    private String defectCode;

    @NotBlank(message = "欠点名は必須です")
    private String defectName;

    private String defectCategory;

    /**
     * フォームをエンティティに変換する.
     *
     * @return Defect エンティティ
     */
    public Defect toEntity() {
        return Defect.builder()
            .defectCode(this.defectCode)
            .defectName(this.defectName)
            .defectCategory(this.defectCategory)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param defect Defect エンティティ
     * @return DefectForm
     */
    public static DefectForm fromEntity(Defect defect) {
        return DefectForm.builder()
            .defectCode(defect.getDefectCode())
            .defectName(defect.getDefectName())
            .defectCategory(defect.getDefectCategory())
            .build();
    }
}
