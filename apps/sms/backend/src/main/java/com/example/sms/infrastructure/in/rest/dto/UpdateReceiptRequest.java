package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.receipt.ReceiptStatus;

/**
 * 入金更新リクエスト DTO.
 */
public record UpdateReceiptRequest(
    ReceiptStatus status,
    String remarks,
    Integer version
) {
}
