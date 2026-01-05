package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateSalesCommand;
import com.example.sms.application.port.in.command.CreateSalesCommand.CreateSalesDetailCommand;
import com.example.sms.application.port.in.command.UpdateSalesCommand;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesDetail;
import com.example.sms.domain.model.sales.SalesStatus;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentDetail;
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
 * 売上登録・編集フォーム.
 */
@Data
public class SalesForm {

    private String salesNumber;

    @NotNull(message = "売上日は必須です")
    private LocalDate salesDate;

    private Integer orderId;

    private Integer shipmentId;

    @NotBlank(message = "顧客コードは必須です")
    private String customerCode;

    private String customerBranchNumber;

    private String representativeCode;

    private SalesStatus status;

    private Integer billingId;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    @Valid
    private List<SalesDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public SalesForm() {
        this.salesDate = LocalDate.now();
        this.customerBranchNumber = "00";
        this.status = SalesStatus.RECORDED;
        this.details.add(new SalesDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateSalesCommand toCreateCommand() {
        List<CreateSalesDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreateSalesDetailCommand(
                d.getOrderDetailId(),
                d.getShipmentDetailId(),
                d.getProductCode(),
                d.getProductName(),
                d.getSalesQuantity(),
                d.getUnit(),
                d.getUnitPrice(),
                d.getRemarks()
            ))
            .toList();

        return new CreateSalesCommand(
            this.salesDate,
            this.orderId,
            this.shipmentId,
            this.customerCode,
            this.customerBranchNumber,
            this.representativeCode,
            this.remarks,
            detailCommands
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateSalesCommand toUpdateCommand() {
        return new UpdateSalesCommand(
            this.status,
            this.billingId,
            this.remarks
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param sales 売上エンティティ
     * @return フォーム
     */
    public static SalesForm from(Sales sales) {
        SalesForm form = new SalesForm();
        form.setSalesNumber(sales.getSalesNumber());
        form.setSalesDate(sales.getSalesDate());
        form.setOrderId(sales.getOrderId());
        form.setShipmentId(sales.getShipmentId());
        form.setCustomerCode(sales.getCustomerCode());
        form.setCustomerBranchNumber(sales.getCustomerBranchNumber());
        form.setRepresentativeCode(sales.getRepresentativeCode());
        form.setStatus(sales.getStatus());
        form.setBillingId(sales.getBillingId());
        form.setRemarks(sales.getRemarks());

        List<SalesDetailForm> detailForms = new ArrayList<>();
        if (sales.getDetails() != null) {
            for (SalesDetail detail : sales.getDetails()) {
                detailForms.add(SalesDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new SalesDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 出荷エンティティからフォームを生成.
     *
     * @param shipment 出荷エンティティ
     * @return フォーム
     */
    public static SalesForm fromShipment(Shipment shipment) {
        SalesForm form = new SalesForm();
        form.setSalesDate(LocalDate.now());
        form.setOrderId(shipment.getOrderId());
        form.setShipmentId(shipment.getId());
        form.setCustomerCode(shipment.getCustomerCode());
        form.setCustomerBranchNumber(shipment.getCustomerBranchNumber());
        form.setRepresentativeCode(shipment.getRepresentativeCode());
        form.setStatus(SalesStatus.RECORDED);
        form.setRemarks(shipment.getRemarks());

        List<SalesDetailForm> detailForms = new ArrayList<>();
        if (shipment.getDetails() != null) {
            for (ShipmentDetail detail : shipment.getDetails()) {
                detailForms.add(SalesDetailForm.fromShipmentDetail(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new SalesDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 売上明細フォーム.
     */
    @Data
    public static class SalesDetailForm {

        private Integer id;

        private Integer orderDetailId;

        private Integer shipmentDetailId;

        private String productCode;

        private String productName;

        @PositiveOrZero(message = "売上数量は0以上で入力してください")
        private BigDecimal salesQuantity;

        private String unit;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        /**
         * デフォルトコンストラクタ.
         */
        public SalesDetailForm() {
            this.salesQuantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.unit = "個";
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 売上明細エンティティ
         * @return フォーム
         */
        public static SalesDetailForm from(SalesDetail detail) {
            SalesDetailForm form = new SalesDetailForm();
            form.setId(detail.getId());
            form.setOrderDetailId(detail.getOrderDetailId());
            form.setShipmentDetailId(detail.getShipmentDetailId());
            form.setProductCode(detail.getProductCode());
            form.setProductName(detail.getProductName());
            form.setSalesQuantity(detail.getSalesQuantity());
            form.setUnit(detail.getUnit());
            form.setUnitPrice(detail.getUnitPrice());
            form.setRemarks(detail.getRemarks());
            return form;
        }

        /**
         * 出荷明細エンティティからフォームを生成.
         *
         * @param detail 出荷明細エンティティ
         * @return フォーム
         */
        public static SalesDetailForm fromShipmentDetail(ShipmentDetail detail) {
            SalesDetailForm form = new SalesDetailForm();
            form.setShipmentDetailId(detail.getId());
            form.setOrderDetailId(detail.getOrderDetailId());
            form.setProductCode(detail.getProductCode());
            form.setProductName(detail.getProductName());
            form.setSalesQuantity(detail.getShippedQuantity());
            form.setUnit(detail.getUnit());
            form.setUnitPrice(detail.getUnitPrice());
            form.setRemarks(detail.getRemarks());
            return form;
        }
    }
}
