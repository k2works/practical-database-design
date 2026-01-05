package com.example.sms.domain.model.sales;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 受注ステータス.
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    RECEIVED("受付済"),
    ALLOCATED("引当済"),
    SHIPMENT_INSTRUCTED("出荷指示済"),
    SHIPPED("出荷済"),
    CANCELLED("キャンセル");

    private final String displayName;

    /**
     * 表示名から受注ステータスを取得する.
     *
     * @param displayName 表示名
     * @return 受注ステータス
     */
    public static OrderStatus fromDisplayName(String displayName) {
        for (OrderStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な受注ステータス: " + displayName);
    }
}
