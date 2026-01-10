package com.example.fas.application.port.in;

import com.example.fas.application.port.in.dto.DailyBalanceResponse;
import com.example.fas.domain.model.common.PageResult;
import java.time.LocalDate;

/**
 * 日次残高照会ユースケース（Input Port）.
 */
public interface DailyBalanceUseCase {

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
    PageResult<DailyBalanceResponse> getDailyBalances(
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
    PageResult<DailyBalanceResponse> getDailyBalancesByDate(
            LocalDate postingDate, int page, int size);
}
