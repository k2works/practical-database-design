package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 発注登録コマンド.
 */
@Value
@Builder
public class CreatePurchaseOrderCommand {
    String supplierCode;
    String ordererCode;
    String departmentCode;
    String remarks;
    List<PurchaseOrderDetailCommand> details;

    /**
     * 発注明細登録コマンド.
     */
    @Value
    @Builder
    public static class PurchaseOrderDetailCommand {
        String itemCode;
        String deliveryLocationCode;
        LocalDate expectedReceivingDate;
        BigDecimal orderQuantity;
        BigDecimal orderUnitPrice;
        String detailRemarks;
    }
}
