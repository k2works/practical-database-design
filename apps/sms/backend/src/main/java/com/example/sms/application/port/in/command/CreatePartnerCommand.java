package com.example.sms.application.port.in.command;

import java.math.BigDecimal;

/**
 * 取引先作成コマンド.
 */
public record CreatePartnerCommand(
    String partnerCode,
    String partnerName,
    String partnerNameKana,
    boolean isCustomer,
    boolean isSupplier,
    String postalCode,
    String address1,
    String address2,
    String classificationCode,
    boolean isTradingProhibited,
    boolean isMiscellaneous,
    String groupCode,
    BigDecimal creditLimit,
    BigDecimal temporaryCreditIncrease
) {
}
