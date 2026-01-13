package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.command.UpdateItemCommand;
import com.example.pms.domain.model.item.ItemCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 品目更新リクエスト DTO.
 */
@Data
public class UpdateItemRequest {

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

    /**
     * コマンドに変換する.
     *
     * @return UpdateItemCommand
     */
    public UpdateItemCommand toCommand() {
        return UpdateItemCommand.builder()
            .itemName(itemName)
            .itemCategory(itemCategory)
            .unitCode(unitCode)
            .effectiveFrom(effectiveFrom)
            .effectiveTo(effectiveTo)
            .leadTime(leadTime)
            .safetyLeadTime(safetyLeadTime)
            .safetyStock(safetyStock)
            .yieldRate(yieldRate)
            .minLotSize(minLotSize)
            .lotIncrement(lotIncrement)
            .maxLotSize(maxLotSize)
            .shelfLife(shelfLife)
            .build();
    }
}
