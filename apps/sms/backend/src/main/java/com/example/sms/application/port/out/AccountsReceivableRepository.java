package com.example.sms.application.port.out;

import com.example.sms.domain.model.invoice.AccountsReceivable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 売掛金残高リポジトリ（Output Port）.
 */
public interface AccountsReceivableRepository {

    void save(AccountsReceivable accountsReceivable);

    Optional<AccountsReceivable> findById(Integer id);

    Optional<AccountsReceivable> findByCustomerAndBaseDate(String customerCode, String customerBranchNumber, LocalDate baseDate);

    List<AccountsReceivable> findByCustomerCode(String customerCode);

    List<AccountsReceivable> findByBaseDate(LocalDate baseDate);

    List<AccountsReceivable> findAll();

    void update(AccountsReceivable accountsReceivable);

    void deleteById(Integer id);

    void deleteAll();
}
