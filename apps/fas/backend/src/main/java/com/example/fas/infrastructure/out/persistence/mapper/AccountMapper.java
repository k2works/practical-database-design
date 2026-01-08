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

    void update(Account account);

    void delete(@Param("accountCode") String accountCode);

    void deleteAll();
}
