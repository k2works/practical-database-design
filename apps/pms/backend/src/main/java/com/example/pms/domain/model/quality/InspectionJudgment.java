package com.example.pms.domain.model.quality;

/**
 * 検査判定.
 */
public enum InspectionJudgment {
    PASSED("合格"),
    FAILED("不合格"),
    HOLD("保留");

    private final String displayName;

    InspectionJudgment(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 表示名から検査判定を取得する.
     *
     * @param displayName 表示名
     * @return 検査判定
     */
    public static InspectionJudgment fromDisplayName(String displayName) {
        for (InspectionJudgment judgment : values()) {
            if (judgment.displayName.equals(displayName)) {
                return judgment;
            }
        }
        throw new IllegalArgumentException("Unknown display name: " + displayName);
    }
}
