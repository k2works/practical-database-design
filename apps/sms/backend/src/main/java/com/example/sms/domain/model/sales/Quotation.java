package com.example.sms.domain.model.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 見積エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    private Integer id;
    private String quotationNumber;
    private LocalDate quotationDate;
    private LocalDate validUntil;
    private String customerCode;
    private String customerBranchNumber;
    private String salesRepCode;
    private String subject;
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Builder.Default
    private QuotationStatus status = QuotationStatus.NEGOTIATING;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder.Default
    private List<QuotationDetail> details = new ArrayList<>();
}
