package com.example.pms.domain.model.process;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 作業指示ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum WorkOrderStatus {
    NOT_STARTED("未着手"),
    IN_PROGRESS("作業中"),
    COMPLETED("完了"),
    SUSPENDED("中断");

    private final String displayName;

    /**
     * 表示名から作業指示ステータスを取得.
     *
     * @param displayName 表示名
     * @return 作業指示ステータス
     */
    public static WorkOrderStatus fromDisplayName(String displayName) {
        for (WorkOrderStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な作業指示ステータス: " + displayName);
    }
}
