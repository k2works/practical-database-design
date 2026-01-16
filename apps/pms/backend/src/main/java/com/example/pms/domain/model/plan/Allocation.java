package com.example.pms.domain.model.plan;

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
public class Allocation {
    private Integer id;
    private Integer requirementId;
    private AllocationType allocationType;
    private Integer orderId;
    private LocalDate allocationDate;
    private BigDecimal allocatedQuantity;
    private String locationCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private Requirement requirement;
    private Order order;
}
