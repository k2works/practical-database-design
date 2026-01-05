package com.example.sms.infrastructure.in.rest.exception;

import com.example.sms.domain.exception.CreditLimitExceededException;
import com.example.sms.domain.exception.DuplicateProductException;
import com.example.sms.domain.exception.InsufficientInventoryException;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.ProductNotFoundException;
import com.example.sms.infrastructure.in.rest.dto.ErrorResponse;
import com.example.sms.infrastructure.in.rest.dto.ValidationErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * グローバル例外ハンドラーテスト.
 */
@DisplayName("GlobalExceptionHandler テスト")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("ResourceNotFoundException ハンドリング")
    class ResourceNotFoundExceptionHandling {

        @Test
        @DisplayName("404 NOT_FOUND を返す")
        void shouldReturn404NotFound() {
            ProductNotFoundException exception = new ProductNotFoundException("PROD-001");

            ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(404);
            assertThat(response.getBody().code()).isEqualTo("NOT_FOUND");
            assertThat(response.getBody().message()).isEqualTo("商品が見つかりません: PROD-001");
            assertThat(response.getBody().timestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("DuplicateResourceException ハンドリング")
    class DuplicateResourceExceptionHandling {

        @Test
        @DisplayName("409 CONFLICT を返す")
        void shouldReturn409Conflict() {
            DuplicateProductException exception = new DuplicateProductException("PROD-001");

            ResponseEntity<ErrorResponse> response = handler.handleDuplicateResource(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(409);
            assertThat(response.getBody().code()).isEqualTo("CONFLICT");
            assertThat(response.getBody().message()).contains("PROD-001");
            assertThat(response.getBody().timestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("BusinessRuleViolationException ハンドリング")
    class BusinessRuleViolationExceptionHandling {

        @Test
        @DisplayName("与信限度額超過で 422 UNPROCESSABLE_ENTITY を返す")
        void shouldReturn422ForCreditLimitExceeded() {
            CreditLimitExceededException exception = new CreditLimitExceededException(
                "CUS-001",
                new BigDecimal("1000000"),
                new BigDecimal("1500000")
            );

            ResponseEntity<ErrorResponse> response = handler.handleBusinessRuleViolation(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(422);
            assertThat(response.getBody().code()).isEqualTo("BUSINESS_RULE_VIOLATION");
            assertThat(response.getBody().message()).contains("CUS-001", "1000000", "1500000");
        }

        @Test
        @DisplayName("在庫不足で 422 UNPROCESSABLE_ENTITY を返す")
        void shouldReturn422ForInsufficientInventory() {
            InsufficientInventoryException exception = new InsufficientInventoryException(
                "PROD-001",
                new BigDecimal("50"),
                new BigDecimal("100")
            );

            ResponseEntity<ErrorResponse> response = handler.handleBusinessRuleViolation(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(422);
            assertThat(response.getBody().code()).isEqualTo("BUSINESS_RULE_VIOLATION");
            assertThat(response.getBody().message()).contains("PROD-001", "50", "100");
        }
    }

    @Nested
    @DisplayName("OptimisticLockException ハンドリング")
    class OptimisticLockExceptionHandling {

        @Test
        @DisplayName("409 CONFLICT (OPTIMISTIC_LOCK) を返す")
        void shouldReturn409OptimisticLock() {
            OptimisticLockException exception = new OptimisticLockException("受注", "ORD-001");

            ResponseEntity<ErrorResponse> response = handler.handleOptimisticLock(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(409);
            assertThat(response.getBody().code()).isEqualTo("OPTIMISTIC_LOCK");
            assertThat(response.getBody().message()).contains("受注", "ORD-001");
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException ハンドリング")
    class ValidationExceptionHandling {

        @Test
        @DisplayName("400 BAD_REQUEST (VALIDATION_ERROR) を返す")
        void shouldReturn400ValidationError() throws Exception {
            // Create a mock validation error
            TestRequest target = new TestRequest();
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testRequest");
            bindingResult.addError(new FieldError("testRequest", "productCode", "商品コードは必須です"));
            bindingResult.addError(new FieldError("testRequest", "productName", "商品名は必須です"));

            MethodParameter methodParameter = new MethodParameter(
                TestController.class.getMethod("create", TestRequest.class), 0);
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult);

            ResponseEntity<ValidationErrorResponse> response = handler.handleValidationErrors(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
            assertThat(response.getBody().message()).isEqualTo("入力値が不正です");
            assertThat(response.getBody().errors()).containsEntry("productCode", "商品コードは必須です");
            assertThat(response.getBody().errors()).containsEntry("productName", "商品名は必須です");
        }
    }

    @Nested
    @DisplayName("一般的な例外ハンドリング")
    class GenericExceptionHandling {

        @Test
        @DisplayName("500 INTERNAL_SERVER_ERROR を返す")
        void shouldReturn500InternalError() {
            RuntimeException exception = new RuntimeException("予期せぬエラー");

            ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(500);
            assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
            assertThat(response.getBody().message()).isEqualTo("システムエラーが発生しました");
        }
    }

    // テスト用のダミークラス
    static class TestRequest {
        String productCode;
        String productName;
    }

    static class TestController {
        public void create(TestRequest request) {
            // テスト用のダミーメソッド
        }
    }
}
