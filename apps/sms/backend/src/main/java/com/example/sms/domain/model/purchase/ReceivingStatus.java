package com.example.sms.domain.model.purchase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 入荷ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum ReceivingStatus {
    WAITING("入荷待ち"),
    INSPECTING("検品中"),
    INSPECTION_COMPLETED("検品完了"),
    PURCHASE_RECORDED("仕入計上済"),
    RETURNED("返品");

    private final String displayName;

    /**
     * 表示名から入荷ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 入荷ステータス
     */
    public static ReceivingStatus fromDisplayName(String displayName) {
        for (ReceivingStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な入荷ステータス: " + displayName);
    }

    /**
     * 検品開始可能かどうか.
     *
     * @return 検品開始可能な場合true
     */
    public boolean canStartInspection() {
        return this == WAITING;
    }

    /**
     * 仕入計上可能かどうか.
     *
     * @return 仕入計上可能な場合true
     */
    public boolean canRecordPurchase() {
        return this == INSPECTION_COMPLETED;
    }
}
