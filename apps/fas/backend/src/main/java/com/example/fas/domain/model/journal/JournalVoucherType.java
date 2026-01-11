package com.example.fas.domain.model.journal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 仕訳伝票区分.
 * 仕訳伝票の種類を区分する.
 */
@Getter
@RequiredArgsConstructor
public enum JournalVoucherType {
    NORMAL("通常"),
    CLOSING("決算"),
    AUTO("自動"),
    TRANSFER("振替");

    private final String displayName;

    /**
     * 表示名から仕訳伝票区分を取得する.
     *
     * @param displayName 表示名
     * @return 仕訳伝票区分
     */
    public static JournalVoucherType fromDisplayName(String displayName) {
        for (JournalVoucherType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な仕訳伝票区分: " + displayName);
    }
}
