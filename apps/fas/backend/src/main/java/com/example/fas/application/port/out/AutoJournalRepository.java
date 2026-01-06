package com.example.fas.application.port.out;

import com.example.fas.domain.model.autojournal.AutoJournalEntry;
import com.example.fas.domain.model.autojournal.AutoJournalHistory;
import com.example.fas.domain.model.autojournal.AutoJournalPattern;
import com.example.fas.domain.model.autojournal.AutoJournalStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 自動仕訳リポジトリ（Output Port）.
 */
public interface AutoJournalRepository {

    // パターンマスタ操作

    void savePattern(AutoJournalPattern pattern);

    Optional<AutoJournalPattern> findPatternByCode(String patternCode);

    List<AutoJournalPattern> findAllPatterns();

    List<AutoJournalPattern> findValidPatterns(LocalDate date);

    void updatePattern(AutoJournalPattern pattern);

    void deletePattern(String patternCode);

    void deleteAllPatterns();

    // 自動仕訳エントリ操作

    void saveEntry(AutoJournalEntry entry);

    Optional<AutoJournalEntry> findEntryByNumber(String autoJournalNumber);

    List<AutoJournalEntry> findEntriesBySalesNumber(String salesNumber);

    List<AutoJournalEntry> findUnpostedEntries();

    List<AutoJournalEntry> findUnpostedEntriesByDate(LocalDate date);

    List<AutoJournalEntry> findEntriesByStatus(AutoJournalStatus status);

    void updateEntry(AutoJournalEntry entry);

    void deleteEntry(String autoJournalNumber);

    void deleteAllEntries();

    // パターン情報付き取得

    Optional<AutoJournalEntry> findEntryWithPatternByNumber(String autoJournalNumber);

    List<AutoJournalEntry> findUnpostedEntriesWithPattern();

    // 処理履歴操作

    void saveHistory(AutoJournalHistory history);

    Optional<AutoJournalHistory> findHistoryByNumber(String processNumber);

    List<AutoJournalHistory> findHistoriesByDateRange(LocalDate fromDate, LocalDate toDate);

    void deleteAllHistories();
}
