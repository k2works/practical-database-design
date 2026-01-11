package com.example.fas.application.port.in;

import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.report.BalanceSheet;
import com.example.fas.domain.model.report.DailyReport;
import com.example.fas.domain.model.report.GeneralLedger;
import com.example.fas.domain.model.report.GeneralLedgerEntry;
import com.example.fas.domain.model.report.IncomeStatement;
import com.example.fas.domain.model.report.TrialBalance;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 帳票出力ユースケース（Input Port）.
 */
public interface ReportUseCase {

    /**
     * 日計表を取得.
     *
     * @param date 対象日
     * @return 日計表
     */
    DailyReport getDailyReport(LocalDate date);

    /**
     * 総勘定元帳を取得（ページネーション対応）.
     *
     * @param accountCode 勘定科目コード
     * @param fromDate 期間開始日
     * @param toDate 期間終了日
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @return ページネーション結果
     */
    PageResult<GeneralLedgerEntry> getGeneralLedger(
            String accountCode,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size);

    /**
     * 総勘定元帳のメタデータを取得.
     *
     * @param accountCode 勘定科目コード
     * @param fromDate 期間開始日
     * @param toDate 期間終了日
     * @return 総勘定元帳（明細なし、メタデータのみ）
     */
    GeneralLedger getGeneralLedgerMeta(
            String accountCode,
            LocalDate fromDate,
            LocalDate toDate);

    /**
     * 貸借対照表を取得.
     *
     * @param asOfDate 基準日
     * @return 貸借対照表
     */
    BalanceSheet getBalanceSheet(LocalDate asOfDate);

    /**
     * 損益計算書を取得.
     *
     * @param fromMonth 期間開始月
     * @param toMonth 期間終了月
     * @return 損益計算書
     */
    IncomeStatement getIncomeStatement(YearMonth fromMonth, YearMonth toMonth);

    /**
     * 合計残高試算表を取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @return 合計残高試算表
     */
    TrialBalance getTrialBalance(Integer fiscalYear, Integer month);
}
