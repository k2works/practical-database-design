package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 単価更新コマンド.
 */
@Value
@Builder
public class UpdateUnitPriceCommand {
    LocalDate effectiveTo;
    BigDecimal price;
    String currencyCode;
}
