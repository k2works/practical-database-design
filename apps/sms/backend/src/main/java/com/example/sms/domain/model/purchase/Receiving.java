package com.example.sms.domain.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 入荷エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Receiving {
    private Integer id;
    private String receivingNumber;
    private Integer purchaseOrderId;
    private String supplierCode;
    @Builder.Default
    private String supplierBranchNumber = "00";
    private LocalDate receivingDate;
    @Builder.Default
    private ReceivingStatus status = ReceivingStatus.WAITING;
    private String receiverCode;
    private String warehouseCode;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /** 楽観ロック用バージョン. */
    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<ReceivingDetail> details = new ArrayList<>();
}
