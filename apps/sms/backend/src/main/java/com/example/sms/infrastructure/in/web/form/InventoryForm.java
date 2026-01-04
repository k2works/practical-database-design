package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateInventoryCommand;
import com.example.sms.domain.model.inventory.Inventory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 在庫登録・編集フォーム.
 */
@Data
public class InventoryForm {

    private Integer id;

    @NotBlank(message = "倉庫コードは必須です")
    private String warehouseCode;

    @NotBlank(message = "商品コードは必須です")
    private String productCode;

    @Size(max = 20, message = "ロケーションコードは20文字以内で入力してください")
    private String locationCode;

    @PositiveOrZero(message = "現在庫数は0以上で入力してください")
    private BigDecimal currentQuantity;

    @PositiveOrZero(message = "引当数は0以上で入力してください")
    private BigDecimal allocatedQuantity;

    @PositiveOrZero(message = "発注残数は0以上で入力してください")
    private BigDecimal orderedQuantity;

    @Size(max = 50, message = "ロット番号は50文字以内で入力してください")
    private String lotNumber;

    @Size(max = 50, message = "シリアル番号は50文字以内で入力してください")
    private String serialNumber;

    private LocalDate expirationDate;

    private Integer version;

    /**
     * デフォルトコンストラクタ.
     */
    public InventoryForm() {
        this.currentQuantity = BigDecimal.ZERO;
        this.allocatedQuantity = BigDecimal.ZERO;
        this.orderedQuantity = BigDecimal.ZERO;
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateInventoryCommand toCreateCommand() {
        return new CreateInventoryCommand(
            this.warehouseCode,
            this.productCode,
            this.locationCode,
            this.currentQuantity,
            this.allocatedQuantity,
            this.orderedQuantity,
            this.lotNumber,
            this.serialNumber,
            this.expirationDate
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param inventory 在庫エンティティ
     * @return フォーム
     */
    public static InventoryForm from(Inventory inventory) {
        InventoryForm form = new InventoryForm();
        form.setId(inventory.getId());
        form.setWarehouseCode(inventory.getWarehouseCode());
        form.setProductCode(inventory.getProductCode());
        form.setLocationCode(inventory.getLocationCode());
        form.setCurrentQuantity(inventory.getCurrentQuantity());
        form.setAllocatedQuantity(inventory.getAllocatedQuantity());
        form.setOrderedQuantity(inventory.getOrderedQuantity());
        form.setLotNumber(inventory.getLotNumber());
        form.setSerialNumber(inventory.getSerialNumber());
        form.setExpirationDate(inventory.getExpirationDate());
        form.setVersion(inventory.getVersion());
        return form;
    }
}
