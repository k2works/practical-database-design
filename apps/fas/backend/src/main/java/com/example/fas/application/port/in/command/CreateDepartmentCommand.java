package com.example.fas.application.port.in.command;

/**
 * 部門登録コマンド.
 */
public record CreateDepartmentCommand(
        String departmentCode,
        String departmentName,
        String departmentShortName,
        Integer organizationLevel,
        String departmentPath,
        Integer lowestLevelFlag
) {
}
