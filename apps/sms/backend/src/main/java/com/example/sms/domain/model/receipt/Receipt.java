package com.example.sms.domain.model.receipt;

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
 * 入金エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Receipt {
    private Integer id;
    private String receiptNumber;
    private LocalDate receiptDate;
    private String customerCode;
    private String customerBranchNumber;
    private ReceiptMethod receiptMethod;
    private BigDecimal receiptAmount;
    @Builder.Default
    private BigDecimal appliedAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal unappliedAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal bankFee = BigDecimal.ZERO;
    private String payerName;
    private String bankName;
    private String accountNumber;
    @Builder.Default
    private ReceiptStatus status = ReceiptStatus.RECEIVED;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /** 楽観ロック用バージョン. */
    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<ReceiptApplication> applications = new ArrayList<>();

    /**
     * 未消込金額を計算する.
     *
     * @return 未消込金額
     */
    public BigDecimal calculateUnappliedAmount() {
        return receiptAmount.subtract(appliedAmount).subtract(bankFee);
    }

    /**
     * 消込可能かどうかを判定する.
     *
     * @param amount 消込金額
     * @return 消込可能な場合は true
     */
    public boolean canApply(BigDecimal amount) {
        return unappliedAmount.compareTo(amount) >= 0;
    }
}
