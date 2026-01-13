package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.pms.domain.model.process.WorkOrderStatus;
import com.example.pms.infrastructure.in.rest.dto.CreateWorkOrderRequest;
import com.example.pms.infrastructure.in.rest.dto.WorkOrderResponse;
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

/**
 * 作業指示 API 統合テスト.
 */
@DisplayName("作業指示 API 統合テスト")
@SuppressWarnings("PMD.TooManyStaticImports")
class WorkOrderApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/work-orders";

    @BeforeEach
    void setUp() {
        createUnit("個", "個", "個");
        createItem("TEST-ITEM001", "テスト品目1", "製品");
        createItem("TEST-ITEM002", "テスト品目2", "半製品");
        createLocation("TEST-LOC001", "テスト倉庫");
        cleanupWorkOrderData();
        cleanupOrderData();
        // 作業指示には元となるオーダが必要
        createOrder("TEST-ORD001", "TEST-ITEM001", "TEST-LOC001");
        createOrder("TEST-ORD002", "TEST-ITEM002", "TEST-LOC001");
    }

    @AfterEach
    void tearDown() {
        cleanupWorkOrderData();
        cleanupOrderData();
    }

    @Nested
    @DisplayName("作業指示登録・取得フロー")
    class WorkOrderCrudFlow {

        @Test
        @DisplayName("作業指示を登録して取得できる")
        void shouldCreateAndRetrieveWorkOrder() {
            CreateWorkOrderRequest request = createWorkOrderRequest("TEST-ITEM001", new BigDecimal("100"));

            WorkOrderResponse createResponse = getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(WorkOrderResponse.class);

            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getWorkOrderNumber()).isNotNull();
            assertThat(createResponse.getItemCode()).isEqualTo("TEST-ITEM001");
            assertThat(createResponse.getOrderQuantity()).isEqualByComparingTo(new BigDecimal("100"));
            assertThat(createResponse.getStatus()).isEqualTo(WorkOrderStatus.NOT_STARTED);
        }

        @Test
        @DisplayName("存在しない作業指示を取得すると404エラー")
        void shouldReturn404WhenWorkOrderNotFound() {
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/NOT-EXIST-WO")
                            .retrieve()
                            .body(WorkOrderResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("作業指示一覧取得")
    class WorkOrderList {

        @Test
        @DisplayName("作業指示一覧を取得できる")
        void shouldGetAllWorkOrders() {
            // 2つの作業指示を作成
            CreateWorkOrderRequest request1 = createWorkOrderRequest("TEST-ITEM001", new BigDecimal("100"));
            CreateWorkOrderRequest request2 = createWorkOrderRequest("TEST-ITEM002", new BigDecimal("200"));

            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request1)
                    .retrieve()
                    .body(WorkOrderResponse.class);

            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request2)
                    .retrieve()
                    .body(WorkOrderResponse.class);

            // 一覧取得
            WorkOrderResponse[] workOrders = getRestClient()
                    .get()
                    .uri(API_PATH)
                    .retrieve()
                    .body(WorkOrderResponse[].class);

            assertThat(workOrders).isNotNull();
            assertThat(workOrders.length).isGreaterThanOrEqualTo(2);
        }
    }

    private CreateWorkOrderRequest createWorkOrderRequest(String itemCode, BigDecimal quantity) {
        CreateWorkOrderRequest request = new CreateWorkOrderRequest();
        // 事前に作成したオーダ番号を使用（品目に応じて選択）
        String orderNo = "TEST-ITEM001".equals(itemCode) ? "TEST-ORD001" : "TEST-ORD002";
        request.setOrderNumber(orderNo);
        request.setItemCode(itemCode);
        request.setOrderQuantity(quantity);
        request.setLocationCode("TEST-LOC001");
        request.setPlannedStartDate(LocalDate.now());
        request.setPlannedEndDate(LocalDate.now().plusDays(7));
        return request;
    }
}
