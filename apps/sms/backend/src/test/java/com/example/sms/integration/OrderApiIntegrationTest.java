package com.example.sms.integration;

import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest.CreateOrderDetailRequest;
import com.example.sms.infrastructure.in.rest.dto.OrderResponse;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 受注 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("受注 API 統合テスト")
class OrderApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/v1/orders";

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
    @DisplayName("受注登録・取得フロー")
    class OrderCrudFlow {

        @Test
        @DisplayName("受注を登録して取得できる")
        void shouldCreateAndRetrieveOrder() {
            // Given: 受注登録リクエスト
            CreateOrderRequest createRequest = createOrderRequest();

            // When: 受注を登録（実際の HTTP POST リクエスト）
            OrderResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(OrderResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.orderNumber()).isNotNull();
            assertThat(createResponse.customerCode()).isEqualTo("CUS-INT-001");
            assertThat(createResponse.status()).isEqualTo(OrderStatus.RECEIVED);

            // When: 登録した受注を取得（実際の HTTP GET リクエスト）
            OrderResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/" + createResponse.orderNumber())
                .retrieve()
                .body(OrderResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.orderNumber()).isEqualTo(createResponse.orderNumber());
            assertThat(getResponse.customerCode()).isEqualTo("CUS-INT-001");
        }

        @Test
        @DisplayName("存在しない受注を取得すると404エラー")
        void shouldReturn404WhenOrderNotFound() {
            // When & Then: 存在しない受注を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/ORD-NOT-EXIST")
                    .retrieve()
                    .body(OrderResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("受注キャンセルフロー")
    class OrderCancelFlow {

        @Test
        @DisplayName("受注をキャンセルできる")
        void shouldCancelOrder() {
            // Given: 受注を作成
            CreateOrderRequest createRequest = createOrderRequest();
            OrderResponse createdOrder = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(OrderResponse.class);

            // When: 受注をキャンセル
            OrderResponse cancelResponse = getRestClient()
                .post()
                .uri(API_PATH + "/" + createdOrder.orderNumber() + "/cancel")
                .retrieve()
                .body(OrderResponse.class);

            // Then: キャンセル成功
            assertThat(cancelResponse).isNotNull();
            assertThat(cancelResponse.status()).isEqualTo(OrderStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("受注削除フロー")
    class OrderDeleteFlow {

        @Test
        @DisplayName("受注を削除できる")
        void shouldDeleteOrder() {
            // Given: 受注を作成
            CreateOrderRequest createRequest = createOrderRequest();
            OrderResponse createdOrder = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(OrderResponse.class);

            // When: 受注を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/" + createdOrder.orderNumber())
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/" + createdOrder.orderNumber())
                    .retrieve()
                    .body(OrderResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("受注一覧取得")
    class OrderListFlow {

        @Test
        @DisplayName("受注一覧を取得できる")
        void shouldGetAllOrders() {
            // Given: 複数の受注を作成
            CreateOrderRequest request1 = createOrderRequest();
            CreateOrderRequest request2 = createOrderRequest();
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request1)
                .retrieve()
                .body(OrderResponse.class);
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request2)
                .retrieve()
                .body(OrderResponse.class);

            // When: 受注一覧を取得
            OrderResponse[] orders = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(OrderResponse[].class);

            // Then: 作成した受注が含まれている
            assertThat(orders).isNotNull();
            assertThat(orders.length).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("ステータスでフィルタして取得できる")
        void shouldFilterByStatus() {
            // Given: 受注を作成
            CreateOrderRequest request = createOrderRequest();
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OrderResponse.class);

            // When: ステータスでフィルタ
            OrderResponse[] orders = getRestClient()
                .get()
                .uri(API_PATH + "?status=RECEIVED")
                .retrieve()
                .body(OrderResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(orders).isNotNull();
            assertThat(orders).allMatch(o -> o.status() == OrderStatus.RECEIVED);
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("受注登録時にデータベースに正しく保存される")
        void shouldPersistOrderToDatabase() {
            // Given: 受注登録リクエスト
            CreateOrderRequest createRequest = createOrderRequest();

            // When: 受注を登録
            OrderResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(OrderResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"受注データ\" WHERE \"受注番号\" = ?",
                Integer.class,
                createResponse.orderNumber()
            );
            assertThat(count).isEqualTo(1);

            // Then: 明細データも保存されていることを確認
            Integer detailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"受注明細\" WHERE \"受注ID\" = ?",
                Integer.class,
                createResponse.id()
            );
            assertThat(detailCount).isEqualTo(2);
        }
    }

    /**
     * テスト用の受注登録リクエストを作成.
     */
    private CreateOrderRequest createOrderRequest() {
        List<CreateOrderDetailRequest> details = List.of(
            new CreateOrderDetailRequest(
                "PRD-INT-001",
                "統合テスト商品1",
                BigDecimal.TEN,
                "個",
                BigDecimal.valueOf(5000),
                null,
                LocalDate.now().plusDays(7),
                "明細備考1"
            ),
            new CreateOrderDetailRequest(
                "PRD-INT-002",
                "統合テスト商品2",
                BigDecimal.valueOf(5),
                "個",
                BigDecimal.valueOf(8000),
                null,
                LocalDate.now().plusDays(7),
                "明細備考2"
            )
        );

        return new CreateOrderRequest(
            LocalDate.now(),
            "CUS-INT-001",
            "00",
            null,
            null,
            LocalDate.now().plusDays(7),
            LocalDate.now().plusDays(5),
            null,
            "CUST-ORD-001",
            "テスト受注",
            details
        );
    }
}
