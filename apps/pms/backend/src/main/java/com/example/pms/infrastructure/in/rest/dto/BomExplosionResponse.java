package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.bom.BomExplosion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * BOM 展開結果レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomExplosionResponse {
    Integer level;
    String parentItemCode;
    String childItemCode;
    LocalDate effectiveFrom;
    LocalDate effectiveTo;
    BigDecimal baseQuantity;
    BigDecimal requiredQuantity;
    BigDecimal totalQuantity;
    BigDecimal defectRate;
    Integer sequence;

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param explosion BOM 展開結果
     * @return BomExplosionResponse
     */
    public static BomExplosionResponse from(BomExplosion explosion) {
        return BomExplosionResponse.builder()
            .level(explosion.getLevel())
            .parentItemCode(explosion.getParentItemCode())
            .childItemCode(explosion.getChildItemCode())
            .effectiveFrom(explosion.getEffectiveFrom())
            .effectiveTo(explosion.getEffectiveTo())
            .baseQuantity(explosion.getBaseQuantity())
            .requiredQuantity(explosion.getRequiredQuantity())
            .totalQuantity(explosion.getTotalQuantity())
            .defectRate(explosion.getDefectRate())
            .sequence(explosion.getSequence())
            .build();
    }
}
