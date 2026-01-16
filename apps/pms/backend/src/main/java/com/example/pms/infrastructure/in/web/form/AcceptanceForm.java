package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.purchase.Acceptance;
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
 * 検収フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptanceForm {

    private Integer id;

    private String acceptanceNumber;

    @NotBlank(message = "受入検査番号は必須です")
    private String inspectionNumber;

    @NotBlank(message = "発注番号は必須です")
    private String purchaseOrderNumber;

    @NotNull(message = "発注行番号は必須です")
    private Integer lineNumber;

    @NotNull(message = "検収日は必須です")
    private LocalDate acceptanceDate;

    private String acceptorCode;

    @NotBlank(message = "取引先コードは必須です")
    private String supplierCode;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @Builder.Default
    private Boolean miscellaneousItemFlag = false;

    @NotNull(message = "検収数は必須です")
    @Positive(message = "検収数は正の数である必要があります")
    private BigDecimal acceptedQuantity;

    @NotNull(message = "検収単価は必須です")
    private BigDecimal unitPrice;

    @NotNull(message = "検収金額は必須です")
    private BigDecimal amount;

    private BigDecimal taxAmount;

    private String remarks;

    private Integer version;

    /**
     * フォームをエンティティに変換する.
     *
     * @return Acceptance エンティティ
     */
    public Acceptance toEntity() {
        return Acceptance.builder()
            .id(this.id)
            .acceptanceNumber(this.acceptanceNumber)
            .inspectionNumber(this.inspectionNumber)
            .purchaseOrderNumber(this.purchaseOrderNumber)
            .lineNumber(this.lineNumber)
            .acceptanceDate(this.acceptanceDate)
            .acceptorCode(this.acceptorCode)
            .supplierCode(this.supplierCode)
            .itemCode(this.itemCode)
            .miscellaneousItemFlag(this.miscellaneousItemFlag)
            .acceptedQuantity(this.acceptedQuantity)
            .unitPrice(this.unitPrice)
            .amount(this.amount)
            .taxAmount(this.taxAmount)
            .remarks(this.remarks)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param acceptance Acceptance エンティティ
     * @return AcceptanceForm
     */
    public static AcceptanceForm fromEntity(Acceptance acceptance) {
        return AcceptanceForm.builder()
            .id(acceptance.getId())
            .acceptanceNumber(acceptance.getAcceptanceNumber())
            .inspectionNumber(acceptance.getInspectionNumber())
            .purchaseOrderNumber(acceptance.getPurchaseOrderNumber())
            .lineNumber(acceptance.getLineNumber())
            .acceptanceDate(acceptance.getAcceptanceDate())
            .acceptorCode(acceptance.getAcceptorCode())
            .supplierCode(acceptance.getSupplierCode())
            .itemCode(acceptance.getItemCode())
            .miscellaneousItemFlag(acceptance.getMiscellaneousItemFlag())
            .acceptedQuantity(acceptance.getAcceptedQuantity())
            .unitPrice(acceptance.getUnitPrice())
            .amount(acceptance.getAmount())
            .taxAmount(acceptance.getTaxAmount())
            .remarks(acceptance.getRemarks())
            .version(acceptance.getVersion())
            .build();
    }
}
