package com.example.pms.domain.model.quality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ロット構成エンティティ.
 * 親子ロット間の関係を管理（トレーサビリティ用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotComposition {
    private Integer id;
    private String parentLotNumber;
    private String childLotNumber;
    private BigDecimal usedQuantity;
    private LocalDateTime createdAt;

    // リレーション
    private LotMaster parentLot;
    private LotMaster childLot;
}
