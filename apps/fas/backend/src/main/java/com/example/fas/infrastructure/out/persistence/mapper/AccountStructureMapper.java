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

    void update(AccountStructure structure);

    void deleteAll();
}
