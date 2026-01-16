package com.example.pms.application.port.in.command;

/**
 * 単位更新コマンド.
 */
public record UpdateUnitCommand(
    String unitSymbol,
    String unitName
) {
}
