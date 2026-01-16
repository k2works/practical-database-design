package com.example.pms.application.port.in.command;

import java.time.LocalDate;

/**
 * 担当者登録コマンド.
 */
public record CreateStaffCommand(
    String staffCode,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    String staffName,
    String departmentCode,
    String email,
    String phoneNumber
) {
}
