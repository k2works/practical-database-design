package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.partner.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 顧客マッパー.
 */
@Mapper
public interface CustomerMapper {

    void insert(Customer customer);

    Optional<Customer> findByCodeAndBranch(@Param("customerCode") String customerCode,
                                           @Param("branchNumber") String branchNumber);

    List<Customer> findByCode(String customerCode);

    List<Customer> findAll();

    void update(Customer customer);

    void deleteByCodeAndBranch(@Param("customerCode") String customerCode,
                               @Param("branchNumber") String branchNumber);

    void deleteAll();
}
