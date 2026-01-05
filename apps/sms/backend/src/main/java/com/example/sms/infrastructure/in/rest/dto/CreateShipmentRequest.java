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
 * 出荷登録リクエスト DTO.
 */
public record CreateShipmentRequest(
    LocalDate shipmentDate,

    @NotNull(message = "受注IDは必須です")
    Integer orderId,

    @NotBlank(message = "顧客コードは必須です")
    String customerCode,

    String customerBranchNumber,

    String shippingDestinationNumber,

    String shippingDestinationName,

    String shippingDestinationPostalCode,

    String shippingDestinationAddress1,

    String shippingDestinationAddress2,

    String representativeCode,

    String warehouseCode,

    String remarks,

    @NotEmpty(message = "明細は1件以上必要です")
    @Valid
    List<CreateShipmentDetailRequest> details
) {

    /**
     * 出荷明細登録リクエスト DTO.
     */
    public record CreateShipmentDetailRequest(
        Integer orderDetailId,

        @NotBlank(message = "商品コードは必須です")
        String productCode,

        String productName,

        @NotNull(message = "数量は必須です")
        @Positive(message = "数量は正の値である必要があります")
        BigDecimal shippedQuantity,

        String unit,

        @NotNull(message = "単価は必須です")
        BigDecimal unitPrice,

        String warehouseCode,

        String remarks
    ) {
    }
}
