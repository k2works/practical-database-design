package com.example.sms.domain.model.inventory;

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
    private Integer stocktakingId;
    private Integer lineNumber;
    private String productCode;
    private String locationCode;
    private String lotNumber;
    private BigDecimal bookQuantity;
    private BigDecimal actualQuantity;
    private BigDecimal differenceQuantity;
    private String differenceReason;
    private Boolean adjustedFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 実棚数を入力.
     */
    public void setActualQuantityAndCalculateDifference(BigDecimal actualQty) {
        this.actualQuantity = actualQty;
        this.differenceQuantity = actualQty.subtract(this.bookQuantity);
    }

    /**
     * 差異があるかどうか.
     */
    public boolean hasDifference() {
        return differenceQuantity != null && differenceQuantity.signum() != 0;
    }
}
