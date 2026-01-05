package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.invoice.InvoiceStatus;

/**
 * 請求更新コマンド.
 */
public record UpdateInvoiceCommand(
    InvoiceStatus status,
    String remarks,
    Integer version
) {
}
