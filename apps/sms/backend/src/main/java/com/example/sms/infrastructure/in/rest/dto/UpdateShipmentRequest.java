package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.shipping.ShipmentStatus;

/**
 * 出荷更新リクエスト DTO.
 */
public record UpdateShipmentRequest(
    ShipmentStatus status,
    String remarks
) {
}
