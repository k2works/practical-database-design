package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.infrastructure.in.rest.dto.CreatePurchaseOrderRequest;
import com.example.pms.infrastructure.in.rest.dto.PurchaseOrderResponse;
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

/**
 * 発注 API 統合テスト.
 */
@DisplayName("発注 API 統合テスト")
@SuppressWarnings("PMD.TooManyStaticImports")
class PurchaseOrderApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/purchase-orders";

    @BeforeEach
    void setUp() {
        createUnit("個", "個", "個");
        createSupplier("TEST-SUP001", "テスト仕入先");
        createItem("TEST-ITEM001", "テスト品目1", "材料");
        createItem("TEST-ITEM002", "テスト品目2", "材料");
        createLocation("TEST-LOC001", "テスト倉庫");
        cleanupPurchaseOrderData();
    }

    @AfterEach
    void tearDown() {
        cleanupPurchaseOrderData();
    }

    @Nested
    @DisplayName("発注登録・取得フロー")
    class PurchaseOrderCrudFlow {

        @Test
        @DisplayName("発注を登録して取得できる")
        void shouldCreateAndRetrievePurchaseOrder() {
            CreatePurchaseOrderRequest request = createPurchaseOrderRequest(
                    "TEST-SUP001", "TEST-ITEM001", new BigDecimal("100"), null);

            PurchaseOrderResponse createResponse = getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PurchaseOrderResponse.class);

            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getPurchaseOrderNumber()).isNotNull();
            assertThat(createResponse.getSupplierCode()).isEqualTo("TEST-SUP001");
            assertThat(createResponse.getStatus()).isEqualTo(PurchaseOrderStatus.CREATING);
            assertThat(createResponse.getDetails()).hasSize(1);
        }

        @Test
        @DisplayName("存在しない発注を取得すると404エラー")
        void shouldReturn404WhenOrderNotFound() {
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/NOT-EXIST-ORDER")
                            .retrieve()
                            .body(PurchaseOrderResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    private CreatePurchaseOrderRequest createPurchaseOrderRequest(
            String supplierCode, String itemCode, BigDecimal quantity, String remarks) {
        CreatePurchaseOrderRequest request = new CreatePurchaseOrderRequest();
        request.setSupplierCode(supplierCode);
        request.setRemarks(remarks);

        CreatePurchaseOrderRequest.PurchaseOrderDetailRequest detail =
                new CreatePurchaseOrderRequest.PurchaseOrderDetailRequest();
        detail.setItemCode(itemCode);
        detail.setOrderQuantity(quantity);
        detail.setExpectedReceivingDate(LocalDate.now().plusDays(7));
        detail.setDeliveryLocationCode("TEST-LOC001");
        request.setDetails(List.of(detail));
        return request;
    }
}
