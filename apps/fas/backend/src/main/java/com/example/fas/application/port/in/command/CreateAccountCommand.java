package com.example.fas.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 勘定科目登録コマンド.
 */
public record CreateAccountCommand(
        @NotBlank(message = "勘定科目コードは必須です")
        @Size(max = 5, message = "勘定科目コードは5文字以内で入力してください")
        String accountCode,

        @NotBlank(message = "勘定科目名は必須です")
        @Size(max = 40, message = "勘定科目名は40文字以内で入力してください")
        String accountName,

        @Size(max = 10, message = "勘定科目略名は10文字以内で入力してください")
        String accountShortName,

        @Size(max = 40, message = "勘定科目カナは40文字以内で入力してください")
        String accountNameKana,

        @NotBlank(message = "BSPL区分は必須です")
        @Pattern(regexp = "^(BS|PL)$", message = "BSPL区分はBSまたはPLで入力してください")
        String bsPlType,

        @NotBlank(message = "貸借区分は必須です")
        @Pattern(regexp = "^(借方|貸方)$", message = "貸借区分は借方または貸方で入力してください")
        String dcType,

        @NotBlank(message = "取引要素区分は必須です")
        @Pattern(regexp = "^(資産|負債|資本|収益|費用)$", message = "取引要素区分は資産/負債/資本/収益/費用で入力してください")
        String elementType,

        @NotBlank(message = "集計区分は必須です")
        @Pattern(regexp = "^(見出科目|集計科目|計上科目)$", message = "集計区分は見出科目/集計科目/計上科目で入力してください")
        String summaryType,

        String parentAccountCode,
        String managementAccountingType,
        String expenseType,
        String ledgerOutputType,
        String subAccountType,
        String consumptionTaxType,
        String taxTransactionCode,
        String dueDateManagementType
) {
}
