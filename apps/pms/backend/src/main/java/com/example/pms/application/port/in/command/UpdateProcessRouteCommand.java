package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * 工程表更新コマンド.
 */
@Value
@Builder
public class UpdateProcessRouteCommand {
    String processCode;
    BigDecimal standardTime;
    BigDecimal setupTime;
}
