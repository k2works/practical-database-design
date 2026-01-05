package com.example.sms.application.port.in.command;

/**
 * 仕入先更新コマンド.
 */
public record UpdateSupplierCommand(
    String representativeName,
    String departmentName,
    String phone,
    String fax,
    String email
) {}
