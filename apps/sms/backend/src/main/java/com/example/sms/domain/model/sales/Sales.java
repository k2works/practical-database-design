package com.example.sms.domain.model.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 売上エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Sales {
    private Integer id;
    private String salesNumber;
    private LocalDate salesDate;
    private Integer orderId;
    private Integer shipmentId;
    private String customerCode;
    private String customerBranchNumber;
    private String representativeCode;
    @Builder.Default
    private BigDecimal salesAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Builder.Default
    private SalesStatus status = SalesStatus.RECORDED;
    private Integer billingId;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<SalesDetail> details;
}
