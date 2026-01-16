package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 単価登録コマンド.
 */
@Value
@Builder
public class CreateUnitPriceCommand {
    String itemCode;
    String supplierCode;
    LocalDate effectiveFrom;
    LocalDate effectiveTo;
    BigDecimal price;
    String currencyCode;
}
