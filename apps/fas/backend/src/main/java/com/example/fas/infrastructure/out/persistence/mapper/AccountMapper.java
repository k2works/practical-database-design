package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.account.Account;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 勘定科目マッパー.
 */
@Mapper
public interface AccountMapper {

    void insert(Account account);

    Optional<Account> findByCode(@Param("accountCode") String accountCode);

    List<Account> findAll();

    List<Account> findByBSPLType(@Param("bsplType") String bsplType);

    List<Account> findByTransactionElementType(@Param("transactionElementType") String type);

    /**
     * ページネーション付きで勘定科目を検索.
     *
     * @param offset   オフセット
     * @param limit    リミット
     * @param bsPlType BSPL区分（BS/PL/null）
     * @param keyword  キーワード
     * @return 勘定科目リスト
     */
    List<Account> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("bsPlType") String bsPlType,
            @Param("keyword") String keyword);

    /**
     * 勘定科目の総件数を取得.
     *
     * @param bsPlType BSPL区分（BS/PL/null）
     * @param keyword  キーワード
     * @return 総件数
     */
    long count(@Param("bsPlType") String bsPlType, @Param("keyword") String keyword);

    void update(Account account);

    void delete(@Param("accountCode") String accountCode);

    void deleteAll();
}
