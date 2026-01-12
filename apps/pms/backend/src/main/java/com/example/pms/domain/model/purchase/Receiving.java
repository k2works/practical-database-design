package com.example.pms.domain.model.purchase;

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
public class Receiving {
    private Integer id;
    private String receivingNumber;
    private String purchaseOrderNumber;
    private Integer lineNumber;
    private LocalDate receivingDate;
    private String receiverCode;
    private ReceivingType receivingType;
    private String itemCode;
    private Boolean miscellaneousItemFlag;
    private BigDecimal receivingQuantity;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private PurchaseOrderDetail purchaseOrderDetail;
    private Item item;
    private List<Inspection> inspections;
}
