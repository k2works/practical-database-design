package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.sales.OrderStatus;

import java.time.LocalDate;

/**
 * 受注更新リクエスト DTO.
 */
public record UpdateOrderRequest(
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
