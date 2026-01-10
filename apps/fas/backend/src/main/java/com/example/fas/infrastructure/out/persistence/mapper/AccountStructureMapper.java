package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.account.AccountStructure;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 勘定科目構成マッパー.
 */
@Mapper
public interface AccountStructureMapper {

    void insert(AccountStructure structure);

    Optional<AccountStructure> findByCode(@Param("accountCode") String accountCode);

    List<AccountStructure> findAll();

    List<AccountStructure> findByPathContaining(@Param("pathSegment") String pathSegment);

    List<AccountStructure> findChildren(@Param("parentCode") String parentCode);

    /**
     * ページネーション付きで勘定科目構成を検索.
     *
     * @param offset  オフセット
     * @param limit   リミット
     * @param keyword キーワード
     * @return 勘定科目構成リスト
     */
    List<AccountStructure> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword);

    /**
     * 勘定科目構成の総件数を取得.
     *
     * @param keyword キーワード
     * @return 総件数
     */
    long count(@Param("keyword") String keyword);

    void update(AccountStructure structure);

    void delete(@Param("accountCode") String accountCode);

    void deleteAll();
}
