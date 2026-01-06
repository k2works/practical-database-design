package com.example.fas.domain.model.audit;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 変更ログエンティティ.
 * マスタデータの変更履歴を保存する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLog {
    private Long logId;               // ログID
    private String tableName;         // テーブル名
    private String recordKey;         // レコードキー
    private OperationType operationType; // 操作種別
    private String beforeData;        // 操作前データ（JSON）
    private String afterData;         // 操作後データ（JSON）
    private LocalDateTime operatedAt; // 操作日時
    private String operatedBy;        // 操作者
    private String operatedFrom;      // 操作端末
    private String remarks;           // 備考

    /**
     * 操作種別.
     */
    public enum OperationType {
        INSERT,
        UPDATE,
        DELETE
    }
}
