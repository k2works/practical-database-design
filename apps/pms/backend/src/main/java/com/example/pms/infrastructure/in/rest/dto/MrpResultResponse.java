package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.MrpUseCase.MrpResult;
import com.example.pms.application.port.in.MrpUseCase.PlannedOrder;
import com.example.pms.application.port.in.MrpUseCase.ShortageItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MRP 実行結果レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrpResultResponse {
    LocalDateTime executionTime;
    LocalDate periodStart;
    LocalDate periodEnd;
    List<PlannedOrderResponse> plannedOrders;
    List<ShortageItemResponse> shortageItems;

    /**
     * 計画オーダレスポンス.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlannedOrderResponse {
        String itemCode;
        String itemName;
        String orderType;
        BigDecimal quantity;
        LocalDate dueDate;

        /**
         * ドメインモデルからレスポンスを作成する.
         *
         * @param plannedOrder 計画オーダ
         * @return PlannedOrderResponse
         */
        public static PlannedOrderResponse from(PlannedOrder plannedOrder) {
            return PlannedOrderResponse.builder()
                .itemCode(plannedOrder.getItemCode())
                .itemName(plannedOrder.getItemName())
                .orderType(plannedOrder.getOrderType())
                .quantity(plannedOrder.getQuantity())
                .dueDate(plannedOrder.getDueDate())
                .build();
        }
    }

    /**
     * 在庫不足品目レスポンス.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortageItemResponse {
        String itemCode;
        String itemName;
        BigDecimal shortageQuantity;
        LocalDate recommendedOrderDate;

        /**
         * ドメインモデルからレスポンスを作成する.
         *
         * @param shortageItem 在庫不足品目
         * @return ShortageItemResponse
         */
        public static ShortageItemResponse from(ShortageItem shortageItem) {
            return ShortageItemResponse.builder()
                .itemCode(shortageItem.getItemCode())
                .itemName(shortageItem.getItemName())
                .shortageQuantity(shortageItem.getShortageQuantity())
                .recommendedOrderDate(shortageItem.getRecommendedOrderDate())
                .build();
        }
    }

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param result MRP 実行結果
     * @return MrpResultResponse
     */
    public static MrpResultResponse from(MrpResult result) {
        return MrpResultResponse.builder()
            .executionTime(result.getExecutionTime())
            .periodStart(result.getPeriodStart())
            .periodEnd(result.getPeriodEnd())
            .plannedOrders(result.getPlannedOrders().stream()
                .map(PlannedOrderResponse::from)
                .toList())
            .shortageItems(result.getShortageItems().stream()
                .map(ShortageItemResponse::from)
                .toList())
            .build();
    }
}
