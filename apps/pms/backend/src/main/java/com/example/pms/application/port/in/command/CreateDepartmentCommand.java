package com.example.pms.application.port.in.command;

import java.time.LocalDate;

/**
 * 部門登録コマンド.
 */
public record CreateDepartmentCommand(
    String departmentCode,
    String departmentName,
    String departmentPath,
    Boolean lowestLevel,
    LocalDate validFrom,
    LocalDate validTo
) {
}
