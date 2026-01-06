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
}
