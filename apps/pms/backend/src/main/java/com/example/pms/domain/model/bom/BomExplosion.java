package com.example.pms.domain.model.bom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomExplosion {
    private String parentItemCode;
    private String childItemCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal baseQuantity;
    private BigDecimal requiredQuantity;
    private BigDecimal defectRate;
    private Integer sequence;
    private Integer level;
    private BigDecimal totalQuantity;
}
