package com.example.fas.application.port.out;

import com.example.fas.domain.model.balance.DailyAccountBalance;
import com.example.fas.domain.model.balance.DailyReportLine;
import com.example.fas.domain.model.common.PageResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 日次勘定科目残高リポジトリ（Output Port）.
 */
public interface DailyAccountBalanceRepository {

    /**
     * 日次残高を保存（UPSERT）.
     *
     * @param balance 日次残高
     */
    void upsert(DailyAccountBalance balance);

    /**
     * 複合キーで検索.
     *
     * @param key 複合キー
     * @return 日次残高
     */
    Optional<DailyAccountBalance> findByKey(DailyAccountBalance.CompositeKey key);

    /**
     * 起票日で検索.
     *
     * @param postingDate 起票日
     * @return 日次残高リスト
     */
    List<DailyAccountBalance> findByPostingDate(LocalDate postingDate);

    /**
     * 勘定科目コードと期間で検索.
     *
     * @param accountCode 勘定科目コード
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 日次残高リスト
     */
    List<DailyAccountBalance> findByAccountCodeAndDateRange(
            String accountCode, LocalDate fromDate, LocalDate toDate);

    /**
     * 日計表データを取得.
     *
     * @param postingDate 起票日
     * @return 日計表データリスト
     */
    List<DailyReportLine> getDailyReport(LocalDate postingDate);

    /**
     * 楽観ロック対応の更新.
     *
     * @param balance 日次残高
     */
    void updateWithOptimisticLock(DailyAccountBalance balance);

    /**
     * 全件削除.
     */
    void deleteAll();

    /**
     * 日次残高一覧を取得（ページネーション対応）.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param fromDate 開始日（任意）
     * @param toDate 終了日（任意）
     * @param accountCode 勘定科目コード（任意）
     * @return ページネーション結果
     */
    PageResult<DailyReportLine> findWithPagination(
            int page, int size,
            LocalDate fromDate, LocalDate toDate,
            String accountCode);

    /**
     * 指定日の日次残高を取得（ページネーション対応）.
     *
     * @param postingDate 起票日
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @return ページネーション結果
     */
    PageResult<DailyReportLine> findByPostingDateWithPagination(
            LocalDate postingDate, int page, int size);
}
