package com.example.pms.domain.model.quality;

import com.example.pms.domain.model.defect.Defect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 工程検査結果データエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInspectionResult {
    private Integer id;
    private String inspectionNumber;
    private String defectCode;
    private BigDecimal quantity;
    private String remarks;

    // リレーション
    private Defect defect;
}
