package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptMethod;
import com.example.sms.domain.model.receipt.ReceiptStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入金レスポンス DTO.
 */
public record ReceiptResponse(
    Integer id,
    String receiptNumber,
    LocalDate receiptDate,
    String customerCode,
    String customerBranchNumber,
    ReceiptMethod receiptMethod,
    BigDecimal receiptAmount,
    BigDecimal appliedAmount,
    BigDecimal unappliedAmount,
    BigDecimal bankFee,
    String payerName,
    String bankName,
    String accountNumber,
    ReceiptStatus status,
    String remarks,
    Integer version
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param receipt 入金ドメインモデル
     * @return 入金レスポンス DTO
     */
    public static ReceiptResponse from(Receipt receipt) {
        return new ReceiptResponse(
            receipt.getId(),
            receipt.getReceiptNumber(),
            receipt.getReceiptDate(),
            receipt.getCustomerCode(),
            receipt.getCustomerBranchNumber(),
            receipt.getReceiptMethod(),
            receipt.getReceiptAmount(),
            receipt.getAppliedAmount(),
            receipt.getUnappliedAmount(),
            receipt.getBankFee(),
            receipt.getPayerName(),
            receipt.getBankName(),
            receipt.getAccountNumber(),
            receipt.getStatus(),
            receipt.getRemarks(),
            receipt.getVersion()
        );
    }
}
