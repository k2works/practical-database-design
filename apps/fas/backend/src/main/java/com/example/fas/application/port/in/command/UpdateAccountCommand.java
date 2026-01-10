package com.example.fas.application.port.in.command;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勘定科目更新コマンド DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountCommand {

    @Size(max = 40, message = "勘定科目名は40文字以内で入力してください")
    private String accountName;

    @Size(max = 10, message = "勘定科目略名は10文字以内で入力してください")
    private String accountShortName;

    @Size(max = 40, message = "勘定科目カナは40文字以内で入力してください")
    private String accountNameKana;

    private String managementAccountingType;
    private String expenseType;
    private String ledgerOutputType;
    private String subAccountType;
    private String consumptionTaxType;
    private String taxTransactionCode;
    private String dueDateManagementType;
}
