package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.AccountStructureRepository;
import com.example.fas.domain.model.account.AccountStructure;
import com.example.fas.infrastructure.out.persistence.mapper.AccountStructureMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 勘定科目構成リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class AccountStructureRepositoryImpl implements AccountStructureRepository {

    private final AccountStructureMapper accountStructureMapper;

    @Override
    public void save(AccountStructure structure) {
        accountStructureMapper.insert(structure);
    }

    @Override
    public Optional<AccountStructure> findByCode(String accountCode) {
        return accountStructureMapper.findByCode(accountCode);
    }

    @Override
    public List<AccountStructure> findAll() {
        return accountStructureMapper.findAll();
    }

    @Override
    public List<AccountStructure> findByPathContaining(String pathSegment) {
        return accountStructureMapper.findByPathContaining(pathSegment);
    }

    @Override
    public List<AccountStructure> findChildren(String parentCode) {
        return accountStructureMapper.findChildren(parentCode);
    }

    @Override
    public void update(AccountStructure structure) {
        accountStructureMapper.update(structure);
    }

    @Override
    public void deleteAll() {
        accountStructureMapper.deleteAll();
    }
}
