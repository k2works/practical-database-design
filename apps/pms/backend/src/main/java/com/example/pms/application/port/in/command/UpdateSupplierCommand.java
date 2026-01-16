package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.supplier.SupplierType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * 取引先更新コマンド.
 */
@Value
@Builder
public class UpdateSupplierCommand {
    LocalDate effectiveTo;
    String supplierName;
    String supplierNameKana;
    SupplierType supplierType;
    String postalCode;
    String address;
    String phoneNumber;
    String faxNumber;
    String contactPerson;
}
