package com.example.sms.infrastructure.in.rest.exception;

import com.example.sms.domain.exception.BusinessRuleViolationException;
import com.example.sms.domain.exception.DuplicateResourceException;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.ResourceNotFoundException;
import com.example.sms.infrastructure.in.rest.dto.ErrorResponse;
import com.example.sms.infrastructure.in.rest.dto.ValidationErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * グローバル例外ハンドラー.
 */
@RestControllerAdvice(basePackages = "com.example.sms.infrastructure.in.rest")
@SuppressWarnings("PMD.GuardLogStatement")
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        LOG.warn("Resource not found: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException e) {
        LOG.warn("Duplicate resource: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "CONFLICT",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException e) {
        LOG.warn("Business rule violation: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "BUSINESS_RULE_VIOLATION",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException e) {
        LOG.warn("Optimistic lock exception: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "OPTIMISTIC_LOCK",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new ConcurrentHashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "入力値が不正です",
            errors,
            Instant.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        LOG.error("Unexpected error occurred", e);

        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "システムエラーが発生しました",
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
