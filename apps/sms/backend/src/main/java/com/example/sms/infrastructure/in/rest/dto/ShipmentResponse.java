package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * 出荷レスポンス DTO.
 */
public record ShipmentResponse(
    Integer id,
    String shipmentNumber,
    LocalDate shipmentDate,
    Integer orderId,
    String customerCode,
    String customerBranchNumber,
    String shippingDestinationNumber,
    String shippingDestinationName,
    String shippingDestinationPostalCode,
    String shippingDestinationAddress1,
    String shippingDestinationAddress2,
    String representativeCode,
    String warehouseCode,
    ShipmentStatus status,
    String remarks,
    List<ShipmentDetailResponse> details
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param shipment 出荷ドメインモデル
     * @return 出荷レスポンス DTO
     */
    public static ShipmentResponse from(Shipment shipment) {
        List<ShipmentDetailResponse> detailResponses = null;
        if (shipment.getDetails() != null && !shipment.getDetails().isEmpty()) {
            detailResponses = shipment.getDetails().stream()
                .map(ShipmentDetailResponse::from)
                .toList();
        }

        return new ShipmentResponse(
            shipment.getId(),
            shipment.getShipmentNumber(),
            shipment.getShipmentDate(),
            shipment.getOrderId(),
            shipment.getCustomerCode(),
            shipment.getCustomerBranchNumber(),
            shipment.getShippingDestinationNumber(),
            shipment.getShippingDestinationName(),
            shipment.getShippingDestinationPostalCode(),
            shipment.getShippingDestinationAddress1(),
            shipment.getShippingDestinationAddress2(),
            shipment.getRepresentativeCode(),
            shipment.getWarehouseCode(),
            shipment.getStatus(),
            shipment.getRemarks(),
            detailResponses
        );
    }
}
