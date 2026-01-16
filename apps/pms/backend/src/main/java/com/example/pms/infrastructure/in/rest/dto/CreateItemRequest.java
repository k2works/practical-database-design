package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.command.CreateItemCommand;
import com.example.pms.domain.model.item.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 品目登録リクエスト DTO.
 */
@Data
public class CreateItemRequest {

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotBlank(message = "品目名は必須です")
    private String itemName;

    @NotNull(message = "品目区分は必須です")
    private ItemCategory itemCategory;

    @NotBlank(message = "単位コードは必須です")
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
     * @return CreateItemCommand
     */
    public CreateItemCommand toCommand() {
        return new CreateItemCommand(
            itemCode,
            itemName,
            itemCategory,
            unitCode,
            effectiveFrom,
            effectiveTo,
            leadTime,
            safetyLeadTime,
            safetyStock,
            yieldRate,
            minLotSize,
            lotIncrement,
            maxLotSize,
            shelfLife
        );
    }
}
