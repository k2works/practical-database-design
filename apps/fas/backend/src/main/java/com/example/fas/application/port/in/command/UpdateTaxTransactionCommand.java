package com.example.fas.application.port.in.command;

import java.math.BigDecimal;

/**
 * 課税取引更新コマンド.
 */
public record UpdateTaxTransactionCommand(
        String taxName,
        BigDecimal taxRate
) {
}
