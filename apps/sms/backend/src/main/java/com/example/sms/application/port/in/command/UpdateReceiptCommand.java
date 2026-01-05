package com.example.sms.application.port.in.command;

import com.example.sms.domain.model.receipt.ReceiptStatus;

/**
 * 入金更新コマンド.
 */
public record UpdateReceiptCommand(
    ReceiptStatus status,
    String remarks,
    Integer version
) {
}
