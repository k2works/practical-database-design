package com.example.sms.domain.model.partner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 請求区分.
 */
@Getter
@RequiredArgsConstructor
public enum BillingType {
    ON_DEMAND("都度"),
    PERIODIC("締め");

    private final String displayName;

    /**
     * 表示名から請求区分を取得する.
     *
     * @param displayName 表示名
     * @return 請求区分
     */
    public static BillingType fromDisplayName(String displayName) {
        for (BillingType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な請求区分: " + displayName);
    }
}
