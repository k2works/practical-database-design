package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.inventory.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 在庫レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    Integer id;
    String locationCode;
    String itemCode;
    BigDecimal stockQuantity;
    BigDecimal passedQuantity;
    BigDecimal defectiveQuantity;
    BigDecimal uninspectedQuantity;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param stock 在庫
     * @return StockResponse
     */
    public static StockResponse from(Stock stock) {
        return StockResponse.builder()
            .id(stock.getId())
            .locationCode(stock.getLocationCode())
            .itemCode(stock.getItemCode())
            .stockQuantity(stock.getStockQuantity())
            .passedQuantity(stock.getPassedQuantity())
            .defectiveQuantity(stock.getDefectiveQuantity())
            .uninspectedQuantity(stock.getUninspectedQuantity())
            .createdAt(stock.getCreatedAt())
            .updatedAt(stock.getUpdatedAt())
            .build();
    }
}
