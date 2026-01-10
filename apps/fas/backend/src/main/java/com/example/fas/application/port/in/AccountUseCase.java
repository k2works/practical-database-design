package com.example.fas.application.port.in;

import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.in.dto.CreateAccountCommand;
import com.example.fas.application.port.in.dto.UpdateAccountCommand;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;

/**
 * 勘定科目ユースケース（Input Port）.
 */
public interface AccountUseCase {

    /**
     * 勘定科目を取得.
     *
     * @param accountCode 勘定科目コード
     * @return 勘定科目レスポンス
     */
    AccountResponse getAccount(String accountCode);

    /**
     * 全勘定科目を取得.
     *
     * @return 勘定科目レスポンスリスト
     */
    List<AccountResponse> getAllAccounts();

    /**
     * BS/PL区分で勘定科目を取得.
     *
     * @param bsPlType BS/PL区分
     * @return 勘定科目レスポンスリスト
     */
    List<AccountResponse> getAccountsByBsPlType(String bsPlType);

    /**
     * ページネーション付きで勘定科目を取得.
     *
     * @param page     ページ番号（0始まり）
     * @param size     ページサイズ
     * @param bsPlType BSPL区分（BS/PL/null）
     * @param keyword  キーワード
     * @return ページネーション結果
     */
    PageResult<AccountResponse> getAccounts(int page, int size, String bsPlType, String keyword);

    /**
     * 計上科目のみ取得.
     *
     * @return 勘定科目レスポンスリスト
     */
    List<AccountResponse> getPostingAccounts();

    /**
     * 勘定科目を登録.
     *
     * @param command 登録コマンド
     * @return 勘定科目レスポンス
     */
    AccountResponse createAccount(CreateAccountCommand command);

    /**
     * 勘定科目を更新.
     *
     * @param accountCode 勘定科目コード
     * @param command 更新コマンド
     * @return 勘定科目レスポンス
     */
    AccountResponse updateAccount(String accountCode, UpdateAccountCommand command);

    /**
     * 勘定科目を削除.
     *
     * @param accountCode 勘定科目コード
     */
    void deleteAccount(String accountCode);
}
