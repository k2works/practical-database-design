package com.example.fas.infrastructure.in.web.form;

import com.example.fas.application.port.in.command.CreateTaxTransactionCommand;
import com.example.fas.application.port.in.command.UpdateTaxTransactionCommand;
import com.example.fas.application.port.in.dto.TaxTransactionResponse;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 課税取引登録・編集フォーム.
 */
@Data
public class TaxTransactionForm {

    @NotBlank(message = "課税取引コードは必須です")
    @Size(max = 3, message = "課税取引コードは3文字以内で入力してください")
    private String taxCode;

    @NotBlank(message = "課税取引名は必須です")
    @Size(max = 40, message = "課税取引名は40文字以内で入力してください")
    private String taxName;

    @NotNull(message = "税率は必須です")
    @DecimalMin(value = "0.00", message = "税率は0以上で入力してください")
    @DecimalMax(value = "1.00", message = "税率は1以下で入力してください")
    private BigDecimal taxRate;

    /**
     * デフォルトコンストラクタ.
     */
    public TaxTransactionForm() {
        this.taxRate = new BigDecimal("0.10");
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateTaxTransactionCommand toCreateCommand() {
        return new CreateTaxTransactionCommand(
                this.taxCode,
                this.taxName,
                this.taxRate
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateTaxTransactionCommand toUpdateCommand() {
        return new UpdateTaxTransactionCommand(
                this.taxName,
                this.taxRate
        );
    }

    /**
     * TaxTransactionResponse からフォームを生成.
     *
     * @param response 課税取引レスポンス
     * @return フォーム
     */
    public static TaxTransactionForm from(TaxTransactionResponse response) {
        TaxTransactionForm form = new TaxTransactionForm();
        form.setTaxCode(response.getTaxCode());
        form.setTaxName(response.getTaxName());
        form.setTaxRate(response.getTaxRate());
        return form;
    }
}
