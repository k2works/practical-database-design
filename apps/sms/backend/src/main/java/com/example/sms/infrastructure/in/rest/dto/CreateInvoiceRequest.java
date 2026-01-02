package com.example.sms.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求登録リクエスト DTO.
 */
public record CreateInvoiceRequest(
    LocalDate invoiceDate,

    @NotBlank(message = "顧客コードは必須です")
    String customerCode,

    String customerBranchNumber,

    @NotNull(message = "締日は必須です")
    LocalDate closingDate,

    @PositiveOrZero(message = "前回残高は0以上である必要があります")
    BigDecimal previousBalance,

    @PositiveOrZero(message = "入金額は0以上である必要があります")
    BigDecimal receiptAmount,

    @NotNull(message = "今回売上額は必須です")
    @PositiveOrZero(message = "今回売上額は0以上である必要があります")
    BigDecimal currentSalesAmount,

    @PositiveOrZero(message = "今回消費税は0以上である必要があります")
    BigDecimal currentTaxAmount,

    LocalDate dueDate,

    String remarks
) {
}
