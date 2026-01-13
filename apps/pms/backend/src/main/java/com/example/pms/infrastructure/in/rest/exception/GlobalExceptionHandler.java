package com.example.pms.infrastructure.in.rest.exception;

import com.example.pms.domain.exception.DomainException;
import com.example.pms.domain.exception.DuplicateItemException;
import com.example.pms.domain.exception.InsufficientInventoryException;
import com.example.pms.domain.exception.InvalidOrderStateException;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.exception.PurchaseOrderNotFoundException;
import com.example.pms.domain.exception.WorkOrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * グローバル例外ハンドラ.
 */
@RestControllerAdvice
@SuppressWarnings("PMD.GuardLogStatement")
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_BASE_URI = "https://api.example.com/errors/";

    /**
     * 品目が見つからない例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ProblemDetail handleItemNotFoundException(ItemNotFoundException ex) {
        LOGGER.warn("品目が見つかりません: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("品目が見つかりません");
        problem.setType(URI.create(ERROR_BASE_URI + "item-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * 品目コード重複例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(DuplicateItemException.class)
    public ProblemDetail handleDuplicateItemException(DuplicateItemException ex) {
        LOGGER.warn("品目コード重複: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("品目コード重複");
        problem.setType(URI.create(ERROR_BASE_URI + "duplicate-item"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * 発注が見つからない例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public ProblemDetail handlePurchaseOrderNotFoundException(PurchaseOrderNotFoundException ex) {
        LOGGER.warn("発注が見つかりません: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("発注が見つかりません");
        problem.setType(URI.create(ERROR_BASE_URI + "purchase-order-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * 発注状態不正例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(InvalidOrderStateException.class)
    public ProblemDetail handleInvalidOrderStateException(InvalidOrderStateException ex) {
        LOGGER.warn("発注状態不正: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("発注状態不正");
        problem.setType(URI.create(ERROR_BASE_URI + "invalid-order-state"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * 在庫不足例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(InsufficientInventoryException.class)
    public ProblemDetail handleInsufficientInventoryException(InsufficientInventoryException ex) {
        LOGGER.warn("在庫不足: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("在庫不足");
        problem.setType(URI.create(ERROR_BASE_URI + "insufficient-inventory"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * 作業指示が見つからない例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(WorkOrderNotFoundException.class)
    public ProblemDetail handleWorkOrderNotFoundException(WorkOrderNotFoundException ex) {
        LOGGER.warn("作業指示が見つかりません: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("作業指示が見つかりません");
        problem.setType(URI.create(ERROR_BASE_URI + "work-order-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * バリデーション例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));

        LOGGER.warn("バリデーションエラー: {}", errors);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, errors);
        problem.setTitle("入力値が不正です");
        problem.setType(URI.create(ERROR_BASE_URI + "validation-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * ドメイン例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        LOGGER.warn("ドメイン例外: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("業務エラー");
        problem.setType(URI.create(ERROR_BASE_URI + "domain-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * その他の例外をハンドリング.
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        LOGGER.error("予期しないエラー", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "予期しないエラーが発生しました");
        problem.setTitle("内部エラー");
        problem.setType(URI.create(ERROR_BASE_URI + "internal-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
