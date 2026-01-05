package com.example.sms.integration;

import com.example.sms.domain.model.receipt.ReceiptMethod;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateReceiptRequest;
import com.example.sms.infrastructure.in.rest.dto.ReceiptResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 入金 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("入金 API 統合テスト")
class ReceiptApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/v1/receipts";

    @BeforeEach
    void setUp() {
        cleanupAllData();
        setupTransactionTestMasterData();
    }

    @AfterEach
    void tearDown() {
        cleanupAllData();
    }

    @Nested
    @DisplayName("入金登録・取得フロー")
    class ReceiptCrudFlow {

        @Test
        @DisplayName("入金を登録して取得できる")
        void shouldCreateAndRetrieveReceipt() {
            // Given: 入金登録リクエスト
            CreateReceiptRequest createRequest = createReceiptRequest();

            // When: 入金を登録
            ReceiptResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ReceiptResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.receiptNumber()).isNotNull();
            assertThat(createResponse.customerCode()).isEqualTo("CUS-INT-001");
            assertThat(createResponse.receiptMethod()).isEqualTo(ReceiptMethod.BANK_TRANSFER);
            assertThat(createResponse.status()).isEqualTo(ReceiptStatus.RECEIVED);

            // When: 登録した入金を取得
            ReceiptResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/" + createResponse.receiptNumber())
                .retrieve()
                .body(ReceiptResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.receiptNumber()).isEqualTo(createResponse.receiptNumber());
        }

        @Test
        @DisplayName("存在しない入金を取得すると404エラー")
        void shouldReturn404WhenReceiptNotFound() {
            // When & Then: 存在しない入金を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/RCP-NOT-EXIST")
                    .retrieve()
                    .body(ReceiptResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("入金削除フロー")
    class ReceiptDeleteFlow {

        @Test
        @DisplayName("入金を削除できる")
        void shouldDeleteReceipt() {
            // Given: 入金を作成
            CreateReceiptRequest createRequest = createReceiptRequest();
            ReceiptResponse receipt = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ReceiptResponse.class);

            // When: 入金を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/" + receipt.receiptNumber())
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/" + receipt.receiptNumber())
                    .retrieve()
                    .body(ReceiptResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("入金一覧取得")
    class ReceiptListFlow {

        @Test
        @DisplayName("入金一覧を取得できる")
        void shouldGetAllReceipts() {
            // Given: 複数の入金を作成
            createReceiptViaApi();
            createReceiptViaApi();

            // When: 入金一覧を取得
            ReceiptResponse[] receipts = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(ReceiptResponse[].class);

            // Then: 作成した入金が含まれている
            assertThat(receipts).isNotNull();
            assertThat(receipts.length).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("ステータスでフィルタして取得できる")
        void shouldFilterByStatus() {
            // Given: 入金を作成
            createReceiptViaApi();

            // When: ステータスでフィルタ
            ReceiptResponse[] receipts = getRestClient()
                .get()
                .uri(API_PATH + "?status=RECEIVED")
                .retrieve()
                .body(ReceiptResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(receipts).isNotNull();
            assertThat(receipts).allMatch(r -> r.status() == ReceiptStatus.RECEIVED);
        }

        @Test
        @DisplayName("顧客コードでフィルタして取得できる")
        void shouldFilterByCustomerCode() {
            // Given: 入金を作成
            createReceiptViaApi();

            // When: 顧客コードでフィルタ
            ReceiptResponse[] receipts = getRestClient()
                .get()
                .uri(API_PATH + "?customerCode=CUS-INT-001")
                .retrieve()
                .body(ReceiptResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(receipts).isNotNull();
            assertThat(receipts).allMatch(r ->
                "CUS-INT-001".equals(r.customerCode()));
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("入金登録時にデータベースに正しく保存される")
        void shouldPersistReceiptToDatabase() {
            // Given: 入金登録リクエスト
            CreateReceiptRequest createRequest = createReceiptRequest();

            // When: 入金を登録
            ReceiptResponse receipt = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ReceiptResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"入金データ\" WHERE \"入金番号\" = ?",
                Integer.class,
                receipt.receiptNumber()
            );
            assertThat(count).isEqualTo(1);
        }
    }

    /**
     * テスト用の入金登録リクエストを作成.
     */
    private CreateReceiptRequest createReceiptRequest() {
        return new CreateReceiptRequest(
            LocalDate.now(),
            "CUS-INT-001",
            "00",
            ReceiptMethod.BANK_TRANSFER,
            BigDecimal.valueOf(50_000),
            BigDecimal.valueOf(330),
            "統合テスト支払者",
            "テスト銀行",
            "1234567",
            "入金APIテスト"
        );
    }

    /**
     * API 経由で入金を作成.
     */
    private ReceiptResponse createReceiptViaApi() {
        CreateReceiptRequest request = createReceiptRequest();
        return getRestClient()
            .post()
            .uri(API_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(ReceiptResponse.class);
    }
}
