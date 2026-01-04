package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreatePurchaseCommand;
import com.example.sms.application.port.in.command.CreatePurchaseCommand.CreatePurchaseDetailCommand;
import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.domain.model.purchase.PurchaseDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 仕入登録・編集フォーム.
 */
@Data
public class PurchaseForm {

    private String purchaseNumber;

    private Integer receivingId;

    @NotNull(message = "仕入日は必須です")
    private LocalDate purchaseDate;

    @NotBlank(message = "仕入先コードは必須です")
    private String supplierCode;

    private String supplierBranchNumber;

    private BigDecimal totalAmount;

    private BigDecimal taxAmount;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    @Valid
    private List<PurchaseDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public PurchaseForm() {
        this.purchaseDate = LocalDate.now();
        this.supplierBranchNumber = "00";
        this.totalAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.details.add(new PurchaseDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreatePurchaseCommand toCreateCommand() {
        List<CreatePurchaseDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreatePurchaseDetailCommand(
                d.getProductCode(),
                d.getPurchaseQuantity(),
                d.getUnitPrice(),
                d.getRemarks()
            ))
            .toList();

        return new CreatePurchaseCommand(
            this.receivingId,
            this.supplierCode,
            this.supplierBranchNumber,
            this.purchaseDate,
            this.remarks,
            detailCommands
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param purchase 仕入エンティティ
     * @return フォーム
     */
    public static PurchaseForm from(Purchase purchase) {
        PurchaseForm form = new PurchaseForm();
        form.setPurchaseNumber(purchase.getPurchaseNumber());
        form.setReceivingId(purchase.getReceivingId());
        form.setPurchaseDate(purchase.getPurchaseDate());
        form.setSupplierCode(purchase.getSupplierCode());
        form.setSupplierBranchNumber(purchase.getSupplierBranchNumber());
        form.setTotalAmount(purchase.getTotalAmount());
        form.setTaxAmount(purchase.getTaxAmount());
        form.setRemarks(purchase.getRemarks());
        form.setVersion(purchase.getVersion());

        List<PurchaseDetailForm> detailForms = new ArrayList<>();
        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                detailForms.add(PurchaseDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new PurchaseDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 仕入明細フォーム.
     */
    @Data
    public static class PurchaseDetailForm {

        private Integer id;

        private String productCode;

        @PositiveOrZero(message = "仕入数量は0以上で入力してください")
        private BigDecimal purchaseQuantity;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        private BigDecimal purchaseAmount;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        /**
         * デフォルトコンストラクタ.
         */
        public PurchaseDetailForm() {
            this.purchaseQuantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.purchaseAmount = BigDecimal.ZERO;
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 仕入明細エンティティ
         * @return フォーム
         */
        public static PurchaseDetailForm from(PurchaseDetail detail) {
            PurchaseDetailForm form = new PurchaseDetailForm();
            form.setId(detail.getId());
            form.setProductCode(detail.getProductCode());
            form.setPurchaseQuantity(detail.getPurchaseQuantity());
            form.setUnitPrice(detail.getUnitPrice());
            form.setPurchaseAmount(detail.getPurchaseAmount());
            form.setRemarks(detail.getRemarks());
            return form;
        }
    }
}
