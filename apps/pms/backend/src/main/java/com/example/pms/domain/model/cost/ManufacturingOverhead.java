package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 製造間接費マスタエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturingOverhead {
    private Integer id;
    private String accountingPeriod;
    private String costCategory;
    private String costCategoryName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;
}
