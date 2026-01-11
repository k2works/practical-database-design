package com.example.pms.domain.model.unitprice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 単価マスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitPrice {
    private String itemCode;
    private String supplierCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal price;
    private String currencyCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
