package com.example.fas.application.service;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.in.dto.CreateAccountCommand;
import com.example.fas.application.port.in.dto.UpdateAccountCommand;
import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.domain.exception.AccountAlreadyExistsException;
import com.example.fas.domain.exception.AccountNotFoundException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
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
    public List<AccountResponse> getPostingAccounts() {
        return accountRepository.findAll().stream()
                .filter(a -> a.getAggregationType() == AggregationType.POSTING)
                .map(AccountResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountCommand command) {
        if (accountRepository.findByCode(command.getAccountCode()).isPresent()) {
            throw new AccountAlreadyExistsException(command.getAccountCode());
        }

        Account account = Account.builder()
                .accountCode(command.getAccountCode())
                .accountName(command.getAccountName())
                .accountShortName(command.getAccountShortName())
                .accountNameKana(command.getAccountNameKana())
                .bsplType(BSPLType.fromDisplayName(command.getBsPlType()))
                .debitCreditType(DebitCreditType.fromDisplayName(command.getDcType()))
                .transactionElementType(
                        TransactionElementType.fromDisplayName(command.getElementType()))
                .aggregationType(AggregationType.fromDisplayName(command.getSummaryType()))
                .managementAccountingType(command.getManagementAccountingType())
                .expenseType(command.getExpenseType())
                .ledgerOutputType(command.getLedgerOutputType())
                .subAccountType(command.getSubAccountType())
                .consumptionTaxType(command.getConsumptionTaxType())
                .taxTransactionCode(command.getTaxTransactionCode())
                .dueDateManagementType(command.getDueDateManagementType())
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
        if (command.getAccountName() != null) {
            account.setAccountName(command.getAccountName());
        }
        if (command.getAccountShortName() != null) {
            account.setAccountShortName(command.getAccountShortName());
        }
        if (command.getAccountNameKana() != null) {
            account.setAccountNameKana(command.getAccountNameKana());
        }
        if (command.getManagementAccountingType() != null) {
            account.setManagementAccountingType(command.getManagementAccountingType());
        }
        if (command.getExpenseType() != null) {
            account.setExpenseType(command.getExpenseType());
        }
    }

    private void applyAdditionalUpdates(Account account, UpdateAccountCommand command) {
        if (command.getLedgerOutputType() != null) {
            account.setLedgerOutputType(command.getLedgerOutputType());
        }
        if (command.getSubAccountType() != null) {
            account.setSubAccountType(command.getSubAccountType());
        }
        if (command.getConsumptionTaxType() != null) {
            account.setConsumptionTaxType(command.getConsumptionTaxType());
        }
        if (command.getTaxTransactionCode() != null) {
            account.setTaxTransactionCode(command.getTaxTransactionCode());
        }
        if (command.getDueDateManagementType() != null) {
            account.setDueDateManagementType(command.getDueDateManagementType());
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
