package com.example.fas.application.port.in;

import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
import java.util.List;

/**
 * 残高照会ユースケース（Input Port）.
 */
public interface BalanceUseCase {

    /**
     * 合計残高試算表を取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @return 試算表レスポンス
     */
    TrialBalanceResponse getTrialBalance(Integer fiscalYear, Integer month);

    /**
     * BSPL区分別の試算表を取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @param bsPlType BSPL区分（BS/PL）
     * @return 試算表レスポンス
     */
    TrialBalanceResponse getTrialBalanceByBsPlType(
            Integer fiscalYear, Integer month, String bsPlType);

    /**
     * 月次残高一覧を取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @return 月次残高リスト
     */
    List<MonthlyBalanceResponse> getMonthlyBalances(Integer fiscalYear, Integer month);

    /**
     * 勘定科目別の月次残高推移を取得.
     *
     * @param fiscalYear 決算期
     * @param accountCode 勘定科目コード
     * @return 月次残高リスト（月別）
     */
    List<MonthlyBalanceResponse> getMonthlyBalancesByAccountCode(
            Integer fiscalYear, String accountCode);
}
