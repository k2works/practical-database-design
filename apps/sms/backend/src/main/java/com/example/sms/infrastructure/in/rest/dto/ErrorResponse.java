package com.example.sms.infrastructure.in.rest.dto;

import java.time.Instant;

/**
 * エラーレスポンス DTO.
 */
public record ErrorResponse(
    int status,
    String code,
    String message,
    Instant timestamp
) {
}
