package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * 工程表登録コマンド.
 */
@Value
@Builder
public class CreateProcessRouteCommand {
    String itemCode;
    Integer sequence;
    String processCode;
    BigDecimal standardTime;
    BigDecimal setupTime;
}
