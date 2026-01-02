package com.example.sms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 棚卸データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stocktaking {
    private Integer id;
    private String stocktakingNumber;
    private String warehouseCode;
    private LocalDate stocktakingDate;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private StocktakingStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<StocktakingDetail> details = new ArrayList<>();

    /**
     * 差異がある明細があるかどうか.
     */
    public boolean hasDifference() {
        return details.stream()
                .anyMatch(d -> d.getDifferenceQuantity() != null &&
                         d.getDifferenceQuantity().signum() != 0);
    }

    /**
     * 全明細が調整済みかどうか.
     */
    public boolean isAllAdjusted() {
        return details.stream()
                .allMatch(StocktakingDetail::getAdjustedFlag);
    }
}
