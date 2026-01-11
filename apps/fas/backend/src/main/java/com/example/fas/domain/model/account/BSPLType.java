package com.example.fas.domain.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * BSPL区分.
 * 貸借対照表(BS)か損益計算書(PL)かを区分する.
 */
@Getter
@RequiredArgsConstructor
public enum BSPLType {
    BS("BS"),
    PL("PL");

    private final String displayName;

    /**
     * 表示名からBSPL区分を取得する.
     *
     * @param displayName 表示名
     * @return BSPL区分
     */
    public static BSPLType fromDisplayName(String displayName) {
        for (BSPLType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正なBSPL区分: " + displayName);
    }
}
