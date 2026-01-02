package com.example.sms.application.port.in.command;

import java.math.BigDecimal;

/**
 * 取引先更新コマンド.
 */
public record UpdatePartnerCommand(
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
