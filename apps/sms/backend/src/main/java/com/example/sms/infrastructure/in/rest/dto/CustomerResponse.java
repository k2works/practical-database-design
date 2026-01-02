package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.partner.BillingType;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.domain.model.partner.PaymentMethod;

import java.time.LocalDateTime;

/**
 * 顧客レスポンス DTO.
 */
public record CustomerResponse(
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
    PaymentMethod paymentMethod2,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
            customer.getCustomerCode(),
            customer.getCustomerBranchNumber(),
            customer.getCustomerCategory(),
            customer.getBillingCode(),
            customer.getBillingBranchNumber(),
            customer.getCollectionCode(),
            customer.getCollectionBranchNumber(),
            customer.getCustomerName(),
            customer.getCustomerNameKana(),
            customer.getOurRepresentativeCode(),
            customer.getCustomerRepresentativeName(),
            customer.getCustomerDepartmentName(),
            customer.getCustomerPostalCode(),
            customer.getCustomerPrefecture(),
            customer.getCustomerAddress1(),
            customer.getCustomerAddress2(),
            customer.getCustomerPhone(),
            customer.getCustomerFax(),
            customer.getCustomerEmail(),
            customer.getBillingType(),
            customer.getClosingDay1(),
            customer.getPaymentMonth1(),
            customer.getPaymentDay1(),
            customer.getPaymentMethod1(),
            customer.getClosingDay2(),
            customer.getPaymentMonth2(),
            customer.getPaymentDay2(),
            customer.getPaymentMethod2(),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}
