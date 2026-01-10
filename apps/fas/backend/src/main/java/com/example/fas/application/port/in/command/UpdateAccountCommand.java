package com.example.fas.application.port.in.command;

import jakarta.validation.constraints.Size;

/**
 * 勘定科目更新コマンド.
 */
public record UpdateAccountCommand(
        @Size(max = 40, message = "勘定科目名は40文字以内で入力してください")
        String accountName,

        @Size(max = 10, message = "勘定科目略名は10文字以内で入力してください")
        String accountShortName,

        @Size(max = 40, message = "勘定科目カナは40文字以内で入力してください")
        String accountNameKana,

        String managementAccountingType,
        String expenseType,
        String ledgerOutputType,
        String subAccountType,
        String consumptionTaxType,
        String taxTransactionCode,
        String dueDateManagementType
) {
}
