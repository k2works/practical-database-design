package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateCustomerCommand;
import com.example.sms.application.port.in.command.UpdateCustomerCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Customer;

import java.util.List;

/**
 * 顧客ユースケース（Input Port）.
 */
public interface CustomerUseCase {

    Customer createCustomer(CreateCustomerCommand command);

    Customer updateCustomer(String customerCode, String branchNumber, UpdateCustomerCommand command);

    List<Customer> getAllCustomers();

    /**
     * ページネーション付きで顧客を取得.
     */
    PageResult<Customer> getCustomers(int page, int size, String keyword);

    List<Customer> getCustomersByCode(String customerCode);

    Customer getCustomerByCodeAndBranch(String customerCode, String branchNumber);

    void deleteCustomer(String customerCode, String branchNumber);
}
