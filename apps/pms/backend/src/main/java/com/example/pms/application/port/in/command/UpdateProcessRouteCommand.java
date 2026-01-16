package com.example.pms.application.port.in.command;

import java.math.BigDecimal;

/**
 * 工程表更新コマンド.
 */
public record UpdateProcessRouteCommand(
    String processCode,
    BigDecimal standardTime,
    BigDecimal setupTime
) {
}
