package com.example.fas.application.port.out;

import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;
import java.util.Optional;

/**
 * 勘定科目リポジトリ（Output Port）.
 */
public interface AccountRepository {

    void save(Account account);

    Optional<Account> findByCode(String accountCode);

    List<Account> findAll();

    List<Account> findByBSPLType(BSPLType bsplType);

    List<Account> findByTransactionElementType(TransactionElementType type);

    /**
     * ページネーション付きで勘定科目を検索.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param bsPlType BSPL区分（BS/PL/null）
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Account> findWithPagination(int page, int size, String bsPlType, String keyword);

    void update(Account account);

    void delete(String accountCode);

    void deleteAll();
}
