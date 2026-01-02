package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 出荷登録コマンド.
 */
public record CreateShipmentCommand(
    LocalDate shipmentDate,
    Integer orderId,
    String customerCode,
    String customerBranchNumber,
    String shippingDestinationNumber,
    String shippingDestinationName,
    String shippingDestinationPostalCode,
    String shippingDestinationAddress1,
    String shippingDestinationAddress2,
    String representativeCode,
    String warehouseCode,
    String remarks,
    List<CreateShipmentDetailCommand> details
) {

    /**
     * 出荷明細登録コマンド.
     */
    public record CreateShipmentDetailCommand(
        Integer orderDetailId,
        String productCode,
        String productName,
        BigDecimal shippedQuantity,
        String unit,
        BigDecimal unitPrice,
        String warehouseCode,
        String remarks
    ) {
    }
}
