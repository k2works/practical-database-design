package com.example.sms.domain.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 顧客別販売単価エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProductPrice {
    private String productCode;
    private String partnerCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal sellingPrice;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
