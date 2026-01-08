package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.account.Account;
import lombok.Builder;
import lombok.Data;

/**
 * 勘定科目レスポンス DTO.
 */
@Data
@Builder
public class AccountResponse {
    private String accountCode;
    private String accountName;
    private String accountShortName;
    private String accountNameKana;
    private String bsPlType;
    private String dcType;
    private String elementType;
    private String summaryType;
    private String managementAccountingType;
    private String expenseType;
    private String ledgerOutputType;
    private String subAccountType;
    private String consumptionTaxType;
    private String taxTransactionCode;
    private String dueDateManagementType;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param account 勘定科目エンティティ
     * @return レスポンス DTO
     */
    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
                .accountCode(account.getAccountCode())
                .accountName(account.getAccountName())
                .accountShortName(account.getAccountShortName())
                .accountNameKana(account.getAccountNameKana())
                .bsPlType(account.getBsplType() != null
                        ? account.getBsplType().getDisplayName() : null)
                .dcType(account.getDebitCreditType() != null
                        ? account.getDebitCreditType().getDisplayName() : null)
                .elementType(account.getTransactionElementType() != null
                        ? account.getTransactionElementType().getDisplayName() : null)
                .summaryType(account.getAggregationType() != null
                        ? account.getAggregationType().getDisplayName() : null)
                .managementAccountingType(account.getManagementAccountingType())
                .expenseType(account.getExpenseType())
                .ledgerOutputType(account.getLedgerOutputType())
                .subAccountType(account.getSubAccountType())
                .consumptionTaxType(account.getConsumptionTaxType())
                .taxTransactionCode(account.getTaxTransactionCode())
                .dueDateManagementType(account.getDueDateManagementType())
                .build();
    }
}
