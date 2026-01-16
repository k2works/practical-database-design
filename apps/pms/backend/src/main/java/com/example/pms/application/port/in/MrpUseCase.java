package com.example.pms.application.port.in;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MRP ユースケース（Input Port）.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface MrpUseCase {

    /**
     * MRP（所要量展開）を実行する.
     *
     * @param startDate 開始日
     * @param endDate 終了日
     * @return MRP 実行結果
     */
    MrpResult execute(LocalDate startDate, LocalDate endDate);

    /**
     * MRP 実行結果.
     */
    @Value
    @Builder
    class MrpResult {
        LocalDateTime executionTime;
        LocalDate periodStart;
        LocalDate periodEnd;
        List<PlannedOrder> plannedOrders;
        List<ShortageItem> shortageItems;
    }

    /**
     * 計画オーダ.
     */
    @Value
    @Builder
    class PlannedOrder {
        String itemCode;
        String itemName;
        String orderType;
        BigDecimal quantity;
        LocalDate dueDate;
    }

    /**
     * 在庫不足品目.
     */
    @Value
    @Builder
    class ShortageItem {
        String itemCode;
        String itemName;
        BigDecimal shortageQuantity;
        LocalDate recommendedOrderDate;
    }
}
