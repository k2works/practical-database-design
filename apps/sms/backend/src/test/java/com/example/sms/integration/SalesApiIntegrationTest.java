package com.example.sms.integration;

import com.example.sms.domain.model.sales.SalesStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest.CreateOrderDetailRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateSalesRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateSalesRequest.CreateSalesDetailRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateShipmentRequest;
import com.example.sms.infrastructure.in.rest.dto.CreateShipmentRequest.CreateShipmentDetailRequest;
import com.example.sms.infrastructure.in.rest.dto.OrderDetailResponse;
import com.example.sms.infrastructure.in.rest.dto.OrderResponse;
import com.example.sms.infrastructure.in.rest.dto.SalesResponse;
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
 * 売上 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("売上 API 統合テスト")
class SalesApiIntegrationTest extends IntegrationTestBase {

    private static final String ORDERS_API = "/api/v1/orders";
    private static final String SHIPMENTS_API = "/api/v1/shipments";
    private static final String API_PATH = "/api/v1/sales";

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
    @DisplayName("売上登録・取得フロー")
    class SalesCrudFlow {

        @Test
        @DisplayName("売上を登録して取得できる")
        void shouldCreateAndRetrieveSales() {
            // Given: 受注と出荷を作成して確定
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            ShipmentResponse shipment = createAndConfirmShipment(order, orderDetails);

            // When: 売上を登録
            CreateSalesRequest salesRequest = createSalesRequest(
                order, shipment, orderDetails);
            SalesResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(salesRequest)
                .retrieve()
                .body(SalesResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.salesNumber()).isNotNull();
            assertThat(createResponse.orderId()).isEqualTo(order.id());
            assertThat(createResponse.shipmentId()).isEqualTo(shipment.id());
            assertThat(createResponse.status()).isEqualTo(SalesStatus.RECORDED);

            // When: 登録した売上を取得
            SalesResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/" + createResponse.salesNumber())
                .retrieve()
                .body(SalesResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.salesNumber()).isEqualTo(createResponse.salesNumber());
        }

        @Test
        @DisplayName("存在しない売上を取得すると404エラー")
        void shouldReturn404WhenSalesNotFound() {
            // When & Then: 存在しない売上を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/SLS-NOT-EXIST")
                    .retrieve()
                    .body(SalesResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("売上キャンセルフロー")
    class SalesCancelFlow {

        @Test
        @DisplayName("売上をキャンセルできる")
        void shouldCancelSales() {
            // Given: 売上を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            ShipmentResponse shipment = createAndConfirmShipment(order, orderDetails);
            CreateSalesRequest salesRequest = createSalesRequest(
                order, shipment, orderDetails);
            SalesResponse sales = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(salesRequest)
                .retrieve()
                .body(SalesResponse.class);

            // When: 売上をキャンセル
            SalesResponse cancelledSales = getRestClient()
                .post()
                .uri(API_PATH + "/" + sales.salesNumber() + "/cancel")
                .retrieve()
                .body(SalesResponse.class);

            // Then: キャンセル成功
            assertThat(cancelledSales).isNotNull();
            assertThat(cancelledSales.status()).isEqualTo(SalesStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("売上削除フロー")
    class SalesDeleteFlow {

        @Test
        @DisplayName("売上を削除できる")
        void shouldDeleteSales() {
            // Given: 売上を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            ShipmentResponse shipment = createAndConfirmShipment(order, orderDetails);
            CreateSalesRequest salesRequest = createSalesRequest(
                order, shipment, orderDetails);
            SalesResponse sales = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(salesRequest)
                .retrieve()
                .body(SalesResponse.class);

            // When: 売上を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/" + sales.salesNumber())
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/" + sales.salesNumber())
                    .retrieve()
                    .body(SalesResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("売上一覧取得")
    class SalesListFlow {

        @Test
        @DisplayName("売上一覧を取得できる")
        void shouldGetAllSales() {
            // Given: 複数の売上を作成
            createSalesViaApi();
            createSalesViaApi();

            // When: 売上一覧を取得
            SalesResponse[] salesList = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(SalesResponse[].class);

            // Then: 作成した売上が含まれている
            assertThat(salesList).isNotNull();
            assertThat(salesList.length).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("ステータスでフィルタして取得できる")
        void shouldFilterByStatus() {
            // Given: 売上を作成
            createSalesViaApi();

            // When: ステータスでフィルタ
            SalesResponse[] salesList = getRestClient()
                .get()
                .uri(API_PATH + "?status=RECORDED")
                .retrieve()
                .body(SalesResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(salesList).isNotNull();
            assertThat(salesList).allMatch(s -> s.status() == SalesStatus.RECORDED);
        }

        @Test
        @DisplayName("顧客コードでフィルタして取得できる")
        void shouldFilterByCustomerCode() {
            // Given: 売上を作成
            createSalesViaApi();

            // When: 顧客コードでフィルタ
            SalesResponse[] salesList = getRestClient()
                .get()
                .uri(API_PATH + "?customerCode=CUS-INT-001")
                .retrieve()
                .body(SalesResponse[].class);

            // Then: フィルタ結果が返される
            assertThat(salesList).isNotNull();
            assertThat(salesList).allMatch(s ->
                "CUS-INT-001".equals(s.customerCode()));
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("売上登録時にデータベースに正しく保存される")
        void shouldPersistSalesToDatabase() {
            // Given: 売上を作成
            OrderResponse order = createOrder();
            OrderDetailResponse[] orderDetails = getOrderDetails(order);
            ShipmentResponse shipment = createAndConfirmShipment(order, orderDetails);
            CreateSalesRequest salesRequest = createSalesRequest(
                order, shipment, orderDetails);

            // When: 売上を登録
            SalesResponse sales = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(salesRequest)
                .retrieve()
                .body(SalesResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"売上データ\" WHERE \"売上番号\" = ?",
                Integer.class,
                sales.salesNumber()
            );
            assertThat(count).isEqualTo(1);

            // Then: 明細データも保存されていることを確認
            Integer detailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"売上明細\" WHERE \"売上ID\" = ?",
                Integer.class,
                sales.id()
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
            "売上テスト用受注",
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
     * 出荷を作成して確定.
     */
    private ShipmentResponse createAndConfirmShipment(
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

        CreateShipmentRequest shipmentRequest = new CreateShipmentRequest(
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
            "売上テスト用出荷",
            details
        );

        ShipmentResponse shipment = getRestClient()
            .post()
            .uri(SHIPMENTS_API)
            .contentType(MediaType.APPLICATION_JSON)
            .body(shipmentRequest)
            .retrieve()
            .body(ShipmentResponse.class);

        // 出荷確定
        return getRestClient()
            .post()
            .uri(SHIPMENTS_API + "/" + shipment.shipmentNumber() + "/confirm")
            .retrieve()
            .body(ShipmentResponse.class);
    }

    /**
     * テスト用の売上登録リクエストを作成.
     */
    private CreateSalesRequest createSalesRequest(
            OrderResponse order, ShipmentResponse shipment,
            OrderDetailResponse... orderDetails) {
        List<CreateSalesDetailRequest> details = new ArrayList<>();
        for (OrderDetailResponse detail : orderDetails) {
            details.add(new CreateSalesDetailRequest(
                detail.id(),
                null,
                detail.productCode(),
                detail.productName(),
                detail.orderQuantity(),
                detail.unit(),
                detail.unitPrice(),
                null
            ));
        }

        return new CreateSalesRequest(
            LocalDate.now(),
            order.id(),
            shipment.id(),
            "CUS-INT-001",
            "00",
            null,
            "売上APIテスト",
            details
        );
    }

    /**
     * API 経由で売上を作成.
     */
    private SalesResponse createSalesViaApi() {
        OrderResponse order = createOrder();
        OrderDetailResponse[] orderDetails = getOrderDetails(order);
        ShipmentResponse shipment = createAndConfirmShipment(order, orderDetails);
        CreateSalesRequest salesRequest = createSalesRequest(
            order, shipment, orderDetails);
        return getRestClient()
            .post()
            .uri(API_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(salesRequest)
            .retrieve()
            .body(SalesResponse.class);
    }
}
