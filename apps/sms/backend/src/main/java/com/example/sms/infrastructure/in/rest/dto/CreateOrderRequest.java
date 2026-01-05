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
 * 受注登録リクエスト DTO.
 */
public record CreateOrderRequest(
    LocalDate orderDate,

    @NotBlank(message = "顧客コードは必須です")
    String customerCode,

    String customerBranchNumber,

    String shippingDestinationNumber,

    String representativeCode,

    LocalDate requestedDeliveryDate,

    LocalDate scheduledShippingDate,

    Integer quotationId,

    String customerOrderNumber,

    String remarks,

    @NotEmpty(message = "明細は1件以上必要です")
    @Valid
    List<CreateOrderDetailRequest> details
) {

    /**
     * 受注明細登録リクエスト DTO.
     */
    public record CreateOrderDetailRequest(
        @NotBlank(message = "商品コードは必須です")
        String productCode,

        String productName,

        @NotNull(message = "数量は必須です")
        @Positive(message = "数量は正の値である必要があります")
        BigDecimal orderQuantity,

        String unit,

        @NotNull(message = "単価は必須です")
        BigDecimal unitPrice,

        String warehouseCode,

        LocalDate requestedDeliveryDate,

        String remarks
    ) {
    }
}
