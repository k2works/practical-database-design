package com.example.sms.domain.model.invoice;

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
 * 請求エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Invoice {
    private Integer id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String billingCode;
    private String customerCode;
    private String customerBranchNumber;
    private LocalDate closingDate;
    @Builder.Default
    private InvoiceType invoiceType = InvoiceType.CLOSING;
    @Builder.Default
    private BigDecimal previousBalance = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal receiptAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal carriedBalance = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal currentSalesAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal currentTaxAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal currentInvoiceAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal invoiceBalance = BigDecimal.ZERO;
    private LocalDate dueDate;
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /** 楽観ロック用バージョン. */
    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<InvoiceDetail> details = new ArrayList<>();

    /**
     * 請求残高を計算する.
     *
     * @return 請求残高
     */
    public BigDecimal calculateInvoiceBalance() {
        return carriedBalance.add(currentInvoiceAmount).subtract(receiptAmount);
    }

    /**
     * 繰越残高を計算する.
     *
     * @return 繰越残高
     */
    public BigDecimal calculateCarriedBalance() {
        return previousBalance.subtract(receiptAmount);
    }
}
