package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 月次勘定科目残高マッパー.
 */
@Mapper
public interface MonthlyAccountBalanceMapper {

    // 登録
    void insert(MonthlyAccountBalance balance);

    // 複合キーで検索
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    MonthlyAccountBalance findByKey(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month,
            @Param("accountCode") String accountCode,
            @Param("subAccountCode") String subAccountCode,
            @Param("departmentCode") String departmentCode,
            @Param("projectCode") String projectCode,
            @Param("closingJournalFlag") Boolean closingJournalFlag);

    // 決算期と月度で検索
    List<MonthlyAccountBalance> findByFiscalYearAndMonth(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month);

    // 勘定科目コードで検索
    List<MonthlyAccountBalance> findByAccountCode(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("accountCode") String accountCode);

    // 合計残高試算表データ取得
    List<TrialBalanceLine> getTrialBalance(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month);

    // BSPL区分別試算表データ取得
    List<TrialBalanceLine> getTrialBalanceByBSPL(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month,
            @Param("bsplType") String bsplType);

    // 日次残高から月次残高を集計
    int aggregateFromDaily(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    // 月次残高の繰越処理
    int carryForward(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("fromMonth") Integer fromMonth,
            @Param("toMonth") Integer toMonth);

    // 楽観ロック対応更新
    int updateWithOptimisticLock(MonthlyAccountBalance balance);

    // バージョン取得
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    Integer findVersion(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month,
            @Param("accountCode") String accountCode,
            @Param("subAccountCode") String subAccountCode,
            @Param("departmentCode") String departmentCode,
            @Param("projectCode") String projectCode,
            @Param("closingJournalFlag") Boolean closingJournalFlag);

    // 全件削除
    void deleteAll();

    // ページネーション付き検索
    List<TrialBalanceLine> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month,
            @Param("accountCode") String accountCode);

    // 検索条件に一致する件数
    long countWithCondition(
            @Param("fiscalYear") Integer fiscalYear,
            @Param("month") Integer month,
            @Param("accountCode") String accountCode);
}
