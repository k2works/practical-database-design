package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateOrderCommand;
import com.example.sms.application.port.in.command.CreateOrderCommand.CreateOrderDetailCommand;
import com.example.sms.application.port.in.command.UpdateOrderCommand;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
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
 * 受注登録・編集フォーム.
 */
@Data
public class OrderForm {

    private String orderNumber;

    @NotNull(message = "受注日は必須です")
    private LocalDate orderDate;

    @NotBlank(message = "顧客コードは必須です")
    private String customerCode;

    private String customerBranchNumber;

    private String shippingDestinationNumber;

    private String representativeCode;

    private LocalDate requestedDeliveryDate;

    private LocalDate scheduledShippingDate;

    private Integer quotationId;

    @Size(max = 50, message = "顧客注文番号は50文字以内で入力してください")
    private String customerOrderNumber;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    @Valid
    private List<OrderDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public OrderForm() {
        this.orderDate = LocalDate.now();
        this.customerBranchNumber = "00";
        this.details.add(new OrderDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateOrderCommand toCreateCommand() {
        List<CreateOrderDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreateOrderDetailCommand(
                d.getProductCode(),
                d.getProductName(),
                d.getOrderQuantity(),
                d.getUnit(),
                d.getUnitPrice(),
                d.getWarehouseCode(),
                d.getRequestedDeliveryDate(),
                d.getRemarks()
            ))
            .toList();

        return new CreateOrderCommand(
            this.orderDate,
            this.customerCode,
            this.customerBranchNumber,
            this.shippingDestinationNumber,
            this.representativeCode,
            this.requestedDeliveryDate,
            this.scheduledShippingDate,
            this.quotationId,
            this.customerOrderNumber,
            this.remarks,
            detailCommands
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateOrderCommand toUpdateCommand() {
        return new UpdateOrderCommand(
            this.shippingDestinationNumber,
            this.representativeCode,
            this.requestedDeliveryDate,
            this.scheduledShippingDate,
            null,
            this.customerOrderNumber,
            this.remarks,
            this.version
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param order 受注エンティティ
     * @return フォーム
     */
    public static OrderForm from(SalesOrder order) {
        OrderForm form = new OrderForm();
        form.setOrderNumber(order.getOrderNumber());
        form.setOrderDate(order.getOrderDate());
        form.setCustomerCode(order.getCustomerCode());
        form.setCustomerBranchNumber(order.getCustomerBranchNumber());
        form.setShippingDestinationNumber(order.getShippingDestinationNumber());
        form.setRepresentativeCode(order.getRepresentativeCode());
        form.setRequestedDeliveryDate(order.getRequestedDeliveryDate());
        form.setScheduledShippingDate(order.getScheduledShippingDate());
        form.setQuotationId(order.getQuotationId());
        form.setCustomerOrderNumber(order.getCustomerOrderNumber());
        form.setRemarks(order.getRemarks());
        form.setVersion(order.getVersion());

        List<OrderDetailForm> detailForms = new ArrayList<>();
        if (order.getDetails() != null) {
            for (SalesOrderDetail detail : order.getDetails()) {
                detailForms.add(OrderDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new OrderDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 受注明細フォーム.
     */
    @Data
    public static class OrderDetailForm {

        private Integer id;

        private String productCode;

        private String productName;

        @PositiveOrZero(message = "数量は0以上で入力してください")
        private BigDecimal orderQuantity;

        private String unit;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        private String warehouseCode;

        private LocalDate requestedDeliveryDate;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        private Integer version;

        /**
         * デフォルトコンストラクタ.
         */
        public OrderDetailForm() {
            this.orderQuantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.unit = "個";
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 受注明細エンティティ
         * @return フォーム
         */
        public static OrderDetailForm from(SalesOrderDetail detail) {
            OrderDetailForm form = new OrderDetailForm();
            form.setId(detail.getId());
            form.setProductCode(detail.getProductCode());
            form.setProductName(detail.getProductName());
            form.setOrderQuantity(detail.getOrderQuantity());
            form.setUnit(detail.getUnit());
            form.setUnitPrice(detail.getUnitPrice());
            form.setWarehouseCode(detail.getWarehouseCode());
            form.setRequestedDeliveryDate(detail.getRequestedDeliveryDate());
            form.setRemarks(detail.getRemarks());
            form.setVersion(detail.getVersion());
            return form;
        }
    }
}
