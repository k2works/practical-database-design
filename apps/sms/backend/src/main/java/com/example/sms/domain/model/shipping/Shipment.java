package com.example.sms.domain.model.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出荷エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Shipment {
    private Integer id;
    private String shipmentNumber;
    private LocalDate shipmentDate;
    private Integer orderId;
    private String customerCode;
    private String customerBranchNumber;
    private String shippingDestinationNumber;
    private String shippingDestinationName;
    private String shippingDestinationPostalCode;
    private String shippingDestinationAddress1;
    private String shippingDestinationAddress2;
    private String representativeCode;
    private String warehouseCode;
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.INSTRUCTED;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<ShipmentDetail> details;
}
