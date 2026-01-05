package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.partner.Partner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 取引先レスポンス DTO.
 */
public record PartnerResponse(
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
    BigDecimal temporaryCreditIncrease,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PartnerResponse from(Partner partner) {
        return new PartnerResponse(
            partner.getPartnerCode(),
            partner.getPartnerName(),
            partner.getPartnerNameKana(),
            partner.isCustomer(),
            partner.isSupplier(),
            partner.getPostalCode(),
            partner.getAddress1(),
            partner.getAddress2(),
            partner.getClassificationCode(),
            partner.isTradingProhibited(),
            partner.isMiscellaneous(),
            partner.getGroupCode(),
            partner.getCreditLimit(),
            partner.getTemporaryCreditIncrease(),
            partner.getCreatedAt(),
            partner.getUpdatedAt()
        );
    }
}
