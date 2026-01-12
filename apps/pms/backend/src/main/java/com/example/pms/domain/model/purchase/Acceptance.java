package com.example.pms.domain.model.purchase;

import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.supplier.Supplier;
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
public class Acceptance {
    private Integer id;
    private String acceptanceNumber;
    private String inspectionNumber;
    private String purchaseOrderNumber;
    private Integer lineNumber;
    private LocalDate acceptanceDate;
    private String acceptorCode;
    private String supplierCode;
    private String itemCode;
    private Boolean miscellaneousItemFlag;
    private BigDecimal acceptedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    // リレーション
    private Inspection inspection;
    private PurchaseOrderDetail purchaseOrderDetail;
    private Supplier supplier;
    private Item item;
}
