package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 受注レスポンス DTO.
 */
public record OrderResponse(
    Integer id,
    String orderNumber,
    LocalDate orderDate,
    String customerCode,
    String customerBranchNumber,
    String shippingDestinationNumber,
    String representativeCode,
    LocalDate requestedDeliveryDate,
    LocalDate scheduledShippingDate,
    BigDecimal orderAmount,
    BigDecimal taxAmount,
    BigDecimal totalAmount,
    OrderStatus status,
    Integer quotationId,
    String customerOrderNumber,
    String remarks,
    Integer version,
    List<OrderDetailResponse> details
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param order 受注ドメインモデル
     * @return 受注レスポンス DTO
     */
    public static OrderResponse from(SalesOrder order) {
        List<OrderDetailResponse> detailResponses = null;
        if (order.getDetails() != null && !order.getDetails().isEmpty()) {
            detailResponses = order.getDetails().stream()
                .map(OrderDetailResponse::from)
                .toList();
        }

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getOrderDate(),
            order.getCustomerCode(),
            order.getCustomerBranchNumber(),
            order.getShippingDestinationNumber(),
            order.getRepresentativeCode(),
            order.getRequestedDeliveryDate(),
            order.getScheduledShippingDate(),
            order.getOrderAmount(),
            order.getTaxAmount(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getQuotationId(),
            order.getCustomerOrderNumber(),
            order.getRemarks(),
            order.getVersion(),
            detailResponses
        );
    }
}
