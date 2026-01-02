package com.example.sms.infrastructure.in.rest.dto;

import java.time.Instant;
import java.util.Map;

/**
 * バリデーションエラーレスポンス DTO.
 */
public record ValidationErrorResponse(
    int status,
    String code,
    String message,
    Map<String, String> errors,
    Instant timestamp
) {
}
