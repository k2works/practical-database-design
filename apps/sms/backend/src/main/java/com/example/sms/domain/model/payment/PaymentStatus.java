package com.example.sms.domain.model.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支払ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    DRAFT("作成中"),
    PENDING_APPROVAL("承認待ち"),
    APPROVED("承認済"),
    PAID("支払済"),
    CANCELLED("取消");

    private final String displayName;

    public static PaymentStatus fromDisplayName(String displayName) {
        for (PaymentStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + displayName);
    }

    /**
     * 承認可能なステータスかどうか.
     */
    public boolean canApprove() {
        return this == PENDING_APPROVAL;
    }

    /**
     * 支払実行可能なステータスかどうか.
     */
    public boolean canExecute() {
        return this == APPROVED;
    }

    /**
     * 取消可能なステータスかどうか.
     */
    public boolean canCancel() {
        return this == DRAFT || this == PENDING_APPROVAL;
    }
}
