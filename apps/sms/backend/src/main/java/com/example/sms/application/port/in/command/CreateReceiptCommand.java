package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.receipt.ReceiptMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入金登録コマンド.
 */
public record CreateReceiptCommand(
    LocalDate receiptDate,
    String customerCode,
    String customerBranchNumber,
    ReceiptMethod receiptMethod,
    BigDecimal receiptAmount,
    BigDecimal bankFee,
    String payerName,
    String bankName,
    String accountNumber,
    String remarks
) {
}
