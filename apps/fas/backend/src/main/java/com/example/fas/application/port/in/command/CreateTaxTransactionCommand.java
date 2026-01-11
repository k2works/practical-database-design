package com.example.fas.application.port.in.command;

import java.math.BigDecimal;

/**
 * 課税取引登録コマンド.
 */
public record CreateTaxTransactionCommand(
        String taxCode,
        String taxName,
        BigDecimal taxRate
) {
}
