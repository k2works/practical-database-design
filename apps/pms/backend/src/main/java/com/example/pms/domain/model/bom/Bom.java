package com.example.pms.domain.model.bom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.ShortClassName")
public class Bom {
    private String parentItemCode;
    private String childItemCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    @Builder.Default
    private BigDecimal baseQuantity = BigDecimal.ONE;
    private BigDecimal requiredQuantity;
    @Builder.Default
    private BigDecimal defectRate = BigDecimal.ZERO;
    private Integer sequence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
