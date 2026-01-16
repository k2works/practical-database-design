package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.item.ItemCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 品目登録コマンド.
 */
public record CreateItemCommand(
    String itemCode,
    String itemName,
    ItemCategory itemCategory,
    String unitCode,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    Integer leadTime,
    Integer safetyLeadTime,
    BigDecimal safetyStock,
    BigDecimal yieldRate,
    BigDecimal minLotSize,
    BigDecimal lotIncrement,
    BigDecimal maxLotSize,
    Integer shelfLife
) {
}
