package com.example.sms.domain.model.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 商品区分.
 */
@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    PRODUCT("商品"),
    MANUFACTURED("製品"),
    SERVICE("サービス"),
    MISCELLANEOUS("諸口");

    private final String displayName;

    /**
     * 表示名から商品区分を取得する.
     *
     * @param displayName 表示名
     * @return 商品区分
     */
    public static ProductCategory fromDisplayName(String displayName) {
        for (ProductCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("不正な商品区分: " + displayName);
    }
}
