package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * 部門登録コマンド.
 */
@Value
@Builder
public class CreateDepartmentCommand {
    String departmentCode;
    String departmentName;
    String departmentPath;
    Boolean lowestLevel;
    LocalDate validFrom;
    LocalDate validTo;
}
