package com.example.pms.domain.model.inventory;

/**
 * 在庫状態.
 */
public enum StockStatus {
    PASSED("合格"),
    DEFECTIVE("不良"),
    UNINSPECTED("未検査");

    private final String displayName;

    StockStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static StockStatus fromDisplayName(String displayName) {
        for (StockStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown display name: " + displayName);
    }
}
