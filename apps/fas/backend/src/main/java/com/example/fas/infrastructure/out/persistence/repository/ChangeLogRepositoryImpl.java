package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.ChangeLogRepository;
import com.example.fas.domain.model.audit.ChangeLog;
import com.example.fas.domain.model.audit.ChangeLog.OperationType;
import com.example.fas.infrastructure.out.persistence.mapper.ChangeLogMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 変更ログリポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ChangeLogRepositoryImpl implements ChangeLogRepository {

    private final ChangeLogMapper mapper;

    @Override
    @Transactional
    public void save(ChangeLog changeLog) {
        mapper.insert(changeLog);
    }

    @Override
    public Optional<ChangeLog> findById(Long logId) {
        return Optional.ofNullable(mapper.findById(logId));
    }

    @Override
    public List<ChangeLog> findByTableNameAndRecordKey(String tableName, String recordKey) {
        return mapper.findByTableNameAndRecordKey(tableName, recordKey);
    }

    @Override
    public List<ChangeLog> findByTableName(String tableName) {
        return mapper.findByTableName(tableName);
    }

    @Override
    public List<ChangeLog> findByOperationType(OperationType operationType) {
        return mapper.findByOperationType(operationType);
    }

    @Override
    public List<ChangeLog> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return mapper.findByDateRange(from, to);
    }

    @Override
    public List<ChangeLog> findByOperatedBy(String operatedBy) {
        return mapper.findByOperatedBy(operatedBy);
    }

    @Override
    public List<ChangeLog> findByConditions(
            String tableName,
            String recordKey,
            OperationType operationType,
            LocalDateTime from,
            LocalDateTime to,
            String operatedBy) {
        return mapper.findByConditions(tableName, recordKey, operationType, from, to, operatedBy);
    }

    @Override
    @Transactional
    public void deleteAll() {
        mapper.deleteAll();
    }
}
