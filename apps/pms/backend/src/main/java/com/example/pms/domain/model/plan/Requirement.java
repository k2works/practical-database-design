package com.example.pms.domain.model.plan;

import com.example.pms.domain.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Requirement {
    private Integer id;
    private String requirementNumber;
    private Integer orderId;
    private String itemCode;
    private LocalDate dueDate;
    private BigDecimal requiredQuantity;
    private BigDecimal allocatedQuantity;
    private BigDecimal shortageQuantity;
    private String locationCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // リレーション
    private Order order;
    private Item item;
    private List<Allocation> allocations;
}
