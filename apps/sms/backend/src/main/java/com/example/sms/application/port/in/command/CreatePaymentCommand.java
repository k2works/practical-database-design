package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 支払登録コマンド.
 */
public record CreatePaymentCommand(
    String supplierCode,
    LocalDate paymentClosingDate,
    LocalDate paymentDueDate,
    PaymentMethod paymentMethod,
    String bankCode,
    String branchCode,
    String accountType,
    String accountNumber,
    String accountName,
    String remarks,
    List<CreatePaymentDetailCommand> details
) {
    /**
     * 支払明細登録コマンド.
     */
    public record CreatePaymentDetailCommand(
        String purchaseNumber,
        LocalDate purchaseDate,
        BigDecimal purchaseAmount,
        BigDecimal taxAmount,
        BigDecimal paymentTargetAmount
    ) {
    }
}
