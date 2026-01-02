package com.example.sms.domain.model.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 倉庫区分.
 */
@Getter
@RequiredArgsConstructor
public enum WarehouseType {
    OWN("自社"),
    EXTERNAL("外部"),
    VIRTUAL("仮想");

    private final String displayName;

    public static WarehouseType fromDisplayName(String displayName) {
        for (WarehouseType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown warehouse type: " + displayName);
    }
}
