package com.example.sms.domain.model.invoice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 請求ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {
    DRAFT("未発行"),
    ISSUED("発行済"),
    PARTIALLY_PAID("一部入金"),
    PAID("入金済"),
    OVERDUE("回収遅延");

    private final String displayName;

    /**
     * 表示名から請求ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 請求ステータス
     */
    public static InvoiceStatus fromDisplayName(String displayName) {
        for (InvoiceStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な請求ステータス: " + displayName);
    }
}
