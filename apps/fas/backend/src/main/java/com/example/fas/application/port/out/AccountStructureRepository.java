package com.example.fas.application.port.out;

import com.example.fas.domain.model.account.AccountStructure;
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

    void update(AccountStructure structure);

    void deleteAll();
}
