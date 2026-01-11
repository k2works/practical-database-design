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
public class Order {
    private Integer id;
    private String orderNumber;
    private OrderType orderType;
    private String itemCode;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate expirationDate;
    private BigDecimal planQuantity;
    private String locationCode;
    private PlanStatus status;
    private Integer mpsId;
    private Integer parentOrderId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private Item item;
    private MasterProductionSchedule mps;
    private Order parentOrder;
    private List<Order> childOrders;
    private List<Requirement> requirements;
}
