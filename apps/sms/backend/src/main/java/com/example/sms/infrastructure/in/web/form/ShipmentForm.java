package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateShipmentCommand;
import com.example.sms.application.port.in.command.CreateShipmentCommand.CreateShipmentDetailCommand;
import com.example.sms.application.port.in.command.UpdateShipmentCommand;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentDetail;
import com.example.sms.domain.model.shipping.ShipmentStatus;
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
 * 出荷登録・編集フォーム.
 */
@Data
public class ShipmentForm {

    private String shipmentNumber;

    @NotNull(message = "出荷日は必須です")
    private LocalDate shipmentDate;

    private Integer orderId;

    @NotBlank(message = "顧客コードは必須です")
    private String customerCode;

    private String customerBranchNumber;

    private String shippingDestinationNumber;

    private String shippingDestinationName;

    private String shippingDestinationPostalCode;

    private String shippingDestinationAddress1;

    private String shippingDestinationAddress2;

    private String representativeCode;

    private String warehouseCode;

    private ShipmentStatus status;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    @Valid
    private List<ShipmentDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public ShipmentForm() {
        this.shipmentDate = LocalDate.now();
        this.customerBranchNumber = "00";
        this.status = ShipmentStatus.INSTRUCTED;
        this.details.add(new ShipmentDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateShipmentCommand toCreateCommand() {
        List<CreateShipmentDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreateShipmentDetailCommand(
                d.getOrderDetailId(),
                d.getProductCode(),
                d.getProductName(),
                d.getShippedQuantity(),
                d.getUnit(),
                d.getUnitPrice(),
                d.getWarehouseCode(),
                d.getRemarks()
            ))
            .toList();

        return new CreateShipmentCommand(
            this.shipmentDate,
            this.orderId,
            this.customerCode,
            this.customerBranchNumber,
            this.shippingDestinationNumber,
            this.shippingDestinationName,
            this.shippingDestinationPostalCode,
            this.shippingDestinationAddress1,
            this.shippingDestinationAddress2,
            this.representativeCode,
            this.warehouseCode,
            this.remarks,
            detailCommands
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateShipmentCommand toUpdateCommand() {
        return new UpdateShipmentCommand(
            this.status,
            this.remarks
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param shipment 出荷エンティティ
     * @return フォーム
     */
    public static ShipmentForm from(Shipment shipment) {
        ShipmentForm form = new ShipmentForm();
        form.setShipmentNumber(shipment.getShipmentNumber());
        form.setShipmentDate(shipment.getShipmentDate());
        form.setOrderId(shipment.getOrderId());
        form.setCustomerCode(shipment.getCustomerCode());
        form.setCustomerBranchNumber(shipment.getCustomerBranchNumber());
        form.setShippingDestinationNumber(shipment.getShippingDestinationNumber());
        form.setShippingDestinationName(shipment.getShippingDestinationName());
        form.setShippingDestinationPostalCode(shipment.getShippingDestinationPostalCode());
        form.setShippingDestinationAddress1(shipment.getShippingDestinationAddress1());
        form.setShippingDestinationAddress2(shipment.getShippingDestinationAddress2());
        form.setRepresentativeCode(shipment.getRepresentativeCode());
        form.setWarehouseCode(shipment.getWarehouseCode());
        form.setStatus(shipment.getStatus());
        form.setRemarks(shipment.getRemarks());

        List<ShipmentDetailForm> detailForms = new ArrayList<>();
        if (shipment.getDetails() != null) {
            for (ShipmentDetail detail : shipment.getDetails()) {
                detailForms.add(ShipmentDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new ShipmentDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 受注エンティティからフォームを生成.
     *
     * @param order 受注エンティティ
     * @return フォーム
     */
    public static ShipmentForm fromOrder(SalesOrder order) {
        ShipmentForm form = new ShipmentForm();
        form.setShipmentDate(LocalDate.now());
        form.setOrderId(order.getId());
        form.setCustomerCode(order.getCustomerCode());
        form.setCustomerBranchNumber(order.getCustomerBranchNumber());
        form.setShippingDestinationNumber(order.getShippingDestinationNumber());
        form.setRepresentativeCode(order.getRepresentativeCode());
        form.setStatus(ShipmentStatus.INSTRUCTED);
        form.setRemarks(order.getRemarks());

        List<ShipmentDetailForm> detailForms = new ArrayList<>();
        if (order.getDetails() != null) {
            for (SalesOrderDetail detail : order.getDetails()) {
                detailForms.add(ShipmentDetailForm.fromOrderDetail(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new ShipmentDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 出荷明細フォーム.
     */
    @Data
    public static class ShipmentDetailForm {

        private Integer id;

        private Integer orderDetailId;

        private String productCode;

        private String productName;

        @PositiveOrZero(message = "出荷数量は0以上で入力してください")
        private BigDecimal shippedQuantity;

        private String unit;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        private String warehouseCode;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        /**
         * デフォルトコンストラクタ.
         */
        public ShipmentDetailForm() {
            this.shippedQuantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.unit = "個";
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 出荷明細エンティティ
         * @return フォーム
         */
        public static ShipmentDetailForm from(ShipmentDetail detail) {
            ShipmentDetailForm form = new ShipmentDetailForm();
            form.setId(detail.getId());
            form.setOrderDetailId(detail.getOrderDetailId());
            form.setProductCode(detail.getProductCode());
            form.setProductName(detail.getProductName());
            form.setShippedQuantity(detail.getShippedQuantity());
            form.setUnit(detail.getUnit());
            form.setUnitPrice(detail.getUnitPrice());
            form.setWarehouseCode(detail.getWarehouseCode());
            form.setRemarks(detail.getRemarks());
            return form;
        }

        /**
         * 受注明細エンティティからフォームを生成.
         *
         * @param detail 受注明細エンティティ
         * @return フォーム
         */
        public static ShipmentDetailForm fromOrderDetail(SalesOrderDetail detail) {
            ShipmentDetailForm form = new ShipmentDetailForm();
            form.setOrderDetailId(detail.getId());
            form.setProductCode(detail.getProductCode());
            form.setProductName(detail.getProductName());
            // 出荷可能数量 = 受注数量 - 出荷済み数量
            BigDecimal remainingQuantity = detail.getOrderQuantity()
                .subtract(detail.getShippedQuantity() != null ? detail.getShippedQuantity() : BigDecimal.ZERO);
            form.setShippedQuantity(remainingQuantity);
            form.setUnit(detail.getUnit());
            form.setUnitPrice(detail.getUnitPrice());
            form.setWarehouseCode(detail.getWarehouseCode());
            form.setRemarks(detail.getRemarks());
            return form;
        }
    }
}
