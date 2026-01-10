package com.example.fas.application.port.in;

import com.example.fas.application.port.in.command.CreateAccountStructureCommand;
import com.example.fas.application.port.in.command.UpdateAccountStructureCommand;
import com.example.fas.application.port.in.dto.AccountStructureResponse;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;

/**
 * 勘定科目構成ユースケース（Input Port）.
 */
public interface AccountStructureUseCase {

    /**
     * 勘定科目構成を取得.
     *
     * @param accountCode 勘定科目コード
     * @return 勘定科目構成レスポンス
     */
    AccountStructureResponse getAccountStructure(String accountCode);

    /**
     * 全勘定科目構成を取得.
     *
     * @return 勘定科目構成レスポンスリスト
     */
    List<AccountStructureResponse> getAllAccountStructures();

    /**
     * ページネーション付きで勘定科目構成を取得.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<AccountStructureResponse> getAccountStructures(int page, int size, String keyword);

    /**
     * 子科目を取得.
     *
     * @param parentCode 親科目コード
     * @return 子科目リスト
     */
    List<AccountStructureResponse> getChildren(String parentCode);

    /**
     * 勘定科目構成を登録.
     *
     * @param command 登録コマンド
     * @return 勘定科目構成レスポンス
     */
    AccountStructureResponse createAccountStructure(CreateAccountStructureCommand command);

    /**
     * 勘定科目構成を更新.
     *
     * @param accountCode 勘定科目コード
     * @param command 更新コマンド
     * @return 勘定科目構成レスポンス
     */
    AccountStructureResponse updateAccountStructure(String accountCode, UpdateAccountStructureCommand command);

    /**
     * 勘定科目構成を削除.
     *
     * @param accountCode 勘定科目コード
     */
    void deleteAccountStructure(String accountCode);
}
