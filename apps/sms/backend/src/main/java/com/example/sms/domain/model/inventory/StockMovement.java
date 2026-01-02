package com.example.sms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入出庫履歴データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    private Integer id;
    private String warehouseCode;
    private String productCode;
    private LocalDateTime movementDateTime;
    private MovementType movementType;
    private BigDecimal movementQuantity;
    private BigDecimal beforeQuantity;
    private BigDecimal afterQuantity;
    private String documentNumber;
    private String documentType;
    private String movementReason;
    private String locationCode;
    private String lotNumber;
    private LocalDateTime createdAt;
    private String createdBy;
}
