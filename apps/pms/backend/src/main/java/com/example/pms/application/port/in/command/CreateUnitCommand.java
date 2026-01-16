package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

/**
 * 単位登録コマンド.
 */
@Value
@Builder
public class CreateUnitCommand {
    String unitCode;
    String unitSymbol;
    String unitName;
}
