package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.sales.OrderStatus;

import java.time.LocalDate;

/**
 * 受注更新コマンド.
 */
public record UpdateOrderCommand(
    String shippingDestinationNumber,
    String representativeCode,
    LocalDate requestedDeliveryDate,
    LocalDate scheduledShippingDate,
    OrderStatus status,
    String customerOrderNumber,
    String remarks,
    Integer version
) {
}
