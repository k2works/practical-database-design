package com.example.pms.domain.model.plan;

import com.example.pms.domain.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterProductionSchedule {
    private Integer id;
    private String mpsNumber;
    private LocalDate planDate;
    private String itemCode;
    private BigDecimal planQuantity;
    private LocalDate dueDate;
    private PlanStatus status;
    private String locationCode;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private Item item;
    private java.util.List<Order> orders;
}
