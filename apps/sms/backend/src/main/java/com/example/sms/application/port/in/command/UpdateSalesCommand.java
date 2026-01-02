package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.sales.SalesStatus;

/**
 * 売上更新コマンド.
 */
public record UpdateSalesCommand(
    SalesStatus status,
    Integer billingId,
    String remarks
) {
}
