package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateUnitPriceCommand;
import com.example.pms.application.port.in.command.UpdateUnitPriceCommand;
import com.example.pms.domain.model.unitprice.UnitPrice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 単価登録フォーム.
 */
@Data
public class UnitPriceForm {

    @NotBlank(message = "品目コードは必須です")
    @Size(max = 20, message = "品目コードは20文字以内で入力してください")
    private String itemCode;

    @NotBlank(message = "取引先コードは必須です")
    @Size(max = 20, message = "取引先コードは20文字以内で入力してください")
    private String supplierCode;

    @NotNull(message = "適用開始日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveTo;

    @NotNull(message = "単価は必須です")
    @Positive(message = "単価は正の数で入力してください")
    private BigDecimal price;

    @Size(max = 3, message = "通貨コードは3文字以内で入力してください")
    private String currencyCode;

    /**
     * フォームから登録コマンドを生成.
     *
     * @return 登録コマンド
     */
    public CreateUnitPriceCommand toCreateCommand() {
        return new CreateUnitPriceCommand(
            this.itemCode,
            this.supplierCode,
            this.effectiveFrom,
            this.effectiveTo != null ? this.effectiveTo : LocalDate.of(9999, 12, 31),
            this.price,
            this.currencyCode != null ? this.currencyCode : "JPY"
        );
    }

    /**
     * フォームから更新コマンドを生成.
     *
     * @return 更新コマンド
     */
    public UpdateUnitPriceCommand toUpdateCommand() {
        return new UpdateUnitPriceCommand(
            this.effectiveTo != null ? this.effectiveTo : LocalDate.of(9999, 12, 31),
            this.price,
            this.currencyCode != null ? this.currencyCode : "JPY"
        );
    }

    /**
     * フォームからエンティティを生成.
     *
     * @return 単価エンティティ
     * @deprecated Use {@link #toCreateCommand()} or {@link #toUpdateCommand()} instead
     */
    @Deprecated
    public UnitPrice toEntity() {
        return UnitPrice.builder()
            .itemCode(this.itemCode)
            .supplierCode(this.supplierCode)
            .effectiveFrom(this.effectiveFrom)
            .effectiveTo(this.effectiveTo != null ? this.effectiveTo : LocalDate.of(9999, 12, 31))
            .price(this.price)
            .currencyCode(this.currencyCode != null ? this.currencyCode : "JPY")
            .build();
    }
}
