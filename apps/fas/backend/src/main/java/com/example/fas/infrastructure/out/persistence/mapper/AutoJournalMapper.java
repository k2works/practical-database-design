package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.autojournal.AutoJournalEntry;
import com.example.fas.domain.model.autojournal.AutoJournalHistory;
import com.example.fas.domain.model.autojournal.AutoJournalPattern;
import com.example.fas.domain.model.autojournal.AutoJournalStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 自動仕訳マッパー.
 */
@Mapper
public interface AutoJournalMapper {

    // パターンマスタ操作

    void insertPattern(AutoJournalPattern pattern);

    Optional<AutoJournalPattern> findPatternByCode(@Param("patternCode") String patternCode);

    List<AutoJournalPattern> findAllPatterns();

    List<AutoJournalPattern> findValidPatterns(@Param("date") LocalDate date);

    int updatePatternWithOptimisticLock(AutoJournalPattern pattern);

    Integer findPatternVersionByCode(@Param("patternCode") String patternCode);

    void deletePattern(@Param("patternCode") String patternCode);

    void deleteAllPatterns();

    // 自動仕訳エントリ操作

    void insertEntry(AutoJournalEntry entry);

    Optional<AutoJournalEntry> findEntryByNumber(
            @Param("autoJournalNumber") String autoJournalNumber);

    List<AutoJournalEntry> findEntriesBySalesNumber(@Param("salesNumber") String salesNumber);

    List<AutoJournalEntry> findUnpostedEntries();

    List<AutoJournalEntry> findUnpostedEntriesByDate(@Param("date") LocalDate date);

    List<AutoJournalEntry> findEntriesByStatus(@Param("status") AutoJournalStatus status);

    int updateEntryWithOptimisticLock(AutoJournalEntry entry);

    Integer findEntryVersionByNumber(@Param("autoJournalNumber") String autoJournalNumber);

    void deleteEntry(@Param("autoJournalNumber") String autoJournalNumber);

    void deleteAllEntries();

    // パターン情報付き取得

    Optional<AutoJournalEntry> findEntryWithPatternByNumber(
            @Param("autoJournalNumber") String autoJournalNumber);

    List<AutoJournalEntry> findUnpostedEntriesWithPattern();

    // 処理履歴操作

    void insertHistory(AutoJournalHistory history);

    Optional<AutoJournalHistory> findHistoryByNumber(@Param("processNumber") String processNumber);

    List<AutoJournalHistory> findHistoriesByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    void deleteAllHistories();
}
