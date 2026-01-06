package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.AutoJournalRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.autojournal.AutoJournalEntry;
import com.example.fas.domain.model.autojournal.AutoJournalHistory;
import com.example.fas.domain.model.autojournal.AutoJournalPattern;
import com.example.fas.domain.model.autojournal.AutoJournalStatus;
import com.example.fas.infrastructure.out.persistence.mapper.AutoJournalMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自動仕訳リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class AutoJournalRepositoryImpl implements AutoJournalRepository {

    private final AutoJournalMapper mapper;

    // パターンマスタ操作

    @Override
    @Transactional
    public void savePattern(AutoJournalPattern pattern) {
        mapper.insertPattern(pattern);
    }

    @Override
    public Optional<AutoJournalPattern> findPatternByCode(String patternCode) {
        return mapper.findPatternByCode(patternCode);
    }

    @Override
    public List<AutoJournalPattern> findAllPatterns() {
        return mapper.findAllPatterns();
    }

    @Override
    public List<AutoJournalPattern> findValidPatterns(LocalDate date) {
        return mapper.findValidPatterns(date);
    }

    @Override
    @Transactional
    public void updatePattern(AutoJournalPattern pattern) {
        int updatedCount = mapper.updatePatternWithOptimisticLock(pattern);
        if (updatedCount == 0) {
            Integer currentVersion = mapper.findPatternVersionByCode(pattern.getPatternCode());
            if (currentVersion == null) {
                throw new OptimisticLockException("自動仕訳パターン", pattern.getPatternCode());
            } else {
                throw new OptimisticLockException("自動仕訳パターン", pattern.getPatternCode(),
                        pattern.getVersion(), currentVersion);
            }
        }
    }

    @Override
    @Transactional
    public void deletePattern(String patternCode) {
        mapper.deletePattern(patternCode);
    }

    @Override
    @Transactional
    public void deleteAllPatterns() {
        mapper.deleteAllPatterns();
    }

    // 自動仕訳エントリ操作

    @Override
    @Transactional
    public void saveEntry(AutoJournalEntry entry) {
        mapper.insertEntry(entry);
    }

    @Override
    public Optional<AutoJournalEntry> findEntryByNumber(String autoJournalNumber) {
        return mapper.findEntryByNumber(autoJournalNumber);
    }

    @Override
    public List<AutoJournalEntry> findEntriesBySalesNumber(String salesNumber) {
        return mapper.findEntriesBySalesNumber(salesNumber);
    }

    @Override
    public List<AutoJournalEntry> findUnpostedEntries() {
        return mapper.findUnpostedEntries();
    }

    @Override
    public List<AutoJournalEntry> findUnpostedEntriesByDate(LocalDate date) {
        return mapper.findUnpostedEntriesByDate(date);
    }

    @Override
    public List<AutoJournalEntry> findEntriesByStatus(AutoJournalStatus status) {
        return mapper.findEntriesByStatus(status);
    }

    @Override
    @Transactional
    public void updateEntry(AutoJournalEntry entry) {
        int updatedCount = mapper.updateEntryWithOptimisticLock(entry);
        if (updatedCount == 0) {
            Integer currentVersion = mapper.findEntryVersionByNumber(entry.getAutoJournalNumber());
            if (currentVersion == null) {
                throw new OptimisticLockException("自動仕訳データ", entry.getAutoJournalNumber());
            } else {
                throw new OptimisticLockException("自動仕訳データ", entry.getAutoJournalNumber(),
                        entry.getVersion(), currentVersion);
            }
        }
    }

    @Override
    @Transactional
    public void deleteEntry(String autoJournalNumber) {
        mapper.deleteEntry(autoJournalNumber);
    }

    @Override
    @Transactional
    public void deleteAllEntries() {
        mapper.deleteAllEntries();
    }

    // パターン情報付き取得

    @Override
    public Optional<AutoJournalEntry> findEntryWithPatternByNumber(String autoJournalNumber) {
        return mapper.findEntryWithPatternByNumber(autoJournalNumber);
    }

    @Override
    public List<AutoJournalEntry> findUnpostedEntriesWithPattern() {
        return mapper.findUnpostedEntriesWithPattern();
    }

    // 処理履歴操作

    @Override
    @Transactional
    public void saveHistory(AutoJournalHistory history) {
        mapper.insertHistory(history);
    }

    @Override
    public Optional<AutoJournalHistory> findHistoryByNumber(String processNumber) {
        return mapper.findHistoryByNumber(processNumber);
    }

    @Override
    public List<AutoJournalHistory> findHistoriesByDateRange(LocalDate fromDate, LocalDate toDate) {
        return mapper.findHistoriesByDateRange(fromDate, toDate);
    }

    @Override
    @Transactional
    public void deleteAllHistories() {
        mapper.deleteAllHistories();
    }
}
