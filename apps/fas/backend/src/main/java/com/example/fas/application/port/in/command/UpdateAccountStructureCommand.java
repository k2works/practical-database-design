package com.example.fas.application.port.in.command;

/**
 * 勘定科目構成更新コマンド.
 */
public record UpdateAccountStructureCommand(
        String parentCode
) {
}
