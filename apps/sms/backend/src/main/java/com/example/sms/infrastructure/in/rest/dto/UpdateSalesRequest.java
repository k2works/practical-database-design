package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.sales.SalesStatus;

/**
 * 売上更新リクエスト DTO.
 */
public record UpdateSalesRequest(
    SalesStatus status,
    Integer billingId,
    String remarks
) {
}
