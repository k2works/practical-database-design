package com.example.fas.domain.model.journal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消費税区分.
 * 仕訳の消費税区分を区分する.
 */
@Getter
@RequiredArgsConstructor
public enum TaxType {
    TAXABLE("課税"),
    NON_TAXABLE("非課税"),
    TAX_EXEMPT("免税"),
    NOT_TAXABLE("不課税"),
    OUT_OF_SCOPE("対象外");

    private final String displayName;

    /**
     * 表示名から消費税区分を取得する.
     *
     * @param displayName 表示名
     * @return 消費税区分
     */
    public static TaxType fromDisplayName(String displayName) {
        for (TaxType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な消費税区分: " + displayName);
    }
}
