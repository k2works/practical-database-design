package com.example.fas.application.port.out;

import com.example.fas.domain.model.audit.ChangeLog;
import com.example.fas.domain.model.audit.ChangeLog.OperationType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 変更ログリポジトリ（Output Port）.
 */
public interface ChangeLogRepository {

    /**
     * 変更ログを保存する.
     *
     * @param changeLog 変更ログ
     */
    void save(ChangeLog changeLog);

    /**
     * ログIDで検索する.
     *
     * @param logId ログID
     * @return 変更ログ
     */
    Optional<ChangeLog> findById(Long logId);

    /**
     * テーブル名とレコードキーで検索する.
     *
     * @param tableName テーブル名
     * @param recordKey レコードキー
     * @return 変更ログリスト
     */
    List<ChangeLog> findByTableNameAndRecordKey(String tableName, String recordKey);

    /**
     * テーブル名で検索する.
     *
     * @param tableName テーブル名
     * @return 変更ログリスト
     */
    List<ChangeLog> findByTableName(String tableName);

    /**
     * 操作種別で検索する.
     *
     * @param operationType 操作種別
     * @return 変更ログリスト
     */
    List<ChangeLog> findByOperationType(OperationType operationType);

    /**
     * 期間指定で検索する.
     *
     * @param from 開始日時
     * @param to 終了日時
     * @return 変更ログリスト
     */
    List<ChangeLog> findByDateRange(LocalDateTime from, LocalDateTime to);

    /**
     * 操作者で検索する.
     *
     * @param operatedBy 操作者
     * @return 変更ログリスト
     */
    List<ChangeLog> findByOperatedBy(String operatedBy);

    /**
     * 複合条件で検索する.
     *
     * @param tableName テーブル名
     * @param recordKey レコードキー
     * @param operationType 操作種別
     * @param from 開始日時
     * @param to 終了日時
     * @param operatedBy 操作者
     * @return 変更ログリスト
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<ChangeLog> findByConditions(
            String tableName,
            String recordKey,
            OperationType operationType,
            LocalDateTime from,
            LocalDateTime to,
            String operatedBy
    );

    /**
     * 全件削除（テスト用）.
     */
    void deleteAll();
}
