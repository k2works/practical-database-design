package com.example.pms.domain.model.purchase;

import com.example.pms.domain.model.supplier.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    private Integer id;
    private String purchaseOrderNumber;
    private LocalDate orderDate;
    private String supplierCode;
    private String ordererCode;
    private String departmentCode;
    private PurchaseOrderStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private Supplier supplier;
    private List<PurchaseOrderDetail> details;
}
