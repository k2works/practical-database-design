package com.example.sms.domain.model.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 伝票区分.
 */
@Getter
@RequiredArgsConstructor
public enum SlipType {
    NORMAL("通常"),
    RED("赤伝"),
    BLACK("黒伝");

    private final String displayName;

    /**
     * 表示名から伝票区分を取得.
     *
     * @param displayName 表示名
     * @return 伝票区分
     */
    public static SlipType fromDisplayName(String displayName) {
        for (SlipType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown slip type: " + displayName);
    }

    /**
     * 赤伝かどうか.
     *
     * @return 赤伝の場合true
     */
    public boolean isRed() {
        return this == RED;
    }

    /**
     * 符号を取得（赤伝はマイナス）.
     *
     * @return 赤伝は-1、それ以外は1
     */
    public int getSign() {
        return this == RED ? -1 : 1;
    }
}
