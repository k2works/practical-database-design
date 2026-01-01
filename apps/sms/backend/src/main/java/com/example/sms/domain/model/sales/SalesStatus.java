package com.example.sms.domain.model.sales;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 売上ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum SalesStatus {
    RECORDED("計上済"),
    BILLED("請求済"),
    PAID("入金済"),
    CANCELLED("キャンセル");

    private final String displayName;

    /**
     * 表示名から売上ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 売上ステータス
     */
    public static SalesStatus fromDisplayName(String displayName) {
        for (SalesStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な売上ステータス: " + displayName);
    }
}
