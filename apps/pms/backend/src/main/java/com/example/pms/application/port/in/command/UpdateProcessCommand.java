package com.example.pms.application.port.in.command;

/**
 * 工程更新コマンド.
 */
public record UpdateProcessCommand(
    String processName,
    String processType,
    String locationCode
) {
}
