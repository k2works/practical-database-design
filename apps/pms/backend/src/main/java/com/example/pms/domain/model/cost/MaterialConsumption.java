package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 材料消費データエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialConsumption {
    private Integer id;
    private String workOrderNumber;
    private String materialCode;
    private LocalDate consumptionDate;
    private BigDecimal consumptionQuantity;
    private BigDecimal unitPrice;
    private BigDecimal consumptionAmount;
    private Boolean isDirect;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;
}
