package com.example.sms.domain.model.purchase;

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
 * 仕入エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Purchase {
    private Integer id;
    private String purchaseNumber;
    private Integer receivingId;
    private String supplierCode;
    @Builder.Default
    private String supplierBranchNumber = "00";
    private LocalDate purchaseDate;
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /** 楽観ロック用バージョン. */
    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<PurchaseDetail> details = new ArrayList<>();

    /**
     * 仕入合計金額を再計算する.
     */
    public void recalculateTotalAmount() {
        if (details != null && !details.isEmpty()) {
            this.totalAmount = details.stream()
                    .map(PurchaseDetail::getPurchaseAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.taxAmount = this.totalAmount.multiply(new BigDecimal("0.10"));
        }
    }
}
