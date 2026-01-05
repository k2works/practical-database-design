package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 売上登録コマンド.
 */
public record CreateSalesCommand(
    LocalDate salesDate,
    Integer orderId,
    Integer shipmentId,
    String customerCode,
    String customerBranchNumber,
    String representativeCode,
    String remarks,
    List<CreateSalesDetailCommand> details
) {

    /**
     * 売上明細登録コマンド.
     */
    public record CreateSalesDetailCommand(
        Integer orderDetailId,
        Integer shipmentDetailId,
        String productCode,
        String productName,
        BigDecimal salesQuantity,
        String unit,
        BigDecimal unitPrice,
        String remarks
    ) {
    }
}
