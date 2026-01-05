package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ChangeHistoryRepository;
import com.example.sms.domain.model.common.ChangeHistory;
import com.example.sms.infrastructure.out.persistence.mapper.ChangeHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 変更履歴リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ChangeHistoryRepositoryImpl implements ChangeHistoryRepository {

    private final ChangeHistoryMapper changeHistoryMapper;

    @Override
    public void save(ChangeHistory history) {
        changeHistoryMapper.insert(history);
    }

    @Override
    public Optional<ChangeHistory> findById(Integer id) {
        return changeHistoryMapper.findById(id);
    }

    @Override
    public List<ChangeHistory> findByTableName(String tableName) {
        return changeHistoryMapper.findByTableName(tableName);
    }

    @Override
    public List<ChangeHistory> findByTableAndRecordId(String tableName, String recordId) {
        return changeHistoryMapper.findByTableAndRecordId(tableName, recordId);
    }

    @Override
    public List<ChangeHistory> findByDateRange(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        return changeHistoryMapper.findByDateRange(fromDateTime, toDateTime);
    }

    @Override
    public List<ChangeHistory> findAll() {
        return changeHistoryMapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        changeHistoryMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        changeHistoryMapper.deleteAll();
    }
}
