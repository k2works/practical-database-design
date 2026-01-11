package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.audit.ChangeLog;
import com.example.fas.domain.model.audit.ChangeLog.OperationType;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 変更ログマッパー.
 */
@Mapper
public interface ChangeLogMapper {

    // 登録
    void insert(ChangeLog changeLog);

    // ログIDで検索
    ChangeLog findById(@Param("logId") Long logId);

    // テーブル名とレコードキーで検索
    List<ChangeLog> findByTableNameAndRecordKey(
            @Param("tableName") String tableName,
            @Param("recordKey") String recordKey);

    // テーブル名で検索
    List<ChangeLog> findByTableName(@Param("tableName") String tableName);

    // 操作種別で検索
    List<ChangeLog> findByOperationType(@Param("operationType") OperationType operationType);

    // 期間指定で検索
    List<ChangeLog> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // 操作者で検索
    List<ChangeLog> findByOperatedBy(@Param("operatedBy") String operatedBy);

    // 複合条件で検索
    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<ChangeLog> findByConditions(
            @Param("tableName") String tableName,
            @Param("recordKey") String recordKey,
            @Param("operationType") OperationType operationType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("operatedBy") String operatedBy);

    // 全件削除（テスト用）
    void deleteAll();
}
