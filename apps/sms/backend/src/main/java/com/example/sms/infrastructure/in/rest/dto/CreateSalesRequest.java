package com.example.sms.infrastructure.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 売上登録リクエスト DTO.
 */
public record CreateSalesRequest(
    LocalDate salesDate,

    Integer orderId,

    Integer shipmentId,

    @NotBlank(message = "顧客コードは必須です")
    String customerCode,

    String customerBranchNumber,

    String representativeCode,

    String remarks,

    @NotEmpty(message = "明細は1件以上必要です")
    @Valid
    List<CreateSalesDetailRequest> details
) {

    /**
     * 売上明細登録リクエスト DTO.
     */
    public record CreateSalesDetailRequest(
        Integer orderDetailId,

        Integer shipmentDetailId,

        @NotBlank(message = "商品コードは必須です")
        String productCode,

        String productName,

        @NotNull(message = "数量は必須です")
        @Positive(message = "数量は正の値である必要があります")
        BigDecimal salesQuantity,

        String unit,

        @NotNull(message = "単価は必須です")
        BigDecimal unitPrice,

        String remarks
    ) {
    }
}
