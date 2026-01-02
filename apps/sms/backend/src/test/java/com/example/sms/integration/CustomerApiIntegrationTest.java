package com.example.sms.integration;

import com.example.sms.domain.model.partner.BillingType;
import com.example.sms.infrastructure.in.rest.dto.CreateCustomerRequest;
import com.example.sms.infrastructure.in.rest.dto.CustomerResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateCustomerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 顧客 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("顧客 API 統合テスト")
class CustomerApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/v1/customers";

    @BeforeEach
    void setUp() {
        cleanupAllData();
        // 顧客作成には取引先が必要
        createPartnerAsCustomer("CUS-INT-001", "統合テスト顧客取引先");
    }

    @AfterEach
    void tearDown() {
        cleanupAllData();
    }

    @Nested
    @DisplayName("顧客登録・取得フロー")
    class CustomerCrudFlow {

        @Test
        @DisplayName("顧客を登録して取得できる")
        void shouldCreateAndRetrieveCustomer() {
            // Given: 顧客登録リクエスト
            CreateCustomerRequest createRequest = createCustomerRequest(
                "CUS-INT-001", "00", "統合テスト顧客");

            // When: 顧客を登録
            CustomerResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(CustomerResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.customerCode()).isEqualTo("CUS-INT-001");
            assertThat(createResponse.customerBranchNumber()).isEqualTo("00");
            assertThat(createResponse.customerName()).isEqualTo("統合テスト顧客");

            // When: 登録した顧客を取得
            CustomerResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/CUS-INT-001/00")
                .retrieve()
                .body(CustomerResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.customerCode()).isEqualTo("CUS-INT-001");
            assertThat(getResponse.customerBranchNumber()).isEqualTo("00");
        }

        @Test
        @DisplayName("存在しない顧客を取得すると404エラー")
        void shouldReturn404WhenCustomerNotFound() {
            // When & Then: 存在しない顧客を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/NOT-EXIST/00")
                    .retrieve()
                    .body(CustomerResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("顧客更新フロー")
    class CustomerUpdateFlow {

        @Test
        @DisplayName("顧客を更新できる")
        void shouldUpdateCustomer() {
            // Given: 顧客を作成
            CreateCustomerRequest createRequest = createCustomerRequest(
                "CUS-INT-001", "00", "更新前顧客");
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(CustomerResponse.class);

            // When: 顧客を更新
            UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
                "一般顧客",
                null, null, null, null,
                "更新後顧客",
                "コウシンゴコキャク",
                null, null, null,
                "200-0002",
                "神奈川県",
                "横浜市中区",
                null, null, null, null,
                BillingType.PERIODIC,
                25, 1, 10, null,
                null, null, null, null
            );

            CustomerResponse updateResponse = getRestClient()
                .put()
                .uri(API_PATH + "/CUS-INT-001/00")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .body(CustomerResponse.class);

            // Then: 更新が反映されている
            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.customerName()).isEqualTo("更新後顧客");
            assertThat(updateResponse.customerPostalCode()).isEqualTo("200-0002");
        }
    }

    @Nested
    @DisplayName("顧客削除フロー")
    class CustomerDeleteFlow {

        @Test
        @DisplayName("顧客を削除できる")
        void shouldDeleteCustomer() {
            // Given: 顧客を作成
            CreateCustomerRequest createRequest = createCustomerRequest(
                "CUS-INT-001", "00", "削除テスト顧客");
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(CustomerResponse.class);

            // When: 顧客を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/CUS-INT-001/00")
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/CUS-INT-001/00")
                    .retrieve()
                    .body(CustomerResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("顧客一覧取得")
    class CustomerListFlow {

        @Test
        @DisplayName("顧客一覧を取得できる")
        void shouldGetAllCustomers() {
            // Given: 複数の顧客を作成
            createPartnerAsCustomer("CUS-INT-002", "統合テスト顧客2");
            createPartnerAsCustomer("CUS-INT-003", "統合テスト顧客3");

            createCustomerViaApi("CUS-INT-001", "00", "顧客1");
            createCustomerViaApi("CUS-INT-002", "00", "顧客2");
            createCustomerViaApi("CUS-INT-003", "00", "顧客3");

            // When: 顧客一覧を取得
            CustomerResponse[] customers = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(CustomerResponse[].class);

            // Then: 作成した顧客が含まれている
            assertThat(customers).isNotNull();
            assertThat(customers.length).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("顧客コードで一覧を取得できる")
        void shouldGetCustomersByCode() {
            // Given: 同じ顧客コードで複数の枝番を作成
            createCustomerViaApi("CUS-INT-001", "00", "顧客本店");
            createCustomerViaApi("CUS-INT-001", "01", "顧客支店1");

            // When: 顧客コードで一覧を取得
            CustomerResponse[] customers = getRestClient()
                .get()
                .uri(API_PATH + "/CUS-INT-001")
                .retrieve()
                .body(CustomerResponse[].class);

            // Then: 同じ顧客コードの顧客が取得できる
            assertThat(customers).isNotNull();
            assertThat(customers.length).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("顧客登録時にデータベースに正しく保存される")
        void shouldPersistCustomerToDatabase() {
            // Given: 顧客登録リクエスト
            CreateCustomerRequest createRequest = createCustomerRequest(
                "CUS-INT-001", "00", "DB検証用顧客");

            // When: 顧客を登録
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(CustomerResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"顧客マスタ\" WHERE \"顧客コード\" = ? AND \"顧客枝番\" = ?",
                Integer.class,
                "CUS-INT-001", "00"
            );
            assertThat(count).isEqualTo(1);
        }
    }

    /**
     * テスト用の顧客登録リクエストを作成.
     */
    private CreateCustomerRequest createCustomerRequest(
            String customerCode, String branchNumber, String customerName) {
        return new CreateCustomerRequest(
            customerCode,
            branchNumber,
            "一般顧客",
            null, null, null, null,
            customerName,
            "トウゴウテストコキャク",
            null, null, null,
            "100-0001",
            "東京都",
            "千代田区",
            null, null, null, null,
            BillingType.PERIODIC,
            20, 1, 25, null,
            null, null, null, null
        );
    }

    /**
     * API 経由で顧客を作成.
     */
    private void createCustomerViaApi(
            String customerCode, String branchNumber, String customerName) {
        CreateCustomerRequest request = createCustomerRequest(
            customerCode, branchNumber, customerName);
        getRestClient()
            .post()
            .uri(API_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(CustomerResponse.class);
    }
}
