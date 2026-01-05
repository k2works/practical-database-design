package com.example.sms.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 採番履歴.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumberingHistory {
    /** ID. */
    private Integer id;
    /** 採番コード. */
    private String numberingCode;
    /** 採番年月. */
    private String yearMonth;
    /** 最終番号. */
    private Long lastNumber;
    /** 作成日時. */
    private LocalDateTime createdAt;
    /** 更新日時. */
    private LocalDateTime updatedAt;
}
