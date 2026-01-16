package com.example.pms.application.port.in.command;

/**
 * 単位登録コマンド.
 */
public record CreateUnitCommand(
    String unitCode,
    String unitSymbol,
    String unitName
) {
}
