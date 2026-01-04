package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.CustomerRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.infrastructure.out.persistence.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 顧客リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;

    @Override
    public void save(Customer customer) {
        customerMapper.insert(customer);
    }

    @Override
    public Optional<Customer> findByCodeAndBranch(String customerCode, String branchNumber) {
        return customerMapper.findByCodeAndBranch(customerCode, branchNumber);
    }

    @Override
    public List<Customer> findByCode(String customerCode) {
        return customerMapper.findByCode(customerCode);
    }

    @Override
    public List<Customer> findAll() {
        return customerMapper.findAll();
    }

    @Override
    public PageResult<Customer> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Customer> customers = customerMapper.findWithPagination(offset, size, keyword);
        long totalElements = customerMapper.count(keyword);
        return new PageResult<>(customers, page, size, totalElements);
    }

    @Override
    public void update(Customer customer) {
        customerMapper.update(customer);
    }

    @Override
    public void deleteByCodeAndBranch(String customerCode, String branchNumber) {
        customerMapper.deleteByCodeAndBranch(customerCode, branchNumber);
    }

    @Override
    public void deleteAll() {
        customerMapper.deleteAll();
    }
}
