package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 受注登録コマンド.
 */
public record CreateOrderCommand(
    LocalDate orderDate,
    String customerCode,
    String customerBranchNumber,
    String shippingDestinationNumber,
    String representativeCode,
    LocalDate requestedDeliveryDate,
    LocalDate scheduledShippingDate,
    Integer quotationId,
    String customerOrderNumber,
    String remarks,
    List<CreateOrderDetailCommand> details
) {

    /**
     * 受注明細登録コマンド.
     */
    public record CreateOrderDetailCommand(
        String productCode,
        String productName,
        BigDecimal orderQuantity,
        String unit,
        BigDecimal unitPrice,
        String warehouseCode,
        LocalDate requestedDeliveryDate,
        String remarks
    ) {
    }
}
