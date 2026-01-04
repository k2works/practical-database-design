package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 入荷登録コマンド.
 */
public record CreateReceivingCommand(
    Integer purchaseOrderId,
    String supplierCode,
    String supplierBranchNumber,
    LocalDate receivingDate,
    String receiverCode,
    String warehouseCode,
    String remarks,
    List<CreateReceivingDetailCommand> details
) {
    /**
     * 入荷明細登録コマンド.
     */
    public record CreateReceivingDetailCommand(
        Integer purchaseOrderDetailId,
        String productCode,
        BigDecimal receivingQuantity,
        BigDecimal unitPrice,
        String remarks
    ) {
    }
}
