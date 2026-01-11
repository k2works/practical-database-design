package com.example.pms.domain.model.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private String supplierCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String supplierName;
    private String supplierNameKana;
    private SupplierType supplierType;
    private String postalCode;
    private String address;
    private String phoneNumber;
    private String faxNumber;
    private String contactPerson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
