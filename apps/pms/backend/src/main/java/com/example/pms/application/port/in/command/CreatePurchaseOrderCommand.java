package com.example.pms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 発注登録コマンド.
 */
public record CreatePurchaseOrderCommand(
    String supplierCode,
    String ordererCode,
    String departmentCode,
    String remarks,
    List<PurchaseOrderDetailCommand> details
) {
    /**
     * 発注明細登録コマンド.
     */
    public record PurchaseOrderDetailCommand(
        String itemCode,
        String deliveryLocationCode,
        LocalDate expectedReceivingDate,
        BigDecimal orderQuantity,
        BigDecimal orderUnitPrice,
        String detailRemarks
    ) {
    }
}
