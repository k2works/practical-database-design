package com.example.pms.domain.model.plan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllocationType {
    INVENTORY("在庫"),
    PURCHASE_ORDER("発注残"),
    MANUFACTURING_ORDER("製造残");

    private final String displayName;

    public static AllocationType fromDisplayName(String displayName) {
        for (AllocationType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不正な引当区分: " + displayName);
    }
}
