package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.invoice.InvoiceStatus;

/**
 * 請求更新リクエスト DTO.
 */
public record UpdateInvoiceRequest(
    InvoiceStatus status,
    String remarks,
    Integer version
) {
}
