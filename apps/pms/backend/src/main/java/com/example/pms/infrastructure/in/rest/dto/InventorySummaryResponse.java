package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.InventoryUseCase.InventorySummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 在庫サマリーレスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponse {
    String itemCode;
    String itemName;
    BigDecimal totalQuantity;
    BigDecimal safetyStock;
    String stockState;

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param summary 在庫サマリー
     * @return InventorySummaryResponse
     */
    public static InventorySummaryResponse from(InventorySummary summary) {
        return InventorySummaryResponse.builder()
            .itemCode(summary.getItemCode())
            .itemName(summary.getItemName())
            .totalQuantity(summary.getTotalQuantity())
            .safetyStock(summary.getSafetyStock())
            .stockState(summary.getStockState().name())
            .build();
    }
}
