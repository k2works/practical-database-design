package com.example.sms.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 取引先作成リクエスト DTO.
 */
public record CreatePartnerRequest(
    @NotBlank(message = "取引先コードは必須です")
    String partnerCode,

    @NotBlank(message = "取引先名は必須です")
    String partnerName,

    String partnerNameKana,
    Boolean isCustomer,
    Boolean isSupplier,
    String postalCode,
    String address1,
    String address2,
    String classificationCode,
    Boolean isTradingProhibited,
    Boolean isMiscellaneous,
    String groupCode,
    BigDecimal creditLimit,
    BigDecimal temporaryCreditIncrease
) {
    public boolean isCustomerValue() {
        return isCustomer != null && isCustomer;
    }

    public boolean isSupplierValue() {
        return isSupplier != null && isSupplier;
    }

    public boolean isTradingProhibitedValue() {
        return isTradingProhibited != null && isTradingProhibited;
    }

    public boolean isMiscellaneousValue() {
        return isMiscellaneous != null && isMiscellaneous;
    }
}
