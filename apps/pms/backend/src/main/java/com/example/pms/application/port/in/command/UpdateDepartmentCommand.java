package com.example.pms.application.port.in.command;

import java.time.LocalDate;

/**
 * 部門更新コマンド.
 */
public record UpdateDepartmentCommand(
    String departmentName,
    String departmentPath,
    Boolean lowestLevel,
    LocalDate validFrom,
    LocalDate validTo
) {
}
