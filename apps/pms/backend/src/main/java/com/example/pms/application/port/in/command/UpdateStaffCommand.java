package com.example.pms.application.port.in.command;

import java.time.LocalDate;

/**
 * 担当者更新コマンド.
 */
public record UpdateStaffCommand(
    LocalDate effectiveTo,
    String staffName,
    String departmentCode,
    String email,
    String phoneNumber
) {
}
