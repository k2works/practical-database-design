package com.example.sms.domain.model.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 出荷先エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDestination {
    private String partnerCode;
    private String customerBranchNumber;
    private String shippingNumber;
    private String shippingName;
    private String regionCode;
    private String postalCode;
    private String address1;
    private String address2;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
