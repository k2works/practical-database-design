package com.example.sms.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 変更履歴.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeHistory {
    /** ID. */
    private Integer id;
    /** テーブル名. */
    private String tableName;
    /** レコードID. */
    private String recordId;
    /** 操作種別. */
    private String operationType;
    /** 変更日時. */
    private LocalDateTime changedAt;
    /** 変更者. */
    private String changedBy;
    /** 変更前データ（JSON）. */
    private String beforeData;
    /** 変更後データ（JSON）. */
    private String afterData;
    /** 変更理由. */
    private String changeReason;
    /** 作成日時. */
    private LocalDateTime createdAt;
}
