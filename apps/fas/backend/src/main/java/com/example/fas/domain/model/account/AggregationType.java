package com.example.fas.domain.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 集計区分.
 * 科目の性質（見出し/集計/計上）を区分する.
 */
@Getter
@RequiredArgsConstructor
public enum AggregationType {
    HEADER("見出科目"),
    SUMMARY("集計科目"),
    POSTING("計上科目");

    private final String displayName;

    /**
     * 表示名から集計区分を取得する.
     *
     * @param displayName 表示名
     * @return 集計区分
     */
    public static AggregationType fromDisplayName(String displayName) {
        for (AggregationType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な集計区分: " + displayName);
    }
}
