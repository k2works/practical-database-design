package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreatePurchaseOrderCommand;
import com.example.sms.application.port.in.command.CreatePurchaseOrderCommand.CreatePurchaseOrderDetailCommand;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
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
 * 発注登録・編集フォーム.
 */
@Data
public class PurchaseOrderForm {

    private String purchaseOrderNumber;

    @NotNull(message = "発注日は必須です")
    private LocalDate orderDate;

    private LocalDate desiredDeliveryDate;

    @NotBlank(message = "仕入先コードは必須です")
    private String supplierCode;

    private String supplierBranchNumber;

    private String purchaserCode;

    private BigDecimal totalAmount;

    private BigDecimal taxAmount;

    private PurchaseOrderStatus status;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    @Valid
    private List<PurchaseOrderDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public PurchaseOrderForm() {
        this.orderDate = LocalDate.now();
        this.desiredDeliveryDate = LocalDate.now().plusWeeks(1);
        this.supplierBranchNumber = "00";
        this.status = PurchaseOrderStatus.DRAFT;
        this.totalAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.details.add(new PurchaseOrderDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreatePurchaseOrderCommand toCreateCommand() {
        List<CreatePurchaseOrderDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreatePurchaseOrderDetailCommand(
                d.getProductCode(),
                d.getOrderQuantity(),
                d.getUnitPrice(),
                d.getExpectedDeliveryDate(),
                d.getRemarks()
            ))
            .toList();

        return new CreatePurchaseOrderCommand(
            this.supplierCode,
            this.supplierBranchNumber,
            this.orderDate,
            this.desiredDeliveryDate,
            this.purchaserCode,
            this.remarks,
            detailCommands
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param purchaseOrder 発注エンティティ
     * @return フォーム
     */
    public static PurchaseOrderForm from(PurchaseOrder purchaseOrder) {
        PurchaseOrderForm form = new PurchaseOrderForm();
        form.setPurchaseOrderNumber(purchaseOrder.getPurchaseOrderNumber());
        form.setOrderDate(purchaseOrder.getOrderDate());
        form.setDesiredDeliveryDate(purchaseOrder.getDesiredDeliveryDate());
        form.setSupplierCode(purchaseOrder.getSupplierCode());
        form.setSupplierBranchNumber(purchaseOrder.getSupplierBranchNumber());
        form.setPurchaserCode(purchaseOrder.getPurchaserCode());
        form.setTotalAmount(purchaseOrder.getTotalAmount());
        form.setTaxAmount(purchaseOrder.getTaxAmount());
        form.setStatus(purchaseOrder.getStatus());
        form.setRemarks(purchaseOrder.getRemarks());
        form.setVersion(purchaseOrder.getVersion());

        List<PurchaseOrderDetailForm> detailForms = new ArrayList<>();
        if (purchaseOrder.getDetails() != null) {
            for (PurchaseOrderDetail detail : purchaseOrder.getDetails()) {
                detailForms.add(PurchaseOrderDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new PurchaseOrderDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 発注明細フォーム.
     */
    @Data
    public static class PurchaseOrderDetailForm {

        private Integer id;

        private String productCode;

        @PositiveOrZero(message = "発注数量は0以上で入力してください")
        private BigDecimal orderQuantity;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        private BigDecimal orderAmount;

        private LocalDate expectedDeliveryDate;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        /**
         * デフォルトコンストラクタ.
         */
        public PurchaseOrderDetailForm() {
            this.orderQuantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.orderAmount = BigDecimal.ZERO;
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 発注明細エンティティ
         * @return フォーム
         */
        public static PurchaseOrderDetailForm from(PurchaseOrderDetail detail) {
            PurchaseOrderDetailForm form = new PurchaseOrderDetailForm();
            form.setId(detail.getId());
            form.setProductCode(detail.getProductCode());
            form.setOrderQuantity(detail.getOrderQuantity());
            form.setUnitPrice(detail.getUnitPrice());
            form.setOrderAmount(detail.getOrderAmount());
            form.setExpectedDeliveryDate(detail.getExpectedDeliveryDate());
            form.setRemarks(detail.getRemarks());
            return form;
        }
    }
}
