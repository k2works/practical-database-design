package com.example.sms.domain.model.receipt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 入金ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum ReceiptStatus {
    RECEIVED("入金済"),
    PARTIALLY_APPLIED("一部消込"),
    APPLIED("消込済"),
    OVERPAID("過入金");

    private final String displayName;

    /**
     * 表示名から入金ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 入金ステータス
     */
    public static ReceiptStatus fromDisplayName(String displayName) {
        for (ReceiptStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な入金ステータス: " + displayName);
    }
}
