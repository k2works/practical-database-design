package com.example.pms.domain.model.quality;

import com.example.pms.domain.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 出荷検査データエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentInspection {
    private Integer id;
    private String inspectionNumber;
    private String shipmentNumber;
    private String itemCode;
    private LocalDate inspectionDate;
    private String inspectorCode;
    private BigDecimal inspectionQuantity;
    private BigDecimal passedQuantity;
    private BigDecimal failedQuantity;
    private InspectionJudgment judgment;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private Item item;
    @Builder.Default
    private List<ShipmentInspectionResult> results = new ArrayList<>();

    /**
     * 不合格率を計算.
     *
     * @return 不合格率（%）
     */
    public BigDecimal getFailureRate() {
        if (inspectionQuantity == null || inspectionQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return failedQuantity.divide(inspectionQuantity, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
