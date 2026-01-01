package com.example.sms.domain.model.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 税区分.
 */
@Getter
@RequiredArgsConstructor
public enum TaxCategory {
    EXCLUSIVE("外税"),
    INCLUSIVE("内税"),
    TAX_FREE("非課税");

    private final String displayName;

    /**
     * 表示名から税区分を取得する.
     *
     * @param displayName 表示名
     * @return 税区分
     */
    public static TaxCategory fromDisplayName(String displayName) {
        for (TaxCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("不正な税区分: " + displayName);
    }
}
