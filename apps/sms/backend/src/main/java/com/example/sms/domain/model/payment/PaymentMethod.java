package com.example.sms.domain.model.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支払方法.
 */
@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    BANK_TRANSFER("振込"),
    BILL("手形"),
    CASH("現金"),
    OFFSET("相殺"),
    ELECTRONIC_RECORD("電子記録債権");

    private final String displayName;

    public static PaymentMethod fromDisplayName(String displayName) {
        for (PaymentMethod method : values()) {
            if (method.displayName.equals(displayName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + displayName);
    }

    /**
     * 振込先情報が必要かどうか.
     */
    public boolean requiresBankInfo() {
        return this == BANK_TRANSFER;
    }
}
