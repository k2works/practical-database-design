package com.example.sms.domain.model.purchase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 発注ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderStatus {
    DRAFT("作成中"),
    CONFIRMED("確定"),
    PARTIALLY_RECEIVED("一部入荷"),
    COMPLETED("入荷完了"),
    CANCELLED("取消");

    private final String displayName;

    /**
     * 表示名から発注ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 発注ステータス
     */
    public static PurchaseOrderStatus fromDisplayName(String displayName) {
        for (PurchaseOrderStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な発注ステータス: " + displayName);
    }

    /**
     * 確定可能かどうか.
     *
     * @return 確定可能な場合true
     */
    public boolean canConfirm() {
        return this == DRAFT;
    }

    /**
     * 入荷登録可能かどうか.
     *
     * @return 入荷登録可能な場合true
     */
    public boolean canReceive() {
        return this == CONFIRMED || this == PARTIALLY_RECEIVED;
    }

    /**
     * 取消可能かどうか.
     *
     * @return 取消可能な場合true
     */
    public boolean canCancel() {
        return this == DRAFT || this == CONFIRMED;
    }
}
