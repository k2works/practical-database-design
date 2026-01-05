package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.partner.BillingType;
import com.example.sms.domain.model.partner.PaymentMethod;
import jakarta.validation.constraints.NotBlank;

/**
 * 顧客作成リクエスト DTO.
 */
public record CreateCustomerRequest(
    @NotBlank(message = "顧客コードは必須です")
    String customerCode,

    String customerBranchNumber,
    String customerCategory,
    String billingCode,
    String billingBranchNumber,
    String collectionCode,
    String collectionBranchNumber,

    @NotBlank(message = "顧客名は必須です")
    String customerName,

    String customerNameKana,
    String ourRepresentativeCode,
    String customerRepresentativeName,
    String customerDepartmentName,
    String customerPostalCode,
    String customerPrefecture,
    String customerAddress1,
    String customerAddress2,
    String customerPhone,
    String customerFax,
    String customerEmail,
    BillingType billingType,
    Integer closingDay1,
    Integer paymentMonth1,
    Integer paymentDay1,
    PaymentMethod paymentMethod1,
    Integer closingDay2,
    Integer paymentMonth2,
    Integer paymentDay2,
    PaymentMethod paymentMethod2
) {
}
