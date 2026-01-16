package com.example.pms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 単価更新コマンド.
 */
public record UpdateUnitPriceCommand(
    LocalDate effectiveTo,
    BigDecimal price,
    String currencyCode
) {
}
