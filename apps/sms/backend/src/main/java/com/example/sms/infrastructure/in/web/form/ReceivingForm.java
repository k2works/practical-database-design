package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateReceivingCommand;
import com.example.sms.application.port.in.command.CreateReceivingCommand.CreateReceivingDetailCommand;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingDetail;
import com.example.sms.domain.model.purchase.ReceivingStatus;
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
 * 入荷登録・編集フォーム.
 */
@Data
public class ReceivingForm {

    private String receivingNumber;

    private Integer purchaseOrderId;

    @NotNull(message = "入荷日は必須です")
    private LocalDate receivingDate;

    @NotBlank(message = "仕入先コードは必須です")
    private String supplierCode;

    private String supplierBranchNumber;

    private String receiverCode;

    private String warehouseCode;

    private ReceivingStatus status;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    @Valid
    private List<ReceivingDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public ReceivingForm() {
        this.receivingDate = LocalDate.now();
        this.supplierBranchNumber = "00";
        this.status = ReceivingStatus.WAITING;
        this.details.add(new ReceivingDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateReceivingCommand toCreateCommand() {
        List<CreateReceivingDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreateReceivingDetailCommand(
                d.getPurchaseOrderDetailId(),
                d.getProductCode(),
                d.getReceivingQuantity(),
                d.getUnitPrice(),
                d.getRemarks()
            ))
            .toList();

        return new CreateReceivingCommand(
            this.purchaseOrderId,
            this.supplierCode,
            this.supplierBranchNumber,
            this.receivingDate,
            this.receiverCode,
            this.warehouseCode,
            this.remarks,
            detailCommands
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param receiving 入荷エンティティ
     * @return フォーム
     */
    public static ReceivingForm from(Receiving receiving) {
        ReceivingForm form = new ReceivingForm();
        form.setReceivingNumber(receiving.getReceivingNumber());
        form.setPurchaseOrderId(receiving.getPurchaseOrderId());
        form.setReceivingDate(receiving.getReceivingDate());
        form.setSupplierCode(receiving.getSupplierCode());
        form.setSupplierBranchNumber(receiving.getSupplierBranchNumber());
        form.setReceiverCode(receiving.getReceiverCode());
        form.setWarehouseCode(receiving.getWarehouseCode());
        form.setStatus(receiving.getStatus());
        form.setRemarks(receiving.getRemarks());
        form.setVersion(receiving.getVersion());

        List<ReceivingDetailForm> detailForms = new ArrayList<>();
        if (receiving.getDetails() != null) {
            for (ReceivingDetail detail : receiving.getDetails()) {
                detailForms.add(ReceivingDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new ReceivingDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 入荷明細フォーム.
     */
    @Data
    public static class ReceivingDetailForm {

        private Integer id;

        private Integer purchaseOrderDetailId;

        private String productCode;

        @PositiveOrZero(message = "入荷数量は0以上で入力してください")
        private BigDecimal receivingQuantity;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        private BigDecimal amount;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        /**
         * デフォルトコンストラクタ.
         */
        public ReceivingDetailForm() {
            this.receivingQuantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.amount = BigDecimal.ZERO;
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 入荷明細エンティティ
         * @return フォーム
         */
        public static ReceivingDetailForm from(ReceivingDetail detail) {
            ReceivingDetailForm form = new ReceivingDetailForm();
            form.setId(detail.getId());
            form.setPurchaseOrderDetailId(detail.getPurchaseOrderDetailId());
            form.setProductCode(detail.getProductCode());
            form.setReceivingQuantity(detail.getReceivingQuantity());
            form.setUnitPrice(detail.getUnitPrice());
            form.setAmount(detail.getAmount());
            form.setRemarks(detail.getRemarks());
            return form;
        }
    }
}
