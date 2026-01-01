package com.example.sms.domain.model.partner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支払方法.
 */
@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CASH("現金"),
    TRANSFER("振込"),
    BILL("手形"),
    CHECK("小切手"),
    OTHER("その他");

    private final String displayName;

    /**
     * 表示名から支払方法を取得する.
     *
     * @param displayName 表示名
     * @return 支払方法
     */
    public static PaymentMethod fromDisplayName(String displayName) {
        for (PaymentMethod method : values()) {
            if (method.displayName.equals(displayName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("不正な支払方法: " + displayName);
    }
}
