package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.receipt.ReceiptMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入金登録リクエスト DTO.
 */
public record CreateReceiptRequest(
    LocalDate receiptDate,

    @NotBlank(message = "顧客コードは必須です")
    String customerCode,

    String customerBranchNumber,

    @NotNull(message = "入金方法は必須です")
    ReceiptMethod receiptMethod,

    @NotNull(message = "入金額は必須です")
    @Positive(message = "入金額は正の値である必要があります")
    BigDecimal receiptAmount,

    @PositiveOrZero(message = "振込手数料は0以上である必要があります")
    BigDecimal bankFee,

    String payerName,

    String bankName,

    String accountNumber,

    String remarks
) {
}
