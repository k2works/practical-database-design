package com.example.fas.infrastructure.in.web.form;

import com.example.fas.application.port.in.command.CreateJournalCommand.DebitCreditCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 仕訳明細行フォーム.
 */
@Data
public class JournalLineForm {

    @NotBlank(message = "貸借区分は必須です")
    private String debitCreditType;

    @NotBlank(message = "勘定科目コードは必須です")
    private String accountCode;

    private String subAccountCode;

    private String departmentCode;

    @NotNull(message = "金額は必須です")
    @Positive(message = "金額は正の数で入力してください")
    private BigDecimal amount;

    private String taxType;

    private Integer taxRate;

    private String lineSummary;

    /**
     * フォームをコマンドに変換.
     *
     * @return 借方・貸方明細コマンド
     */
    public DebitCreditCommand toCommand() {
        return DebitCreditCommand.builder()
            .debitCreditType(this.debitCreditType)
            .accountCode(this.accountCode)
            .subAccountCode(this.subAccountCode)
            .departmentCode(this.departmentCode)
            .amount(this.amount)
            .taxType(this.taxType)
            .taxRate(this.taxRate)
            .build();
    }
}
