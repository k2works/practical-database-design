package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.supplier.SupplierType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * 取引先登録コマンド.
 */
@Value
@Builder
public class CreateSupplierCommand {
    String supplierCode;
    LocalDate effectiveFrom;
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
