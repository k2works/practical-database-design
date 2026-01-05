package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
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

    /**
     * ページネーション付きで顧客を検索.
     */
    PageResult<Customer> findWithPagination(int page, int size, String keyword);

    void update(Customer customer);

    void deleteByCodeAndBranch(String customerCode, String branchNumber);

    void deleteAll();
}
