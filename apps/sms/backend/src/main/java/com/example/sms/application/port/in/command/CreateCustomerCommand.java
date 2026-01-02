package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.partner.BillingType;
import com.example.sms.domain.model.partner.PaymentMethod;

/**
 * 顧客作成コマンド.
 */
public record CreateCustomerCommand(
    String customerCode,
    String customerBranchNumber,
    String customerCategory,
    String billingCode,
    String billingBranchNumber,
    String collectionCode,
    String collectionBranchNumber,
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
