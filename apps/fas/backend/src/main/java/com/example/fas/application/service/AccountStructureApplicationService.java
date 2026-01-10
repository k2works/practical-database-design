package com.example.fas.application.service;

import com.example.fas.application.port.in.AccountStructureUseCase;
import com.example.fas.application.port.in.dto.AccountStructureResponse;
import com.example.fas.application.port.in.dto.CreateAccountStructureCommand;
import com.example.fas.application.port.in.dto.UpdateAccountStructureCommand;
import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.AccountStructureRepository;
import com.example.fas.domain.exception.AccountStructureNotFoundException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AccountStructure;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 勘定科目構成アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountStructureApplicationService implements AccountStructureUseCase {

    private final AccountStructureRepository accountStructureRepository;
    private final AccountRepository accountRepository;

    @Override
    public AccountStructureResponse getAccountStructure(String accountCode) {
        AccountStructure structure = accountStructureRepository.findByCode(accountCode)
                .orElseThrow(() -> new AccountStructureNotFoundException(accountCode));
        String accountName = getAccountName(accountCode);
        return AccountStructureResponse.from(structure, accountName);
    }

    @Override
    public List<AccountStructureResponse> getAllAccountStructures() {
        List<AccountStructure> structures = accountStructureRepository.findAll();
        Map<String, String> accountNames = getAccountNames(structures);
        return structures.stream()
                .map(s -> AccountStructureResponse.from(s, accountNames.get(s.getAccountCode())))
                .toList();
    }

    @Override
    public PageResult<AccountStructureResponse> getAccountStructures(int page, int size, String keyword) {
        PageResult<AccountStructure> pageResult = accountStructureRepository
                .findWithPagination(page, size, keyword);
        Map<String, String> accountNames = getAccountNames(pageResult.getContent());
        List<AccountStructureResponse> content = pageResult.getContent().stream()
                .map(s -> AccountStructureResponse.from(s, accountNames.get(s.getAccountCode())))
                .toList();
        return new PageResult<>(content, pageResult.getPage(), pageResult.getSize(),
                pageResult.getTotalElements());
    }

    @Override
    public List<AccountStructureResponse> getChildren(String parentCode) {
        List<AccountStructure> children = accountStructureRepository.findChildren(parentCode);
        Map<String, String> accountNames = getAccountNames(children);
        return children.stream()
                .map(s -> AccountStructureResponse.from(s, accountNames.get(s.getAccountCode())))
                .toList();
    }

    @Override
    @Transactional
    public AccountStructureResponse createAccountStructure(CreateAccountStructureCommand command) {
        String accountPath = buildPath(command.getParentCode(), command.getAccountCode());

        AccountStructure structure = AccountStructure.builder()
                .accountCode(command.getAccountCode())
                .accountPath(accountPath)
                .updatedBy("system")
                .build();

        accountStructureRepository.save(structure);

        String accountName = getAccountName(command.getAccountCode());
        return AccountStructureResponse.from(structure, accountName);
    }

    @Override
    @Transactional
    public AccountStructureResponse updateAccountStructure(String accountCode,
                                                            UpdateAccountStructureCommand command) {
        AccountStructure structure = accountStructureRepository.findByCode(accountCode)
                .orElseThrow(() -> new AccountStructureNotFoundException(accountCode));

        String newPath = buildPath(command.getParentCode(), accountCode);
        structure.setAccountPath(newPath);
        structure.setUpdatedBy("system");

        accountStructureRepository.update(structure);

        String accountName = getAccountName(accountCode);
        return AccountStructureResponse.from(structure, accountName);
    }

    @Override
    @Transactional
    public void deleteAccountStructure(String accountCode) {
        accountStructureRepository.findByCode(accountCode)
                .orElseThrow(() -> new AccountStructureNotFoundException(accountCode));
        accountStructureRepository.delete(accountCode);
    }

    private String buildPath(String parentCode, String accountCode) {
        if (parentCode == null || parentCode.isEmpty()) {
            return accountCode;
        }
        AccountStructure parent = accountStructureRepository.findByCode(parentCode)
                .orElseThrow(() -> new AccountStructureNotFoundException(parentCode));
        return parent.getAccountPath() + "~" + accountCode;
    }

    private String getAccountName(String accountCode) {
        return accountRepository.findByCode(accountCode)
                .map(Account::getAccountName)
                .orElse(accountCode);
    }

    private Map<String, String> getAccountNames(List<AccountStructure> structures) {
        List<String> codes = structures.stream()
                .map(AccountStructure::getAccountCode)
                .toList();
        return accountRepository.findAll().stream()
                .filter(a -> codes.contains(a.getAccountCode()))
                .collect(Collectors.toMap(Account::getAccountCode, Account::getAccountName));
    }
}
