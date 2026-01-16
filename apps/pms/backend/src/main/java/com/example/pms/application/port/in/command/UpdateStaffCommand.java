package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * 担当者更新コマンド.
 */
@Value
@Builder
public class UpdateStaffCommand {
    LocalDate effectiveTo;
    String staffName;
    String departmentCode;
    String email;
    String phoneNumber;
}
