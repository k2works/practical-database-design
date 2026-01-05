package com.example.sms.domain.model.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 顧客エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private String customerCode;
    @Builder.Default
    private String customerBranchNumber = "00";
    private String customerCategory;
    private String billingCode;
    private String billingBranchNumber;
    private String collectionCode;
    private String collectionBranchNumber;
    private String customerName;
    private String customerNameKana;
    private String ourRepresentativeCode;
    private String customerRepresentativeName;
    private String customerDepartmentName;
    private String customerPostalCode;
    private String customerPrefecture;
    private String customerAddress1;
    private String customerAddress2;
    private String customerPhone;
    private String customerFax;
    private String customerEmail;
    @Builder.Default
    private BillingType billingType = BillingType.PERIODIC;
    private Integer closingDay1;
    private Integer paymentMonth1;
    private Integer paymentDay1;
    private PaymentMethod paymentMethod1;
    private Integer closingDay2;
    private Integer paymentMonth2;
    private Integer paymentDay2;
    private PaymentMethod paymentMethod2;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
