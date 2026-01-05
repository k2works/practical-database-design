package com.example.sms.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 赤黒処理履歴.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedBlackHistory {
    /** ID. */
    private Integer id;
    /** 処理番号. */
    private String processNumber;
    /** 処理日時. */
    private LocalDateTime processDateTime;
    /** 伝票種別. */
    private String slipCategory;
    /** 元伝票番号. */
    private String originalSlipNumber;
    /** 赤伝票番号. */
    private String redSlipNumber;
    /** 黒伝票番号. */
    private String blackSlipNumber;
    /** 処理理由. */
    private String processReason;
    /** 処理者. */
    private String processedBy;
    /** 作成日時. */
    private LocalDateTime createdAt;
}
