package com.example.sms.application.port.in.command;

/**
 * 仕入先作成コマンド.
 */
public record CreateSupplierCommand(
    String supplierCode,
    String supplierBranchNumber,
    String representativeName,
    String departmentName,
    String phone,
    String fax,
    String email
) {}
