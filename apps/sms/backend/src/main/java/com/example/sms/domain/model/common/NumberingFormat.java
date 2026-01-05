package com.example.sms.domain.model.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 採番形式.
 */
@Getter
@RequiredArgsConstructor
public enum NumberingFormat {
    YEARLY("年次"),
    MONTHLY("月次"),
    DAILY("日次"),
    SEQUENTIAL("連番");

    private final String displayName;

    /**
     * 表示名から採番形式を取得.
     *
     * @param displayName 表示名
     * @return 採番形式
     */
    public static NumberingFormat fromDisplayName(String displayName) {
        for (NumberingFormat format : values()) {
            if (format.displayName.equals(displayName)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown numbering format: " + displayName);
    }
}
