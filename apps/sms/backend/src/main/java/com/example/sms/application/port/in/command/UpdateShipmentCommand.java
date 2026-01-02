package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.shipping.ShipmentStatus;

/**
 * 出荷更新コマンド.
 */
public record UpdateShipmentCommand(
    ShipmentStatus status,
    String remarks
) {
}
