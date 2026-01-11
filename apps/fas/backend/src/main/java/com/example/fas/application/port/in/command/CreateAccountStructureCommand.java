package com.example.fas.application.port.in.command;

/**
 * 勘定科目構成登録コマンド.
 */
public record CreateAccountStructureCommand(
        String accountCode,
        String parentCode
) {
}
