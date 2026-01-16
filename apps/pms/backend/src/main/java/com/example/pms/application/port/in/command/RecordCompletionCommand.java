package com.example.pms.application.port.in.command;

import java.math.BigDecimal;

/**
 * 完成実績登録コマンド.
 */
public record RecordCompletionCommand(
    BigDecimal completedQuantity,
    BigDecimal goodQuantity,
    BigDecimal defectQuantity
) {
}
