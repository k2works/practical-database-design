package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 発注登録コマンド.
 */
public record CreatePurchaseOrderCommand(
    String supplierCode,
    String supplierBranchNumber,
    LocalDate orderDate,
    LocalDate desiredDeliveryDate,
    String purchaserCode,
    String remarks,
    List<CreatePurchaseOrderDetailCommand> details
) {
    /**
     * 発注明細登録コマンド.
     */
    public record CreatePurchaseOrderDetailCommand(
        String productCode,
        BigDecimal orderQuantity,
        BigDecimal unitPrice,
        LocalDate expectedDeliveryDate,
        String remarks
    ) {
    }
}
