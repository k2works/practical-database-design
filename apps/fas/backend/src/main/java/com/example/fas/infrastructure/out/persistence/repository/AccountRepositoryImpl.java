package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.infrastructure.out.persistence.mapper.AccountMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 勘定科目リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper accountMapper;

    @Override
    public void save(Account account) {
        accountMapper.insert(account);
    }

    @Override
    public Optional<Account> findByCode(String accountCode) {
        return accountMapper.findByCode(accountCode);
    }

    @Override
    public List<Account> findAll() {
        return accountMapper.findAll();
    }

    @Override
    public List<Account> findByBSPLType(BSPLType bsplType) {
        return accountMapper.findByBSPLType(bsplType.getDisplayName());
    }

    @Override
    public List<Account> findByTransactionElementType(TransactionElementType type) {
        return accountMapper.findByTransactionElementType(type.getDisplayName());
    }

    @Override
    public void update(Account account) {
        accountMapper.update(account);
    }

    @Override
    public void delete(String accountCode) {
        accountMapper.delete(accountCode);
    }

    @Override
    public void deleteAll() {
        accountMapper.deleteAll();
    }
}
