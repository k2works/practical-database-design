package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.ChangeHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 変更履歴リポジトリ（Output Port）.
 */
public interface ChangeHistoryRepository {

    void save(ChangeHistory history);

    Optional<ChangeHistory> findById(Integer id);

    List<ChangeHistory> findByTableName(String tableName);

    List<ChangeHistory> findByTableAndRecordId(String tableName, String recordId);

    List<ChangeHistory> findByDateRange(LocalDateTime fromDateTime, LocalDateTime toDateTime);

    List<ChangeHistory> findAll();

    void deleteById(Integer id);

    void deleteAll();
}
