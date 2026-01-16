package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * 部門更新コマンド.
 */
@Value
@Builder
public class UpdateDepartmentCommand {
    String departmentName;
    String departmentPath;
    Boolean lowestLevel;
    LocalDate validFrom;
    LocalDate validTo;
}
