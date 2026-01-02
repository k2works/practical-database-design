package com.example.sms.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 採番マスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumberingMaster {
    /** 採番コード. */
    private String numberingCode;
    /** 採番名. */
    private String numberingName;
    /** プレフィックス. */
    private String prefix;
    /** 採番形式. */
    private String format;
    /** 桁数. */
    private Integer digits;
    /** 現在値. */
    private Long currentValue;
    /** 最終採番日. */
    private LocalDate lastNumberingDate;
    /** リセット対象. */
    private Boolean resetTarget;
    /** 作成日時. */
    private LocalDateTime createdAt;
    /** 更新日時. */
    private LocalDateTime updatedAt;
}
