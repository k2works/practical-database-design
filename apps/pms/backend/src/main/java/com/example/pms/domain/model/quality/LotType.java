package com.example.pms.domain.model.quality;

/**
 * ロット種別.
 */
public enum LotType {
    PURCHASED("購入ロット"),
    MANUFACTURED("製造ロット");

    private final String displayName;

    LotType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 表示名からロット種別を取得する.
     *
     * @param displayName 表示名
     * @return ロット種別
     */
    public static LotType fromDisplayName(String displayName) {
        for (LotType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown display name: " + displayName);
    }
}
