package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.AccountsReceivableRepository;
import com.example.sms.domain.model.invoice.AccountsReceivable;
import com.example.sms.infrastructure.out.persistence.mapper.AccountsReceivableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 売掛金残高リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class AccountsReceivableRepositoryImpl implements AccountsReceivableRepository {

    private final AccountsReceivableMapper accountsReceivableMapper;

    @Override
    public void save(AccountsReceivable accountsReceivable) {
        accountsReceivableMapper.insert(accountsReceivable);
    }

    @Override
    public Optional<AccountsReceivable> findById(Integer id) {
        return accountsReceivableMapper.findById(id);
    }

    @Override
    public Optional<AccountsReceivable> findByCustomerAndBaseDate(
            String customerCode, String customerBranchNumber, LocalDate baseDate) {
        return accountsReceivableMapper.findByCustomerAndBaseDate(customerCode, customerBranchNumber, baseDate);
    }

    @Override
    public List<AccountsReceivable> findByCustomerCode(String customerCode) {
        return accountsReceivableMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<AccountsReceivable> findByBaseDate(LocalDate baseDate) {
        return accountsReceivableMapper.findByBaseDate(baseDate);
    }

    @Override
    public List<AccountsReceivable> findAll() {
        return accountsReceivableMapper.findAll();
    }

    @Override
    public void update(AccountsReceivable accountsReceivable) {
        accountsReceivableMapper.update(accountsReceivable);
    }

    @Override
    public void deleteById(Integer id) {
        accountsReceivableMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        accountsReceivableMapper.deleteAll();
    }
}
