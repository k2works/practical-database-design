package com.example.fas.infrastructure.in.rest;

import com.example.fas.domain.exception.AccountAlreadyExistsException;
import com.example.fas.domain.exception.AccountNotFoundException;
import com.example.fas.domain.exception.AccountingException;
import com.example.fas.domain.exception.JournalAlreadyCancelledException;
import com.example.fas.domain.exception.JournalBalanceException;
import com.example.fas.domain.exception.JournalNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * グローバル例外ハンドラー.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 勘定科目が見つからない場合の例外ハンドラー.
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
            AccountNotFoundException e) {
        if (log.isWarnEnabled()) {
            log.warn("Account not found: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    /**
     * 勘定科目が既に存在する場合の例外ハンドラー.
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExistsException(
            AccountAlreadyExistsException e) {
        if (log.isWarnEnabled()) {
            log.warn("Account already exists: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    /**
     * 仕訳が見つからない場合の例外ハンドラー.
     */
    @ExceptionHandler(JournalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJournalNotFoundException(
            JournalNotFoundException e) {
        if (log.isWarnEnabled()) {
            log.warn("Journal not found: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    /**
     * 仕訳の貸借バランスエラーの例外ハンドラー.
     */
    @ExceptionHandler(JournalBalanceException.class)
    public ResponseEntity<ErrorResponse> handleJournalBalanceException(
            JournalBalanceException e) {
        if (log.isWarnEnabled()) {
            log.warn("Journal balance error: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    /**
     * 仕訳が既に取消済みの場合の例外ハンドラー.
     */
    @ExceptionHandler(JournalAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponse> handleJournalAlreadyCancelledException(
            JournalAlreadyCancelledException e) {
        if (log.isWarnEnabled()) {
            log.warn("Journal already cancelled: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    /**
     * その他のドメイン例外ハンドラー.
     */
    @ExceptionHandler(AccountingException.class)
    public ResponseEntity<ErrorResponse> handleAccountingException(AccountingException e) {
        if (log.isWarnEnabled()) {
            log.warn("Accounting exception: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    /**
     * バリデーションエラーの例外ハンドラー.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        if (log.isWarnEnabled()) {
            log.warn("Validation error: {}", e.getMessage());
        }

        Map<String, String> errors = new ConcurrentHashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorResponse.builder()
                        .errorCode("VAL001")
                        .message("入力値が不正です")
                        .timestamp(LocalDateTime.now())
                        .errors(errors)
                        .build());
    }

    /**
     * 静的リソースが見つからない場合の例外ハンドラー（favicon.ico等）.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        // favicon.ico等の静的リソース404はログ出力しない
        return ResponseEntity.notFound().build();
    }

    /**
     * 予期しない例外ハンドラー.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        if (log.isErrorEnabled()) {
            log.error("Unexpected error", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("SYS001", "システムエラーが発生しました"));
    }

    private ErrorResponse createErrorResponse(String errorCode, String message) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * エラーレスポンス.
     */
    @Data
    @Builder
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private LocalDateTime timestamp;
    }

    /**
     * バリデーションエラーレスポンス.
     */
    @Data
    @Builder
    public static class ValidationErrorResponse {
        private String errorCode;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> errors;
    }
}
