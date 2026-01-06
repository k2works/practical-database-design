package com.example.fas.domain.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 取引要素区分.
 * 5つの取引要素（資産・負債・資本・収益・費用）を区分する.
 */
@Getter
@RequiredArgsConstructor
public enum TransactionElementType {
    ASSET("資産"),
    LIABILITY("負債"),
    EQUITY("資本"),
    REVENUE("収益"),
    EXPENSE("費用");

    private final String displayName;

    /**
     * 表示名から取引要素区分を取得する.
     *
     * @param displayName 表示名
     * @return 取引要素区分
     */
    public static TransactionElementType fromDisplayName(String displayName) {
        for (TransactionElementType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な取引要素区分: " + displayName);
    }
}
