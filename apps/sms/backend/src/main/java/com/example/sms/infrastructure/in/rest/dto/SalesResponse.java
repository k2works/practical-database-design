package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 売上レスポンス DTO.
 */
public record SalesResponse(
    Integer id,
    String salesNumber,
    LocalDate salesDate,
    Integer orderId,
    Integer shipmentId,
    String customerCode,
    String customerBranchNumber,
    String representativeCode,
    BigDecimal salesAmount,
    BigDecimal taxAmount,
    BigDecimal totalAmount,
    SalesStatus status,
    Integer billingId,
    String remarks,
    List<SalesDetailResponse> details
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param sales 売上ドメインモデル
     * @return 売上レスポンス DTO
     */
    public static SalesResponse from(Sales sales) {
        List<SalesDetailResponse> detailResponses = null;
        if (sales.getDetails() != null && !sales.getDetails().isEmpty()) {
            detailResponses = sales.getDetails().stream()
                .map(SalesDetailResponse::from)
                .toList();
        }

        return new SalesResponse(
            sales.getId(),
            sales.getSalesNumber(),
            sales.getSalesDate(),
            sales.getOrderId(),
            sales.getShipmentId(),
            sales.getCustomerCode(),
            sales.getCustomerBranchNumber(),
            sales.getRepresentativeCode(),
            sales.getSalesAmount(),
            sales.getTaxAmount(),
            sales.getTotalAmount(),
            sales.getStatus(),
            sales.getBillingId(),
            sales.getRemarks(),
            detailResponses
        );
    }
}
