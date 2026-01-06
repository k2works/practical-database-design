package com.example.fas.domain.model.journal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消費税計算区分.
 * 消費税の計算方法を区分する.
 */
@Getter
@RequiredArgsConstructor
public enum TaxCalculationType {
    EXCLUSIVE("外税"),
    INCLUSIVE("内税"),
    NO_TAX("税なし");

    private final String displayName;

    /**
     * 表示名から消費税計算区分を取得する.
     *
     * @param displayName 表示名
     * @return 消費税計算区分
     */
    public static TaxCalculationType fromDisplayName(String displayName) {
        for (TaxCalculationType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な消費税計算区分: " + displayName);
    }
}
