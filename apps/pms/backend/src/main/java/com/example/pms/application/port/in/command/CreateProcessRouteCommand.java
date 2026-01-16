package com.example.pms.application.port.in.command;

import java.math.BigDecimal;

/**
 * 工程表登録コマンド.
 */
public record CreateProcessRouteCommand(
    String itemCode,
    Integer sequence,
    String processCode,
    BigDecimal standardTime,
    BigDecimal setupTime
) {
}
