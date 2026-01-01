package com.example.sms.domain.model.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 取引先エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Partner {
    private String partnerCode;
    private String partnerName;
    private String partnerNameKana;
    @Builder.Default
    private boolean isCustomer = false;
    @Builder.Default
    private boolean isSupplier = false;
    private String postalCode;
    private String address1;
    private String address2;
    private String classificationCode;
    @Builder.Default
    private boolean isTradingProhibited = false;
    @Builder.Default
    private boolean isMiscellaneous = false;
    private String groupCode;
    @Builder.Default
    private BigDecimal creditLimit = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal temporaryCreditIncrease = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
