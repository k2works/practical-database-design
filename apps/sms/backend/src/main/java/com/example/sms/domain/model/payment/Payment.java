package com.example.sms.domain.model.payment;

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
 * 支払データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Integer id;
    private String paymentNumber;
    private String supplierCode;
    private LocalDate paymentClosingDate;
    private LocalDate paymentDueDate;
    private PaymentMethod paymentMethod;
    private BigDecimal paymentAmount;
    private BigDecimal taxAmount;
    private BigDecimal withholdingAmount;
    private BigDecimal netPaymentAmount;
    private LocalDate paymentExecutionDate;
    private PaymentStatus status;
    private String bankCode;
    private String branchCode;
    private String accountType;
    private String accountNumber;
    private String accountName;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<PaymentDetail> details = new ArrayList<>();

    /**
     * 明細の合計金額を計算.
     */
    public BigDecimal calculateTotalAmount() {
        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return details.stream()
                .map(PaymentDetail::getPaymentTargetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 差引支払額を計算.
     */
    public BigDecimal calculateNetAmount() {
        BigDecimal wh = withholdingAmount != null ? withholdingAmount : BigDecimal.ZERO;
        return paymentAmount.add(taxAmount).subtract(wh);
    }

    /**
     * 承認申請.
     */
    public void submitForApproval() {
        if (this.status != PaymentStatus.DRAFT) {
            throw new IllegalStateException("作成中ステータスのみ承認申請が可能です");
        }
        this.status = PaymentStatus.PENDING_APPROVAL;
    }

    /**
     * 承認.
     */
    public void approve() {
        if (!this.status.canApprove()) {
            throw new IllegalStateException(
                    "このステータスでは承認できません: " + this.status.getDisplayName());
        }
        this.status = PaymentStatus.APPROVED;
    }

    /**
     * 支払実行.
     */
    public void execute(LocalDate executionDate) {
        if (!this.status.canExecute()) {
            throw new IllegalStateException(
                    "このステータスでは支払実行できません: " + this.status.getDisplayName());
        }
        this.paymentExecutionDate = executionDate;
        this.status = PaymentStatus.PAID;
    }

    /**
     * 取消.
     */
    public void cancel() {
        if (!this.status.canCancel()) {
            throw new IllegalStateException(
                    "このステータスでは取消できません: " + this.status.getDisplayName());
        }
        this.status = PaymentStatus.CANCELLED;
    }
}
