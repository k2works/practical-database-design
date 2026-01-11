package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.balance.DailyAccountBalance;
import com.example.fas.domain.model.balance.DailyReportLine;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 日次勘定科目残高マッパー.
 */
@Mapper
public interface DailyAccountBalanceMapper {

    // UPSERT
    void upsert(DailyAccountBalance balance);

    // 複合キーで検索
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    DailyAccountBalance findByKey(
            @Param("postingDate") LocalDate postingDate,
            @Param("accountCode") String accountCode,
            @Param("subAccountCode") String subAccountCode,
            @Param("departmentCode") String departmentCode,
            @Param("projectCode") String projectCode,
            @Param("closingJournalFlag") Boolean closingJournalFlag);

    // 起票日で検索
    List<DailyAccountBalance> findByPostingDate(@Param("postingDate") LocalDate postingDate);

    // 勘定科目コードと期間で検索
    List<DailyAccountBalance> findByAccountCodeAndDateRange(
            @Param("accountCode") String accountCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    // 日計表データ取得
    List<DailyReportLine> getDailyReport(@Param("postingDate") LocalDate postingDate);

    // 楽観ロック対応更新
    int updateWithOptimisticLock(DailyAccountBalance balance);

    // バージョン取得
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    Integer findVersion(
            @Param("postingDate") LocalDate postingDate,
            @Param("accountCode") String accountCode,
            @Param("subAccountCode") String subAccountCode,
            @Param("departmentCode") String departmentCode,
            @Param("projectCode") String projectCode,
            @Param("closingJournalFlag") Boolean closingJournalFlag);

    // 全件削除
    void deleteAll();

    // ページネーション付き検索
    List<DailyReportLine> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("accountCode") String accountCode);

    // 検索条件に一致する件数
    long countWithCondition(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("accountCode") String accountCode);

    // 指定日のページネーション付き検索
    List<DailyReportLine> findByPostingDateWithPagination(
            @Param("postingDate") LocalDate postingDate,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 指定日の件数
    long countByPostingDate(@Param("postingDate") LocalDate postingDate);
}
