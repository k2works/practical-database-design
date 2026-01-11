package com.example.fas.domain.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 貸借区分.
 * 借方科目か貸方科目かを区分する.
 */
@Getter
@RequiredArgsConstructor
public enum DebitCreditType {
    DEBIT("借方"),
    CREDIT("貸方");

    private final String displayName;

    /**
     * 表示名から貸借区分を取得する.
     *
     * @param displayName 表示名
     * @return 貸借区分
     */
    public static DebitCreditType fromDisplayName(String displayName) {
        for (DebitCreditType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な貸借区分: " + displayName);
    }
}
