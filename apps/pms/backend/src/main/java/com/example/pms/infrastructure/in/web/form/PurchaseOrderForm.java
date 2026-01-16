package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreatePurchaseOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 発注登録フォーム.
 */
@Data
public class PurchaseOrderForm {

    @NotBlank(message = "仕入先は必須です")
    private String supplierCode;

    private String ordererCode;

    private String departmentCode;

    private String remarks;

    @Valid
    @NotEmpty(message = "明細は1件以上必要です")
    private List<PurchaseOrderDetailForm> details = new ArrayList<>();

    /**
     * 発注明細フォーム.
     */
    @Data
    public static class PurchaseOrderDetailForm {

        @NotBlank(message = "品目は必須です")
        private String itemCode;

        private String deliveryLocationCode;

        @NotNull(message = "納入予定日は必須です")
        private LocalDate expectedReceivingDate;

        @NotNull(message = "発注数量は必須です")
        @Positive(message = "発注数量は正の数で入力してください")
        private BigDecimal orderQuantity;

        @NotNull(message = "発注単価は必須です")
        @Positive(message = "発注単価は正の数で入力してください")
        private BigDecimal orderUnitPrice;

        private String detailRemarks;

        /**
         * コマンドに変換する.
         */
        public CreatePurchaseOrderCommand.PurchaseOrderDetailCommand toDetailCommand() {
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
     */
    public CreatePurchaseOrderCommand toCommand() {
        return new CreatePurchaseOrderCommand(
            supplierCode,
            ordererCode,
            departmentCode,
            remarks,
            details.stream()
                .map(PurchaseOrderDetailForm::toDetailCommand)
                .toList()
        );
    }
}
