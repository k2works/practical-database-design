package com.example.sms.domain.model.receipt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 入金方法.
 */
@Getter
@RequiredArgsConstructor
public enum ReceiptMethod {
    CASH("現金"),
    BANK_TRANSFER("銀行振込"),
    CREDIT_CARD("クレジットカード"),
    BILL("手形"),
    ELECTRONIC_BOND("電子記録債権");

    private final String displayName;

    /**
     * 表示名から入金方法を取得する.
     *
     * @param displayName 表示名
     * @return 入金方法
     */
    public static ReceiptMethod fromDisplayName(String displayName) {
        for (ReceiptMethod method : values()) {
            if (method.displayName.equals(displayName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("不正な入金方法: " + displayName);
    }
}
