package com.example.pms.application.port.in.command;

/**
 * 工程登録コマンド.
 */
public record CreateProcessCommand(
    String processCode,
    String processName,
    String processType,
    String locationCode
) {
}
