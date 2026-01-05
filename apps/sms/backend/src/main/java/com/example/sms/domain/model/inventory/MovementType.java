package com.example.sms.domain.model.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 移動区分.
 */
@Getter
@RequiredArgsConstructor
public enum MovementType {
    RECEIPT("入荷入庫"),
    SHIPMENT("出荷出庫"),
    TRANSFER_OUT("倉庫間移動出"),
    TRANSFER_IN("倉庫間移動入"),
    ADJUSTMENT_PLUS("棚卸調整増"),
    ADJUSTMENT_MINUS("棚卸調整減"),
    RETURN_RECEIPT("返品入庫"),
    DISPOSAL("廃棄");

    private final String displayName;

    public static MovementType fromDisplayName(String displayName) {
        for (MovementType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown movement type: " + displayName);
    }

    /**
     * 入庫系の移動かどうか.
     */
    public boolean isInbound() {
        return this == RECEIPT || this == TRANSFER_IN ||
               this == ADJUSTMENT_PLUS || this == RETURN_RECEIPT;
    }

    /**
     * 出庫系の移動かどうか.
     */
    public boolean isOutbound() {
        return this == SHIPMENT || this == TRANSFER_OUT ||
               this == ADJUSTMENT_MINUS || this == DISPOSAL;
    }
}
