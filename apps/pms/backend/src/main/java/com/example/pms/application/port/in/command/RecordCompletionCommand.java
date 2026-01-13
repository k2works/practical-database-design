package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * 完成実績登録コマンド.
 */
@Value
@Builder
public class RecordCompletionCommand {
    BigDecimal completedQuantity;
    BigDecimal goodQuantity;
    BigDecimal defectQuantity;
}
