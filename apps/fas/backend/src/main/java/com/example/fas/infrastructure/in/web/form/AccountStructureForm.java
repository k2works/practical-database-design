package com.example.fas.infrastructure.in.web.form;

import com.example.fas.application.port.in.dto.AccountStructureResponse;
import com.example.fas.application.port.in.dto.CreateAccountStructureCommand;
import com.example.fas.application.port.in.dto.UpdateAccountStructureCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 勘定科目構成フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStructureForm {

    @NotBlank(message = "勘定科目コードは必須です")
    @Size(max = 5, message = "勘定科目コードは5文字以内で入力してください")
    private String accountCode;

    @Size(max = 5, message = "親科目コードは5文字以内で入力してください")
    private String parentCode;

    /**
     * 登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateAccountStructureCommand toCreateCommand() {
        return CreateAccountStructureCommand.builder()
                .accountCode(accountCode)
                .parentCode(parentCode)
                .build();
    }

    /**
     * 更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateAccountStructureCommand toUpdateCommand() {
        return UpdateAccountStructureCommand.builder()
                .parentCode(parentCode)
                .build();
    }

    /**
     * レスポンスからフォームを生成.
     *
     * @param response 勘定科目構成レスポンス
     * @return フォーム
     */
    public static AccountStructureForm from(AccountStructureResponse response) {
        return AccountStructureForm.builder()
                .accountCode(response.getAccountCode())
                .parentCode(response.getParentCode())
                .build();
    }
}
