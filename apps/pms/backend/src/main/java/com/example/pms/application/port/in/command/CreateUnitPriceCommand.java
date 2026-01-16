package com.example.pms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 単価登録コマンド.
 */
public record CreateUnitPriceCommand(
    String itemCode,
    String supplierCode,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    BigDecimal price,
    String currencyCode
) {
}
