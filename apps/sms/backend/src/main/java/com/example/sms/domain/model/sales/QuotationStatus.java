package com.example.sms.domain.model.sales;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 見積ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum QuotationStatus {
    NEGOTIATING("商談中"),
    ORDERED("受注確定"),
    LOST("失注"),
    EXPIRED("期限切れ");

    private final String displayName;

    /**
     * 表示名から見積ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 見積ステータス
     */
    public static QuotationStatus fromDisplayName(String displayName) {
        for (QuotationStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な見積ステータス: " + displayName);
    }
}
