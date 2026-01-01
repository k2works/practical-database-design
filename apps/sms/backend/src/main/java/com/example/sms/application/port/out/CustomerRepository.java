package com.example.sms.application.port.out;

import com.example.sms.domain.model.partner.Customer;

import java.util.List;
import java.util.Optional;

/**
 * 顧客リポジトリ（Output Port）.
 */
public interface CustomerRepository {

    void save(Customer customer);

    Optional<Customer> findByCodeAndBranch(String customerCode, String branchNumber);

    List<Customer> findByCode(String customerCode);

    List<Customer> findAll();

    void update(Customer customer);

    void deleteAll();
}
