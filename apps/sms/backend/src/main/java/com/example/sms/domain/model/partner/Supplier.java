package com.example.sms.domain.model.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 仕入先エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private String supplierCode;
    @Builder.Default
    private String supplierBranchNumber = "00";
    private String representativeName;
    private String departmentName;
    private String phone;
    private String fax;
    private String email;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
