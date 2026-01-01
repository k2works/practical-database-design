package com.example.sms.domain.model.sales;

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
 * 受注エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class SalesOrder {
    private Integer id;
    private String orderNumber;
    private LocalDate orderDate;
    private String customerCode;
    private String customerBranchNumber;
    private String shippingDestinationNumber;
    private String representativeCode;
    private LocalDate requestedDeliveryDate;
    private LocalDate scheduledShippingDate;
    @Builder.Default
    private BigDecimal orderAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Builder.Default
    private OrderStatus status = OrderStatus.RECEIVED;
    private Integer quotationId;
    private String customerOrderNumber;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder.Default
    private List<SalesOrderDetail> details = new ArrayList<>();
}
