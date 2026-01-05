package com.example.sms.domain.model.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 棚卸ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum StocktakingStatus {
    DRAFT("作成中"),
    IN_PROGRESS("実施中"),
    CONFIRMED("確定"),
    CANCELLED("取消");

    private final String displayName;

    public static StocktakingStatus fromDisplayName(String displayName) {
        for (StocktakingStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown stocktaking status: " + displayName);
    }

    /**
     * 実施開始可能かどうか.
     */
    public boolean canStart() {
        return this == DRAFT;
    }

    /**
     * 確定可能かどうか.
     */
    public boolean canConfirm() {
        return this == IN_PROGRESS;
    }

    /**
     * 取消可能かどうか.
     */
    public boolean canCancel() {
        return this == DRAFT || this == IN_PROGRESS;
    }
}
