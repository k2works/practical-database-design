package com.example.fas.application.port.out;

import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 月次勘定科目残高リポジトリ（Output Port）.
 */
public interface MonthlyAccountBalanceRepository {

    /**
     * 月次残高を保存.
     *
     * @param balance 月次残高
     */
    void save(MonthlyAccountBalance balance);

    /**
     * 複合キーで検索.
     *
     * @param key 複合キー
     * @return 月次残高
     */
    Optional<MonthlyAccountBalance> findByKey(MonthlyAccountBalance.CompositeKey key);

    /**
     * 決算期と月度で検索.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @return 月次残高リスト
     */
    List<MonthlyAccountBalance> findByFiscalYearAndMonth(Integer fiscalYear, Integer month);

    /**
     * 勘定科目コードで検索.
     *
     * @param fiscalYear 決算期
     * @param accountCode 勘定科目コード
     * @return 月次残高リスト
     */
    List<MonthlyAccountBalance> findByAccountCode(Integer fiscalYear, String accountCode);

    /**
     * 合計残高試算表データを取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @return 合計残高試算表データリスト
     */
    List<TrialBalanceLine> getTrialBalance(Integer fiscalYear, Integer month);

    /**
     * BSPL区分別試算表データを取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @param bsplType BSPL区分
     * @return 試算表データリスト
     */
    List<TrialBalanceLine> getTrialBalanceByBSPL(
            Integer fiscalYear, Integer month, String bsplType);

    /**
     * 日次残高から月次残高を集計.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 集計件数
     */
    int aggregateFromDaily(Integer fiscalYear, Integer month,
            LocalDate fromDate, LocalDate toDate);

    /**
     * 月次残高の繰越処理.
     *
     * @param fiscalYear 決算期
     * @param fromMonth 繰越元月度
     * @param toMonth 繰越先月度
     * @return 繰越件数
     */
    int carryForward(Integer fiscalYear, Integer fromMonth, Integer toMonth);

    /**
     * 楽観ロック対応の更新.
     *
     * @param balance 月次残高
     */
    void updateWithOptimisticLock(MonthlyAccountBalance balance);

    /**
     * 全件削除.
     */
    void deleteAll();
}
