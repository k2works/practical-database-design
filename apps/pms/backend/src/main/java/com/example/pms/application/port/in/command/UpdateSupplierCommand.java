package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.supplier.SupplierType;

import java.time.LocalDate;

/**
 * 取引先更新コマンド.
 */
public record UpdateSupplierCommand(
    LocalDate effectiveTo,
    String supplierName,
    String supplierNameKana,
    SupplierType supplierType,
    String postalCode,
    String address,
    String phoneNumber,
    String faxNumber,
    String contactPerson
) {
}
