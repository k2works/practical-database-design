package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入荷受入フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingForm {

    private Integer id;

    private String receivingNumber;

    @NotBlank(message = "発注番号は必須です")
    private String purchaseOrderNumber;

    @NotNull(message = "発注行番号は必須です")
    private Integer lineNumber;

    @NotNull(message = "入荷日は必須です")
    private LocalDate receivingDate;

    private String receiverCode;

    @NotNull(message = "入荷種別は必須です")
    private ReceivingType receivingType;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @Builder.Default
    private Boolean miscellaneousItemFlag = false;

    @NotNull(message = "入荷数量は必須です")
    @Positive(message = "入荷数量は正の数である必要があります")
    private BigDecimal receivingQuantity;

    private String remarks;

    private Integer version;

    /**
     * フォームをエンティティに変換する.
     *
     * @return Receiving エンティティ
     */
    public Receiving toEntity() {
        return Receiving.builder()
            .id(this.id)
            .receivingNumber(this.receivingNumber)
            .purchaseOrderNumber(this.purchaseOrderNumber)
            .lineNumber(this.lineNumber)
            .receivingDate(this.receivingDate)
            .receiverCode(this.receiverCode)
            .receivingType(this.receivingType)
            .itemCode(this.itemCode)
            .miscellaneousItemFlag(this.miscellaneousItemFlag)
            .receivingQuantity(this.receivingQuantity)
            .remarks(this.remarks)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param receiving Receiving エンティティ
     * @return ReceivingForm
     */
    public static ReceivingForm fromEntity(Receiving receiving) {
        return ReceivingForm.builder()
            .id(receiving.getId())
            .receivingNumber(receiving.getReceivingNumber())
            .purchaseOrderNumber(receiving.getPurchaseOrderNumber())
            .lineNumber(receiving.getLineNumber())
            .receivingDate(receiving.getReceivingDate())
            .receiverCode(receiving.getReceiverCode())
            .receivingType(receiving.getReceivingType())
            .itemCode(receiving.getItemCode())
            .miscellaneousItemFlag(receiving.getMiscellaneousItemFlag())
            .receivingQuantity(receiving.getReceivingQuantity())
            .remarks(receiving.getRemarks())
            .version(receiving.getVersion())
            .build();
    }
}
