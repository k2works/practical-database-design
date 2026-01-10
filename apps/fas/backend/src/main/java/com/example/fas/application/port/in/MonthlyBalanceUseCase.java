package com.example.fas.application.port.in;

import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.domain.model.common.PageResult;

/**
 * 月次残高照会ユースケース（Input Port）.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface MonthlyBalanceUseCase {

    /**
     * 月次残高一覧を取得（ページネーション対応）.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param fiscalYear 決算期（任意）
     * @param month 月度（任意）
     * @param accountCode 勘定科目コード（任意）
     * @return ページネーション結果
     */
    PageResult<MonthlyBalanceResponse> getMonthlyBalances(
            int page, int size,
            Integer fiscalYear, Integer month,
            String accountCode);
}
