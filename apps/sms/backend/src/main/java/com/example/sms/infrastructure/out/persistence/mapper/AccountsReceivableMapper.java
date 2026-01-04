package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.invoice.AccountsReceivable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 売掛金残高 MyBatis Mapper.
 */
@Mapper
public interface AccountsReceivableMapper {

    void insert(AccountsReceivable accountsReceivable);

    Optional<AccountsReceivable> findById(@Param("id") Integer id);

    Optional<AccountsReceivable> findByCustomerAndBaseDate(
            @Param("customerCode") String customerCode,
            @Param("customerBranchNumber") String customerBranchNumber,
            @Param("baseDate") LocalDate baseDate);

    List<AccountsReceivable> findByCustomerCode(@Param("customerCode") String customerCode);

    List<AccountsReceivable> findByBaseDate(@Param("baseDate") LocalDate baseDate);

    List<AccountsReceivable> findAll();

    void update(AccountsReceivable accountsReceivable);

    void deleteById(@Param("id") Integer id);

    void deleteAll();
}
