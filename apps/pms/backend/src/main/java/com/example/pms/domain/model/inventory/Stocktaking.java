package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 棚卸データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stocktaking {
    private Integer id;
    private String stocktakingNumber;
    private String locationCode;
    private LocalDate stocktakingDate;
    private StocktakingStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private List<StocktakingDetail> details;
}
