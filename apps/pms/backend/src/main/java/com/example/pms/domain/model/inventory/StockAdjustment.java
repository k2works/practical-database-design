package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 在庫調整データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustment {
    private Integer id;
    private String adjustmentNumber;
    private String stocktakingNumber;
    private String itemCode;
    private String locationCode;
    private LocalDate adjustmentDate;
    private String adjusterCode;
    private BigDecimal adjustmentQuantity;
    private String reasonCode;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
