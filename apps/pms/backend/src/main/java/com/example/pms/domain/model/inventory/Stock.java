package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 在庫情報.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private Integer id;
    private String locationCode;
    private String itemCode;
    private BigDecimal stockQuantity;
    private BigDecimal passedQuantity;
    private BigDecimal defectiveQuantity;
    private BigDecimal uninspectedQuantity;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public static Stock empty(String locationCode, String itemCode) {
        return Stock.builder()
                .locationCode(locationCode)
                .itemCode(itemCode)
                .stockQuantity(BigDecimal.ZERO)
                .passedQuantity(BigDecimal.ZERO)
                .defectiveQuantity(BigDecimal.ZERO)
                .uninspectedQuantity(BigDecimal.ZERO)
                .build();
    }
}
