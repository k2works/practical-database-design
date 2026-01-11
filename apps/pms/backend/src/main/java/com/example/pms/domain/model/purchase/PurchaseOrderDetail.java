package com.example.pms.domain.model.purchase;

import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.plan.Order;
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
public class PurchaseOrderDetail {
    private Integer id;
    private String purchaseOrderNumber;
    private Integer lineNumber;
    private String orderNumber;
    private String deliveryLocationCode;
    private String itemCode;
    private Boolean miscellaneousItemFlag;
    private LocalDate expectedReceivingDate;
    private LocalDate confirmedDeliveryDate;
    private BigDecimal orderUnitPrice;
    private BigDecimal orderQuantity;
    private BigDecimal receivedQuantity;
    private BigDecimal inspectedQuantity;
    private BigDecimal acceptedQuantity;
    private BigDecimal orderAmount;
    private BigDecimal taxAmount;
    private Boolean completedFlag;
    private String detailRemarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private PurchaseOrder purchaseOrder;
    private Item item;
    private Order order;
    private MiscellaneousItem miscellaneousItem;
    private List<Receiving> receivings;
    private List<Acceptance> acceptances;
}
