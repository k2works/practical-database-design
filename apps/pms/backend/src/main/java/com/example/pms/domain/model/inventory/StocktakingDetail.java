package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 棚卸明細データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingDetail {
    private Integer id;
    private String stocktakingNumber;
    private Integer lineNumber;
    private String itemCode;
    private BigDecimal bookQuantity;
    private BigDecimal actualQuantity;
    private BigDecimal differenceQuantity;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
