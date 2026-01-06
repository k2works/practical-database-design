package com.example.fas.domain.model.autojournal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 自動仕訳処理ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum AutoJournalStatus {
    PENDING("処理待ち"),
    PROCESSING("処理中"),
    COMPLETED("処理完了"),
    POSTED("転記済"),
    ERROR("エラー");

    private final String displayName;

    /**
     * 表示名からステータスを取得.
     *
     * @param displayName 表示名
     * @return ステータス
     */
    public static AutoJournalStatus fromDisplayName(String displayName) {
        for (AutoJournalStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な自動仕訳ステータス: " + displayName);
    }
}
