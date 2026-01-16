package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

/**
 * 単位更新コマンド.
 */
@Value
@Builder
public class UpdateUnitCommand {
    String unitSymbol;
    String unitName;
}
