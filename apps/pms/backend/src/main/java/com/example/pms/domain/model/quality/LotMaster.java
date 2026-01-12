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
 * ロットマスタエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotMaster {
    private Integer id;
    private String lotNumber;
    private String itemCode;
    private LotType lotType;
    private LocalDate manufactureDate;
    private LocalDate expirationDate;
    private BigDecimal quantity;
    private String warehouseCode;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private Item item;
    @Builder.Default
    private List<LotComposition> parentLotRelations = new ArrayList<>();
    @Builder.Default
    private List<LotComposition> childLotRelations = new ArrayList<>();

    /**
     * 有効期限が切れているかチェック.
     *
     * @return 有効期限切れの場合 true
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }
}
