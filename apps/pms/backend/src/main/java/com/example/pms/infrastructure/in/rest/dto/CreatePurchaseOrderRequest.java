package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.command.CreatePurchaseOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 発注登録リクエスト DTO.
 */
@Data
public class CreatePurchaseOrderRequest {

    @NotBlank(message = "仕入先コードは必須です")
    private String supplierCode;

    private String ordererCode;

    private String departmentCode;

    private String remarks;

    @NotEmpty(message = "発注明細は必須です")
    @Valid
    private List<PurchaseOrderDetailRequest> details;

    /**
     * 発注明細リクエスト DTO.
     */
    @Data
    public static class PurchaseOrderDetailRequest {

        @NotBlank(message = "品目コードは必須です")
        private String itemCode;

        private String deliveryLocationCode;

        private LocalDate expectedReceivingDate;

        @NotNull(message = "発注数量は必須です")
        @Positive(message = "発注数量は正の値である必要があります")
        private BigDecimal orderQuantity;

        private BigDecimal orderUnitPrice;

        private String detailRemarks;

        /**
         * コマンドに変換する.
         *
         * @return PurchaseOrderDetailCommand
         */
        public CreatePurchaseOrderCommand.PurchaseOrderDetailCommand toCommand() {
            return new CreatePurchaseOrderCommand.PurchaseOrderDetailCommand(
                itemCode,
                deliveryLocationCode,
                expectedReceivingDate,
                orderQuantity,
                orderUnitPrice,
                detailRemarks
            );
        }
    }

    /**
     * コマンドに変換する.
     *
     * @return CreatePurchaseOrderCommand
     */
    public CreatePurchaseOrderCommand toCommand() {
        return new CreatePurchaseOrderCommand(
            supplierCode,
            ordererCode,
            departmentCode,
            remarks,
            details.stream()
                .map(PurchaseOrderDetailRequest::toCommand)
                .toList()
        );
    }
}
