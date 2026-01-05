package com.example.sms.application.service;

import com.example.sms.application.port.in.CustomerUseCase;
import com.example.sms.application.port.in.command.CreateCustomerCommand;
import com.example.sms.application.port.in.command.UpdateCustomerCommand;
import com.example.sms.application.port.out.CustomerRepository;
import com.example.sms.domain.exception.CustomerNotFoundException;
import com.example.sms.domain.exception.DuplicateCustomerException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 顧客アプリケーションサービス.
 */
@Service
@Transactional
public class CustomerService implements CustomerUseCase {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(CreateCustomerCommand command) {
        String branchNumber = command.customerBranchNumber() != null
            ? command.customerBranchNumber() : "00";

        customerRepository.findByCodeAndBranch(command.customerCode(), branchNumber)
            .ifPresent(existing -> {
                throw new DuplicateCustomerException(command.customerCode(), branchNumber);
            });

        Customer customer = Customer.builder()
            .customerCode(command.customerCode())
            .customerBranchNumber(branchNumber)
            .customerCategory(command.customerCategory())
            .billingCode(command.billingCode())
            .billingBranchNumber(command.billingBranchNumber())
            .collectionCode(command.collectionCode())
            .collectionBranchNumber(command.collectionBranchNumber())
            .customerName(command.customerName())
            .customerNameKana(command.customerNameKana())
            .ourRepresentativeCode(command.ourRepresentativeCode())
            .customerRepresentativeName(command.customerRepresentativeName())
            .customerDepartmentName(command.customerDepartmentName())
            .customerPostalCode(command.customerPostalCode())
            .customerPrefecture(command.customerPrefecture())
            .customerAddress1(command.customerAddress1())
            .customerAddress2(command.customerAddress2())
            .customerPhone(command.customerPhone())
            .customerFax(command.customerFax())
            .customerEmail(command.customerEmail())
            .billingType(command.billingType())
            .closingDay1(command.closingDay1())
            .paymentMonth1(command.paymentMonth1())
            .paymentDay1(command.paymentDay1())
            .paymentMethod1(command.paymentMethod1())
            .closingDay2(command.closingDay2())
            .paymentMonth2(command.paymentMonth2())
            .paymentDay2(command.paymentDay2())
            .paymentMethod2(command.paymentMethod2())
            .build();

        customerRepository.save(customer);
        return customer;
    }

    @Override
    public Customer updateCustomer(String customerCode, String branchNumber, UpdateCustomerCommand command) {
        Customer existing = customerRepository.findByCodeAndBranch(customerCode, branchNumber)
            .orElseThrow(() -> new CustomerNotFoundException(customerCode, branchNumber));

        Customer updated = Customer.builder()
            .customerCode(customerCode)
            .customerBranchNumber(branchNumber)
            .customerCategory(coalesce(command.customerCategory(), existing.getCustomerCategory()))
            .billingCode(coalesce(command.billingCode(), existing.getBillingCode()))
            .billingBranchNumber(coalesce(command.billingBranchNumber(), existing.getBillingBranchNumber()))
            .collectionCode(coalesce(command.collectionCode(), existing.getCollectionCode()))
            .collectionBranchNumber(coalesce(command.collectionBranchNumber(), existing.getCollectionBranchNumber()))
            .customerName(coalesce(command.customerName(), existing.getCustomerName()))
            .customerNameKana(coalesce(command.customerNameKana(), existing.getCustomerNameKana()))
            .ourRepresentativeCode(coalesce(command.ourRepresentativeCode(), existing.getOurRepresentativeCode()))
            .customerRepresentativeName(coalesce(command.customerRepresentativeName(),
                existing.getCustomerRepresentativeName()))
            .customerDepartmentName(coalesce(command.customerDepartmentName(), existing.getCustomerDepartmentName()))
            .customerPostalCode(coalesce(command.customerPostalCode(), existing.getCustomerPostalCode()))
            .customerPrefecture(coalesce(command.customerPrefecture(), existing.getCustomerPrefecture()))
            .customerAddress1(coalesce(command.customerAddress1(), existing.getCustomerAddress1()))
            .customerAddress2(coalesce(command.customerAddress2(), existing.getCustomerAddress2()))
            .customerPhone(coalesce(command.customerPhone(), existing.getCustomerPhone()))
            .customerFax(coalesce(command.customerFax(), existing.getCustomerFax()))
            .customerEmail(coalesce(command.customerEmail(), existing.getCustomerEmail()))
            .billingType(coalesce(command.billingType(), existing.getBillingType()))
            .closingDay1(coalesce(command.closingDay1(), existing.getClosingDay1()))
            .paymentMonth1(coalesce(command.paymentMonth1(), existing.getPaymentMonth1()))
            .paymentDay1(coalesce(command.paymentDay1(), existing.getPaymentDay1()))
            .paymentMethod1(coalesce(command.paymentMethod1(), existing.getPaymentMethod1()))
            .closingDay2(coalesce(command.closingDay2(), existing.getClosingDay2()))
            .paymentMonth2(coalesce(command.paymentMonth2(), existing.getPaymentMonth2()))
            .paymentDay2(coalesce(command.paymentDay2(), existing.getPaymentDay2()))
            .paymentMethod2(coalesce(command.paymentMethod2(), existing.getPaymentMethod2()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .build();

        customerRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Customer> getCustomers(int page, int size, String keyword) {
        return customerRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByCode(String customerCode) {
        return customerRepository.findByCode(customerCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByCodeAndBranch(String customerCode, String branchNumber) {
        return customerRepository.findByCodeAndBranch(customerCode, branchNumber)
            .orElseThrow(() -> new CustomerNotFoundException(customerCode, branchNumber));
    }

    @Override
    public void deleteCustomer(String customerCode, String branchNumber) {
        customerRepository.findByCodeAndBranch(customerCode, branchNumber)
            .orElseThrow(() -> new CustomerNotFoundException(customerCode, branchNumber));

        customerRepository.deleteByCodeAndBranch(customerCode, branchNumber);
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
