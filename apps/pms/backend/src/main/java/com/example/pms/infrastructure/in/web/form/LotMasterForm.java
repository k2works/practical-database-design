package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateLotMasterCommand;
import com.example.pms.application.port.in.command.UpdateLotMasterCommand;
import com.example.pms.domain.model.quality.LotMaster;
import com.example.pms.domain.model.quality.LotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ロットマスタフォーム.
 */
@Data
public class LotMasterForm {

    @NotBlank(message = "ロット番号は必須です")
    private String lotNumber;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "ロット種別は必須です")
    private LotType lotType;

    @NotNull(message = "製造日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @NotNull(message = "数量は必須です")
    private BigDecimal quantity;

    private String warehouseCode;

    private String remarks;

    private Integer version;

    /**
     * フォームから登録コマンドを生成.
     *
     * @return 登録コマンド
     */
    public CreateLotMasterCommand toCreateCommand() {
        return new CreateLotMasterCommand(
                lotNumber,
                itemCode,
                lotType,
                manufactureDate,
                expirationDate,
                quantity,
                warehouseCode,
                remarks
        );
    }

    /**
     * フォームから更新コマンドを生成.
     *
     * @return 更新コマンド
     */
    public UpdateLotMasterCommand toUpdateCommand() {
        return new UpdateLotMasterCommand(
                itemCode,
                lotType,
                manufactureDate,
                expirationDate,
                quantity,
                warehouseCode,
                remarks
        );
    }

    /**
     * フォームからエンティティに変換する.
     *
     * @return LotMaster
     * @deprecated Use {@link #toCreateCommand()} or {@link #toUpdateCommand()} instead
     */
    @Deprecated
    public LotMaster toEntity() {
        return LotMaster.builder()
                .lotNumber(lotNumber)
                .itemCode(itemCode)
                .lotType(lotType)
                .manufactureDate(manufactureDate)
                .expirationDate(expirationDate)
                .quantity(quantity)
                .warehouseCode(warehouseCode)
                .remarks(remarks)
                .version(version != null ? version : 1)
                .build();
    }

    /**
     * エンティティからフォームに変換する.
     *
     * @param lotMaster ロットマスタ
     * @return フォーム
     */
    public static LotMasterForm fromEntity(LotMaster lotMaster) {
        LotMasterForm form = new LotMasterForm();
        form.setLotNumber(lotMaster.getLotNumber());
        form.setItemCode(lotMaster.getItemCode());
        form.setLotType(lotMaster.getLotType());
        form.setManufactureDate(lotMaster.getManufactureDate());
        form.setExpirationDate(lotMaster.getExpirationDate());
        form.setQuantity(lotMaster.getQuantity());
        form.setWarehouseCode(lotMaster.getWarehouseCode());
        form.setRemarks(lotMaster.getRemarks());
        form.setVersion(lotMaster.getVersion());
        return form;
    }
}
