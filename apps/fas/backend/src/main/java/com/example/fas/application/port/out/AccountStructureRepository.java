package com.example.fas.application.port.out;

import com.example.fas.domain.model.account.AccountStructure;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;
import java.util.Optional;

/**
 * 勘定科目構成リポジトリ（Output Port）.
 */
public interface AccountStructureRepository {

    void save(AccountStructure structure);

    Optional<AccountStructure> findByCode(String accountCode);

    List<AccountStructure> findAll();

    List<AccountStructure> findByPathContaining(String pathSegment);

    List<AccountStructure> findChildren(String parentCode);

    /**
     * ページネーション付きで勘定科目構成を検索.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<AccountStructure> findWithPagination(int page, int size, String keyword);

    void update(AccountStructure structure);

    void delete(String accountCode);

    void deleteAll();
}
