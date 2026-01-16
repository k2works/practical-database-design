package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateItemCommand;
import com.example.pms.application.port.in.command.UpdateItemCommand;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 品目登録・編集フォーム.
 */
@Data
public class ItemForm {

    @NotBlank(message = "品目コードは必須です")
    @Size(max = 20, message = "品目コードは20文字以内で入力してください")
    private String itemCode;

    @NotBlank(message = "品名は必須です")
    @Size(max = 100, message = "品名は100文字以内で入力してください")
    private String itemName;

    @NotNull(message = "品目区分は必須です")
    private ItemCategory itemCategory;

    private String unitCode;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @PositiveOrZero(message = "リードタイムは0以上で入力してください")
    private Integer leadTime;

    @PositiveOrZero(message = "安全リードタイムは0以上で入力してください")
    private Integer safetyLeadTime;

    @PositiveOrZero(message = "安全在庫数は0以上で入力してください")
    private BigDecimal safetyStock;

    @Positive(message = "歩留率は正の数で入力してください")
    private BigDecimal yieldRate;

    @Positive(message = "最小ロット数は正の数で入力してください")
    private BigDecimal minLotSize;

    @Positive(message = "刻みロット数は正の数で入力してください")
    private BigDecimal lotIncrement;

    @Positive(message = "最大ロット数は正の数で入力してください")
    private BigDecimal maxLotSize;

    @PositiveOrZero(message = "有効期間は0以上で入力してください")
    private Integer shelfLife;

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateItemCommand toCreateCommand() {
        return new CreateItemCommand(
            this.itemCode,
            this.itemName,
            this.itemCategory,
            this.unitCode,
            this.effectiveFrom != null ? this.effectiveFrom : LocalDate.now(),
            this.effectiveTo,
            this.leadTime,
            this.safetyLeadTime,
            this.safetyStock,
            this.yieldRate,
            this.minLotSize,
            this.lotIncrement,
            this.maxLotSize,
            this.shelfLife
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateItemCommand toUpdateCommand() {
        return new UpdateItemCommand(
            this.itemName,
            this.itemCategory,
            this.unitCode,
            this.effectiveFrom,
            this.effectiveTo,
            this.leadTime,
            this.safetyLeadTime,
            this.safetyStock,
            this.yieldRate,
            this.minLotSize,
            this.lotIncrement,
            this.maxLotSize,
            this.shelfLife
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param item 品目エンティティ
     * @return フォーム
     */
    public static ItemForm from(Item item) {
        ItemForm form = new ItemForm();
        form.setItemCode(item.getItemCode());
        form.setItemName(item.getItemName());
        form.setItemCategory(item.getItemCategory());
        form.setUnitCode(item.getUnitCode());
        form.setEffectiveFrom(item.getEffectiveFrom());
        form.setEffectiveTo(item.getEffectiveTo());
        form.setLeadTime(item.getLeadTime());
        form.setSafetyLeadTime(item.getSafetyLeadTime());
        form.setSafetyStock(item.getSafetyStock());
        form.setYieldRate(item.getYieldRate());
        form.setMinLotSize(item.getMinLotSize());
        form.setLotIncrement(item.getLotIncrement());
        form.setMaxLotSize(item.getMaxLotSize());
        form.setShelfLife(item.getShelfLife());
        return form;
    }
}
