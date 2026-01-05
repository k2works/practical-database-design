package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 仕入登録コマンド.
 */
public record CreatePurchaseCommand(
    Integer receivingId,
    String supplierCode,
    String supplierBranchNumber,
    LocalDate purchaseDate,
    String remarks,
    List<CreatePurchaseDetailCommand> details
) {
    /**
     * 仕入明細登録コマンド.
     */
    public record CreatePurchaseDetailCommand(
        String productCode,
        BigDecimal purchaseQuantity,
        BigDecimal unitPrice,
        String remarks
    ) {
    }
}
