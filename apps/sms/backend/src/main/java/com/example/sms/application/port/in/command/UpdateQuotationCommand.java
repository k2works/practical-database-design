package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.sales.QuotationStatus;

import java.time.LocalDate;

/**
 * 見積更新コマンド.
 */
public record UpdateQuotationCommand(
    LocalDate validUntil,
    String subject,
    QuotationStatus status,
    String remarks,
    Integer version
) {
}
