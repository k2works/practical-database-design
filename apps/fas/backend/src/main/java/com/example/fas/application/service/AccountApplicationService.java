package com.example.fas.application.service;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.command.CreateAccountCommand;
import com.example.fas.application.port.in.command.UpdateAccountCommand;
import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.domain.exception.AccountAlreadyExistsException;
import com.example.fas.domain.exception.AccountNotFoundException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 勘定科目アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountApplicationService implements AccountUseCase {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse getAccount(String accountCode) {
        Account account = accountRepository.findByCode(accountCode)
                .orElseThrow(() -> new AccountNotFoundException(accountCode));
        return AccountResponse.from(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Override
    public List<AccountResponse> getAccountsByBsPlType(String bsPlType) {
        BSPLType type = BSPLType.fromDisplayName(bsPlType);
        return accountRepository.findByBSPLType(type).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Override
    public PageResult<AccountResponse> getAccounts(int page, int size, String bsPlType, String keyword) {
        PageResult<Account> pageResult = accountRepository.findWithPagination(page, size, bsPlType, keyword);
        List<AccountResponse> content = pageResult.getContent().stream()
                .map(AccountResponse::from)
                .toList();
        return new PageResult<>(content, pageResult.getPage(), pageResult.getSize(), pageResult.getTotalElements());
    }

    @Override
    public List<AccountResponse> getPostingAccounts() {
        return accountRepository.findAll().stream()
                .filter(a -> a.getAggregationType() == AggregationType.POSTING)
                .map(AccountResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountCommand command) {
        if (accountRepository.findByCode(command.accountCode()).isPresent()) {
            throw new AccountAlreadyExistsException(command.accountCode());
        }

        Account account = Account.builder()
                .accountCode(command.accountCode())
                .accountName(command.accountName())
                .accountShortName(command.accountShortName())
                .accountNameKana(command.accountNameKana())
                .bsplType(BSPLType.fromDisplayName(command.bsPlType()))
                .debitCreditType(DebitCreditType.fromDisplayName(command.dcType()))
                .transactionElementType(
                        TransactionElementType.fromDisplayName(command.elementType()))
                .aggregationType(AggregationType.fromDisplayName(command.summaryType()))
                .managementAccountingType(command.managementAccountingType())
                .expenseType(command.expenseType())
                .ledgerOutputType(command.ledgerOutputType())
                .subAccountType(command.subAccountType())
                .consumptionTaxType(command.consumptionTaxType())
                .taxTransactionCode(command.taxTransactionCode())
                .dueDateManagementType(command.dueDateManagementType())
                .build();

        accountRepository.save(account);

        return AccountResponse.from(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(String accountCode, UpdateAccountCommand command) {
        Account account = accountRepository.findByCode(accountCode)
                .orElseThrow(() -> new AccountNotFoundException(accountCode));

        applyBasicUpdates(account, command);
        applyAdditionalUpdates(account, command);

        accountRepository.update(account);

        return AccountResponse.from(account);
    }

    private void applyBasicUpdates(Account account, UpdateAccountCommand command) {
        if (command.accountName() != null) {
            account.setAccountName(command.accountName());
        }
        if (command.accountShortName() != null) {
            account.setAccountShortName(command.accountShortName());
        }
        if (command.accountNameKana() != null) {
            account.setAccountNameKana(command.accountNameKana());
        }
        if (command.managementAccountingType() != null) {
            account.setManagementAccountingType(command.managementAccountingType());
        }
        if (command.expenseType() != null) {
            account.setExpenseType(command.expenseType());
        }
    }

    private void applyAdditionalUpdates(Account account, UpdateAccountCommand command) {
        if (command.ledgerOutputType() != null) {
            account.setLedgerOutputType(command.ledgerOutputType());
        }
        if (command.subAccountType() != null) {
            account.setSubAccountType(command.subAccountType());
        }
        if (command.consumptionTaxType() != null) {
            account.setConsumptionTaxType(command.consumptionTaxType());
        }
        if (command.taxTransactionCode() != null) {
            account.setTaxTransactionCode(command.taxTransactionCode());
        }
        if (command.dueDateManagementType() != null) {
            account.setDueDateManagementType(command.dueDateManagementType());
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String accountCode) {
        accountRepository.findByCode(accountCode)
                .orElseThrow(() -> new AccountNotFoundException(accountCode));
        accountRepository.delete(accountCode);
    }
}
