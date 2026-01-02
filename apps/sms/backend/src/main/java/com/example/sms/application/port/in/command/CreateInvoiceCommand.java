package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求登録コマンド.
 */
public record CreateInvoiceCommand(
    LocalDate invoiceDate,
    String customerCode,
    String customerBranchNumber,
    LocalDate closingDate,
    BigDecimal previousBalance,
    BigDecimal receiptAmount,
    BigDecimal currentSalesAmount,
    BigDecimal currentTaxAmount,
    LocalDate dueDate,
    String remarks
) {
}
