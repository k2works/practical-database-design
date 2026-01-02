package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.common.ChangeHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 変更履歴マッパー.
 */
@Mapper
public interface ChangeHistoryMapper {

    void insert(ChangeHistory history);

    Optional<ChangeHistory> findById(@Param("id") Integer id);

    List<ChangeHistory> findByTableName(@Param("tableName") String tableName);

    List<ChangeHistory> findByTableAndRecordId(
            @Param("tableName") String tableName,
            @Param("recordId") String recordId);

    List<ChangeHistory> findByDateRange(
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime);

    List<ChangeHistory> findAll();

    void deleteById(@Param("id") Integer id);

    void deleteAll();
}
