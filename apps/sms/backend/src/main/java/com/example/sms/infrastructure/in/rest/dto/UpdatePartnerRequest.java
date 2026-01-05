package com.example.sms.infrastructure.in.rest.dto;

import java.math.BigDecimal;

/**
 * 取引先更新リクエスト DTO.
 */
public record UpdatePartnerRequest(
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
}
