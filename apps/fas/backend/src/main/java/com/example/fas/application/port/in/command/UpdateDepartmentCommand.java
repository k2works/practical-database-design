package com.example.fas.application.port.in.command;

/**
 * 部門更新コマンド.
 */
public record UpdateDepartmentCommand(
        String departmentName,
        String departmentShortName,
        Integer organizationLevel,
        String departmentPath,
        Integer lowestLevelFlag
) {
}
