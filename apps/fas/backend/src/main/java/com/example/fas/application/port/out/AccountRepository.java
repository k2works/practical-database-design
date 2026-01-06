package com.example.fas.application.port.out;

import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.TransactionElementType;
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

    void update(Account account);

    void deleteAll();
}
