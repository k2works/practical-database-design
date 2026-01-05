package com.example.sms.integration;

import com.example.sms.domain.model.shipping.ShipmentStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest.CreateOrderDetailRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateShipmentRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateShipmentRequest.CreateShipmentDetailRequest;
import com.example.sms.infrastructure.in.rest.dto.OrderDetailResponse;
import com.example.sms.infrastructure.in.rest.dto.OrderResponse;
import com.example.sms.infrastructure.in.rest.dto.ShipmentResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 出荷 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("出荷 API 統合テスト")
class ShipmentApiIntegrationTest extends IntegrationTestBase {

    private static final String ORDERS_API = "/api/v1/orders";
    private static final String API_PATH = "/api/v1/shipments";

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
    @DisplayName("出荷登録・取得フロー")
    class ShipmentCrudFlow {

        @Test
        @DisplayName("出荷を登録して取得できる")
        void shouldCreateAndRetrieveShipment() {
            // Given: 受注を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);

            // When: 出荷を登録
            CreateShipmentRequest shipmentRequest = createShipmentRequest(order, orderDetails);
            ShipmentResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRequest)
                .retrieve()
                .body(ShipmentResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.shipmentNumber()).isNotNull();
            assertThat(createResponse.orderId()).isEqualTo(order.id());
            assertThat(createResponse.status()).isEqualTo(ShipmentStatus.INSTRUCTED);

            // When: 登録した出荷を取得
            ShipmentResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/" + createResponse.shipmentNumber())
                .retrieve()
                .body(ShipmentResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.shipmentNumber()).isEqualTo(createResponse.shipmentNumber());
        }

        @Test
        @DisplayName("存在しない出荷を取得すると404エラー")
        void shouldReturn404WhenShipmentNotFound() {
            // When & Then: 存在しない出荷を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/SHP-NOT-EXIST")
                    .retrieve()
                    .body(ShipmentResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("出荷確定フロー")
    class ShipmentConfirmFlow {

        @Test
        @DisplayName("出荷を確定できる")
        void shouldConfirmShipment() {
            // Given: 出荷を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            CreateShipmentRequest shipmentRequest = createShipmentRequest(order, orderDetails);
            ShipmentResponse shipment = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRequest)
                .retrieve()
                .body(ShipmentResponse.class);

            // When: 出荷を確定
            ShipmentResponse confirmedShipment = getRestClient()
                .post()
                .uri(API_PATH + "/" + shipment.shipmentNumber() + "/confirm")
                .retrieve()
                .body(ShipmentResponse.class);

            // Then: 確定成功
            assertThat(confirmedShipment).isNotNull();
            assertThat(confirmedShipment.status()).isEqualTo(ShipmentStatus.SHIPPED);
        }
    }

    @Nested
    @DisplayName("出荷削除フロー")
    class ShipmentDeleteFlow {

        @Test
        @DisplayName("出荷を削除できる")
        void shouldDeleteShipment() {
            // Given: 出荷を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            CreateShipmentRequest shipmentRequest = createShipmentRequest(order, orderDetails);
            ShipmentResponse shipment = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRequest)
                .retrieve()
                .body(ShipmentResponse.class);

            // When: 出荷を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/" + shipment.shipmentNumber())
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/" + shipment.shipmentNumber())
                    .retrieve()
                    .body(ShipmentResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("出荷一覧取得")
    class ShipmentListFlow {

        @Test
        @DisplayName("出荷一覧を取得できる")
        void shouldGetAllShipments() {
            // Given: 複数の出荷を作成
            OrderResponse order1 = createOrder();
            OrderResponse order2 = createOrder();
            OrderDetailResponse[] orderDetails1 = getOrderDetails(order1);
            OrderDetailResponse[] orderDetails2 = getOrderDetails(order2);

            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createShipmentRequest(order1, orderDetails1))
                .retrieve()
                .body(ShipmentResponse.class);
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createShipmentRequest(order2, orderDetails2))
                .retrieve()
                .body(ShipmentResponse.class);

            // When: 出荷一覧を取得
            ShipmentResponse[] shipments = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(ShipmentResponse[].class);

            // Then: 作成した出荷が含まれている
            assertThat(shipments).isNotNull();
            assertThat(shipments.length).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("ステータスでフィルタして取得できる")
        void shouldFilterByStatus() {
            // Given: 出荷を作成して確定
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            ShipmentResponse shipment = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createShipmentRequest(order, orderDetails))
                .retrieve()
                .body(ShipmentResponse.class);

            getRestClient()
                .post()
                .uri(API_PATH + "/" + shipment.shipmentNumber() + "/confirm")
                .retrieve()
                .body(ShipmentResponse.class);

            // When: ステータスでフィルタ
            ShipmentResponse[] shipments = getRestClient()
                .get()
                .uri(API_PATH + "?status=SHIPPED")
                .retrieve()
                .body(ShipmentResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(shipments).isNotNull();
            assertThat(shipments).allMatch(s -> s.status() == ShipmentStatus.SHIPPED);
        }

        @Test
        @DisplayName("受注IDでフィルタして取得できる")
        void shouldFilterByOrderId() {
            // Given: 出荷を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createShipmentRequest(order, orderDetails))
                .retrieve()
                .body(ShipmentResponse.class);

            // When: 受注IDでフィルタ
            ShipmentResponse[] shipments = getRestClient()
                .get()
                .uri(API_PATH + "?orderId=" + order.id())
                .retrieve()
                .body(ShipmentResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(shipments).isNotNull();
            assertThat(shipments).allMatch(s -> s.orderId().equals(order.id()));
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("出荷登録時にデータベースに正しく保存される")
        void shouldPersistShipmentToDatabase() {
            // Given: 受注と出荷を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            CreateShipmentRequest shipmentRequest = createShipmentRequest(order, orderDetails);

            // When: 出荷を登録
            ShipmentResponse shipment = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRequest)
                .retrieve()
                .body(ShipmentResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"出荷データ\" WHERE \"出荷番号\" = ?",
                Integer.class,
                shipment.shipmentNumber()
            );
            assertThat(count).isEqualTo(1);

            // Then: 明細データも保存されていることを確認
            Integer detailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"出荷明細\" WHERE \"出荷ID\" = ?",
                Integer.class,
                shipment.id()
            );
            assertThat(detailCount).isEqualTo(2);
        }
    }

    /**
     * 受注を作成.
     */
    private OrderResponse createOrder() {
        List<CreateOrderDetailRequest> details = List.of(
            new CreateOrderDetailRequest(
                "PRD-INT-001",
                "統合テスト商品1",
                BigDecimal.TEN,
                "個",
                BigDecimal.valueOf(5000),
                null,
                LocalDate.now().plusDays(7),
                null
            ),
            new CreateOrderDetailRequest(
                "PRD-INT-002",
                "統合テスト商品2",
                BigDecimal.valueOf(5),
                "個",
                BigDecimal.valueOf(8000),
                null,
                LocalDate.now().plusDays(7),
                null
            )
        );

        CreateOrderRequest orderRequest = new CreateOrderRequest(
            LocalDate.now(),
            "CUS-INT-001",
            "00",
            null,
            null,
            LocalDate.now().plusDays(7),
            LocalDate.now().plusDays(5),
            null,
            null,
            "出荷テスト用受注",
            details
        );

        return getRestClient()
            .post()
            .uri(ORDERS_API)
            .contentType(MediaType.APPLICATION_JSON)
            .body(orderRequest)
            .retrieve()
            .body(OrderResponse.class);
    }

    /**
     * 受注明細を取得.
     */
    private OrderDetailResponse[] getOrderDetails(OrderResponse order) {
        return getRestClient()
            .get()
            .uri(ORDERS_API + "/" + order.orderNumber() + "/details")
            .retrieve()
            .body(OrderDetailResponse[].class);
    }

    /**
     * テスト用の出荷登録リクエストを作成.
     */
    private CreateShipmentRequest createShipmentRequest(
            OrderResponse order, OrderDetailResponse... orderDetails) {
        List<CreateShipmentDetailRequest> details = new ArrayList<>();
        for (OrderDetailResponse detail : orderDetails) {
            details.add(new CreateShipmentDetailRequest(
                detail.id(),
                detail.productCode(),
                detail.productName(),
                detail.orderQuantity(),
                detail.unit(),
                detail.unitPrice(),
                null,
                null
            ));
        }

        return new CreateShipmentRequest(
            LocalDate.now(),
            order.id(),
            "CUS-INT-001",
            "00",
            null,
            "統合テスト出荷先",
            "100-0001",
            "東京都千代田区",
            null,
            null,
            null,
            "出荷APIテスト",
            details
        );
    }
}
