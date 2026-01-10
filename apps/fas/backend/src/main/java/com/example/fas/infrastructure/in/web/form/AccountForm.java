package com.example.fas.infrastructure.in.web.form;

import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.in.dto.CreateAccountCommand;
import com.example.fas.application.port.in.dto.UpdateAccountCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 勘定科目登録・編集フォーム.
 */
@Data
public class AccountForm {

    @NotBlank(message = "勘定科目コードは必須です")
    @Size(max = 5, message = "勘定科目コードは5文字以内で入力してください")
    private String accountCode;

    @NotBlank(message = "勘定科目名は必須です")
    @Size(max = 40, message = "勘定科目名は40文字以内で入力してください")
    private String accountName;

    @Size(max = 10, message = "勘定科目略名は10文字以内で入力してください")
    private String accountShortName;

    @Size(max = 40, message = "勘定科目カナは40文字以内で入力してください")
    private String accountNameKana;

    @NotBlank(message = "BSPL区分は必須です")
    @Pattern(regexp = "^(BS|PL)$", message = "BSPL区分はBSまたはPLで選択してください")
    private String bsPlType;

    @NotBlank(message = "貸借区分は必須です")
    @Pattern(regexp = "^(借方|貸方)$", message = "貸借区分は借方または貸方で選択してください")
    private String dcType;

    @NotBlank(message = "取引要素区分は必須です")
    @Pattern(regexp = "^(資産|負債|資本|収益|費用)$", message = "取引要素区分を選択してください")
    private String elementType;

    @NotBlank(message = "集計区分は必須です")
    @Pattern(regexp = "^(見出科目|集計科目|計上科目)$", message = "集計区分を選択してください")
    private String summaryType;

    /**
     * デフォルトコンストラクタ.
     */
    public AccountForm() {
        // 初期値
        this.bsPlType = "BS";
        this.dcType = "借方";
        this.elementType = "資産";
        this.summaryType = "計上科目";
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateAccountCommand toCreateCommand() {
        return CreateAccountCommand.builder()
            .accountCode(this.accountCode)
            .accountName(this.accountName)
            .accountShortName(this.accountShortName)
            .accountNameKana(this.accountNameKana)
            .bsPlType(this.bsPlType)
            .dcType(this.dcType)
            .elementType(this.elementType)
            .summaryType(this.summaryType)
            .build();
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateAccountCommand toUpdateCommand() {
        return UpdateAccountCommand.builder()
            .accountName(this.accountName)
            .accountShortName(this.accountShortName)
            .accountNameKana(this.accountNameKana)
            .build();
    }

    /**
     * AccountResponse からフォームを生成.
     *
     * @param account 勘定科目レスポンス
     * @return フォーム
     */
    public static AccountForm from(AccountResponse account) {
        AccountForm form = new AccountForm();
        form.setAccountCode(account.getAccountCode());
        form.setAccountName(account.getAccountName());
        form.setAccountShortName(account.getAccountShortName());
        form.setAccountNameKana(account.getAccountNameKana());
        form.setBsPlType(account.getBsPlType());
        form.setDcType(account.getDcType());
        form.setElementType(account.getElementType());
        form.setSummaryType(account.getSummaryType());
        return form;
    }
}
