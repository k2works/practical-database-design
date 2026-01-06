package com.example.fas.domain.model.account;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勘定科目エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String accountCode;
    private String accountName;
    private String accountShortName;
    private String accountNameKana;
    private BSPLType bsplType;
    private DebitCreditType debitCreditType;
    private TransactionElementType transactionElementType;
    private AggregationType aggregationType;
    private String managementAccountingType;
    private String expenseType;
    private String ledgerOutputType;
    private String subAccountType;
    private String consumptionTaxType;
    private String taxTransactionCode;
    private String dueDateManagementType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
