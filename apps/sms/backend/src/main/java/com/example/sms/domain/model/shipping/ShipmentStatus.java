package com.example.sms.domain.model.shipping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 出荷ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum ShipmentStatus {
    INSTRUCTED("出荷指示済"),
    PREPARING("出荷準備中"),
    SHIPPED("出荷済"),
    CANCELLED("キャンセル");

    private final String displayName;

    /**
     * 表示名から出荷ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 出荷ステータス
     */
    public static ShipmentStatus fromDisplayName(String displayName) {
        for (ShipmentStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な出荷ステータス: " + displayName);
    }
}
