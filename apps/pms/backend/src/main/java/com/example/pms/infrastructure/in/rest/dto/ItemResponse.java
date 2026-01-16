package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 品目レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Integer id;
    private String itemCode;
    private String itemName;
    private ItemCategory itemCategory;
    private String unitCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Integer leadTime;
    private Integer safetyLeadTime;
    private BigDecimal safetyStock;
    private BigDecimal yieldRate;
    private BigDecimal minLotSize;
    private BigDecimal lotIncrement;
    private BigDecimal maxLotSize;
    private Integer shelfLife;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param item 品目
     * @return ItemResponse
     */
    public static ItemResponse from(Item item) {
        return ItemResponse.builder()
            .id(item.getId())
            .itemCode(item.getItemCode())
            .itemName(item.getItemName())
            .itemCategory(item.getItemCategory())
            .unitCode(item.getUnitCode())
            .effectiveFrom(item.getEffectiveFrom())
            .effectiveTo(item.getEffectiveTo())
            .leadTime(item.getLeadTime())
            .safetyLeadTime(item.getSafetyLeadTime())
            .safetyStock(item.getSafetyStock())
            .yieldRate(item.getYieldRate())
            .minLotSize(item.getMinLotSize())
            .lotIncrement(item.getLotIncrement())
            .maxLotSize(item.getMaxLotSize())
            .shelfLife(item.getShelfLife())
            .createdAt(item.getCreatedAt())
            .updatedAt(item.getUpdatedAt())
            .build();
    }
}
