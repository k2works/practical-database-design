package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.bom.Bom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * BOM レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomResponse {
    String parentItemCode;
    String childItemCode;
    LocalDate effectiveFrom;
    LocalDate effectiveTo;
    BigDecimal baseQuantity;
    BigDecimal requiredQuantity;
    BigDecimal defectRate;
    Integer sequence;

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param bom BOM
     * @return BomResponse
     */
    public static BomResponse from(Bom bom) {
        return BomResponse.builder()
            .parentItemCode(bom.getParentItemCode())
            .childItemCode(bom.getChildItemCode())
            .effectiveFrom(bom.getEffectiveFrom())
            .effectiveTo(bom.getEffectiveTo())
            .baseQuantity(bom.getBaseQuantity())
            .requiredQuantity(bom.getRequiredQuantity())
            .defectRate(bom.getDefectRate())
            .sequence(bom.getSequence())
            .build();
    }
}
