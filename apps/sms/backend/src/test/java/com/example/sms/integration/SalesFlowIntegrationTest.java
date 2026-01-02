package com.example.sms.integration;

import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesStatus;
import com.example.sms.domain.model.shipping.ShipmentStatus;
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
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 販売フロー統合テスト.
 * 受注から出荷、売上までの一連のフローをテストする。
 */
@DisplayName("販売フロー統合テスト")
class SalesFlowIntegrationTest extends IntegrationTestBase {

    private static final String ORDERS_API = "/api/v1/orders";
    private static final String SHIPMENTS_API = "/api/v1/shipments";
    private static final String SALES_API = "/api/v1/sales";

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
    @DisplayName("受注から売上までの一連のフロー")
    class CompleteSalesFlow {

        @Test
        @DisplayName("受注から出荷、売上までの一連のフローが正常に動作する")
        void shouldCompleteSalesFlow() {
            // ============================================
            // 1. 受注登録
            // ============================================
            CreateOrderRequest orderRequest = createOrderRequest();
            OrderResponse orderResponse = getRestClient()
                .post()
                .uri(ORDERS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .body(OrderResponse.class);

            assertThat(orderResponse).isNotNull();
            assertThat(orderResponse.orderNumber()).isNotNull();
            assertThat(orderResponse.status()).isEqualTo(OrderStatus.RECEIVED);

            // 受注明細を取得
            OrderDetailResponse[] orderDetails = getRestClient()
                .get()
                .uri(ORDERS_API + "/" + orderResponse.orderNumber() + "/details")
                .retrieve()
                .body(OrderDetailResponse[].class);

            assertThat(orderDetails).isNotNull();
            assertThat(orderDetails.length).isEqualTo(2);

            // ============================================
            // 2. 出荷登録
            // ============================================
            CreateShipmentRequest shipmentRequest = createShipmentRequest(
                orderResponse, orderDetails);
            ShipmentResponse shipmentResponse = getRestClient()
                .post()
                .uri(SHIPMENTS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRequest)
                .retrieve()
                .body(ShipmentResponse.class);

            assertThat(shipmentResponse).isNotNull();
            assertThat(shipmentResponse.shipmentNumber()).isNotNull();
            assertThat(shipmentResponse.orderId()).isEqualTo(orderResponse.id());
            assertThat(shipmentResponse.status()).isEqualTo(ShipmentStatus.INSTRUCTED);

            // ============================================
            // 3. 出荷確定
            // ============================================
            ShipmentResponse confirmedShipment = getRestClient()
                .post()
                .uri(SHIPMENTS_API + "/" + shipmentResponse.shipmentNumber() + "/confirm")
                .retrieve()
                .body(ShipmentResponse.class);

            assertThat(confirmedShipment).isNotNull();
            assertThat(confirmedShipment.status()).isEqualTo(ShipmentStatus.SHIPPED);

            // ============================================
            // 4. 売上登録
            // ============================================
            CreateSalesRequest salesRequest = createSalesRequest(
                orderResponse, shipmentResponse, orderDetails);
            SalesResponse salesResponse = getRestClient()
                .post()
                .uri(SALES_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(salesRequest)
                .retrieve()
                .body(SalesResponse.class);

            assertThat(salesResponse).isNotNull();
            assertThat(salesResponse.salesNumber()).isNotNull();
            assertThat(salesResponse.orderId()).isEqualTo(orderResponse.id());
            assertThat(salesResponse.shipmentId()).isEqualTo(shipmentResponse.id());
            assertThat(salesResponse.status()).isEqualTo(SalesStatus.RECORDED);

            // ============================================
            // 5. データベース状態の検証
            // ============================================
            verifyDatabaseState(orderResponse, shipmentResponse, salesResponse);
        }

        @Test
        @DisplayName("売上をキャンセルできる")
        void shouldCancelSales() {
            // Given: 受注から売上までのフローを実行
            CreateOrderRequest orderRequest = createOrderRequest();
            OrderResponse orderResponse = getRestClient()
                .post()
                .uri(ORDERS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .body(OrderResponse.class);

            // 受注明細を取得
            OrderDetailResponse[] orderDetails = getRestClient()
                .get()
                .uri(ORDERS_API + "/" + orderResponse.orderNumber() + "/details")
                .retrieve()
                .body(OrderDetailResponse[].class);

            CreateShipmentRequest shipmentRequest = createShipmentRequest(
                orderResponse, orderDetails);
            ShipmentResponse shipmentResponse = getRestClient()
                .post()
                .uri(SHIPMENTS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRequest)
                .retrieve()
                .body(ShipmentResponse.class);

            getRestClient()
                .post()
                .uri(SHIPMENTS_API + "/" + shipmentResponse.shipmentNumber() + "/confirm")
                .retrieve()
                .body(ShipmentResponse.class);

            CreateSalesRequest salesRequest = createSalesRequest(
                orderResponse, shipmentResponse, orderDetails);
            SalesResponse salesResponse = getRestClient()
                .post()
                .uri(SALES_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(salesRequest)
                .retrieve()
                .body(SalesResponse.class);

            // When: 売上をキャンセル
            SalesResponse cancelledSales = getRestClient()
                .post()
                .uri(SALES_API + "/" + salesResponse.salesNumber() + "/cancel")
                .retrieve()
                .body(SalesResponse.class);

            // Then: キャンセル成功
            assertThat(cancelledSales).isNotNull();
            assertThat(cancelledSales.status()).isEqualTo(SalesStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("データベース整合性の検証")
    class DatabaseIntegrity {

        @Test
        @DisplayName("トランザクションデータの整合性が保たれる")
        void shouldMaintainDataIntegrity() {
            // Given: 受注を作成
            CreateOrderRequest orderRequest = createOrderRequest();
            OrderResponse orderResponse = getRestClient()
                .post()
                .uri(ORDERS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .body(OrderResponse.class);

            // When: 受注に関連するデータを取得
            Integer orderCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"受注データ\" WHERE \"ID\" = ?",
                Integer.class, orderResponse.id());
            Integer detailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"受注明細\" WHERE \"受注ID\" = ?",
                Integer.class, orderResponse.id());

            // Then: データが正しく保存されている
            assertThat(orderCount).isEqualTo(1);
            assertThat(detailCount).isEqualTo(2);

            // When: 受注を削除
            getRestClient()
                .delete()
                .uri(ORDERS_API + "/" + orderResponse.orderNumber())
                .retrieve()
                .toBodilessEntity();

            // Then: 関連する明細も削除されている（CASCADE）
            Integer orderCountAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"受注データ\" WHERE \"ID\" = ?",
                Integer.class, orderResponse.id());
            Integer detailCountAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"受注明細\" WHERE \"受注ID\" = ?",
                Integer.class, orderResponse.id());

            assertThat(orderCountAfter).isEqualTo(0);
            assertThat(detailCountAfter).isEqualTo(0);
        }
    }

    /**
     * データベース状態を検証.
     */
    private void verifyDatabaseState(
            OrderResponse order, ShipmentResponse shipment, SalesResponse sales) {
        // 受注データの検証
        Integer orderCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"受注データ\" WHERE \"ID\" = ?",
            Integer.class, order.id());
        assertThat(orderCount).isEqualTo(1);

        // 出荷データの検証
        Integer shipmentCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"出荷データ\" WHERE \"ID\" = ?",
            Integer.class, shipment.id());
        assertThat(shipmentCount).isEqualTo(1);

        // 売上データの検証
        Integer salesCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"売上データ\" WHERE \"ID\" = ?",
            Integer.class, sales.id());
        assertThat(salesCount).isEqualTo(1);
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

        return new CreateOrderRequest(
            LocalDate.now(),
            "CUS-INT-001",
            "00",
            null,
            null,
            LocalDate.now().plusDays(7),
            LocalDate.now().plusDays(5),
            null,
            null,
            "販売フローテスト",
            details
        );
    }

    /**
     * テスト用の出荷登録リクエストを作成（注文明細を使用）.
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
            "出荷テスト",
            details
        );
    }

    /**
     * テスト用の売上登録リクエストを作成（注文明細を使用）.
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
            "売上テスト",
            details
        );
    }
}
