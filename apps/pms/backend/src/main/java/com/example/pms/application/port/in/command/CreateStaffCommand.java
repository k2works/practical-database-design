package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * 担当者登録コマンド.
 */
@Value
@Builder
public class CreateStaffCommand {
    String staffCode;
    LocalDate effectiveFrom;
    LocalDate effectiveTo;
    String staffName;
    String departmentCode;
    String email;
    String phoneNumber;
}
