package com.example.sms.integration;

import com.example.sms.infrastructure.in.rest.dto.CreatePartnerRequest;
import com.example.sms.infrastructure.in.rest.dto.PartnerResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdatePartnerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 取引先 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("取引先 API 統合テスト")
class PartnerApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/v1/partners";

    @BeforeEach
    void setUp() {
        cleanupAllData();
        createPartnerGroup("GRP-001", "テストグループ");
    }

    @AfterEach
    void tearDown() {
        cleanupAllData();
    }

    @Nested
    @DisplayName("取引先登録・取得フロー")
    class PartnerCrudFlow {

        @Test
        @DisplayName("取引先を登録して取得できる")
        void shouldCreateAndRetrievePartner() {
            // Given: 取引先登録リクエスト
            CreatePartnerRequest createRequest = new CreatePartnerRequest(
                "PTN-INT-001",
                "統合テスト取引先",
                "トウゴウテストトリヒキサキ",
                true,
                false,
                "100-0001",
                "東京都千代田区",
                "1-1-1",
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal("1000000"),
                BigDecimal.ZERO
            );

            // When: 取引先を登録（実際の HTTP POST リクエスト）
            PartnerResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(PartnerResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.partnerCode()).isEqualTo("PTN-INT-001");
            assertThat(createResponse.partnerName()).isEqualTo("統合テスト取引先");

            // When: 登録した取引先を取得（実際の HTTP GET リクエスト）
            PartnerResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/PTN-INT-001")
                .retrieve()
                .body(PartnerResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.partnerCode()).isEqualTo("PTN-INT-001");
            assertThat(getResponse.partnerName()).isEqualTo("統合テスト取引先");
            assertThat(getResponse.isCustomer()).isTrue();
            assertThat(getResponse.isSupplier()).isFalse();
        }

        @Test
        @DisplayName("存在しない取引先を取得すると404エラー")
        void shouldReturn404WhenPartnerNotFound() {
            // When & Then: 存在しない取引先を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/NOT-EXIST")
                    .retrieve()
                    .body(PartnerResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("取引先更新フロー")
    class PartnerUpdateFlow {

        @Test
        @DisplayName("取引先を更新できる")
        void shouldUpdatePartner() {
            // Given: 取引先を作成
            CreatePartnerRequest createRequest = new CreatePartnerRequest(
                "PTN-UPD-001",
                "更新テスト取引先",
                "コウシンテストトリヒキサキ",
                true,
                false,
                "100-0002",
                "東京都港区",
                "2-2-2",
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal("500000"),
                BigDecimal.ZERO
            );

            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(PartnerResponse.class);

            // When: 取引先を更新
            UpdatePartnerRequest updateRequest = new UpdatePartnerRequest(
                "更新後取引先名",
                "コウシンゴトリヒキサキメイ",
                true,
                true,
                "200-0001",
                "神奈川県横浜市",
                "3-3-3",
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal("2000000"),
                new BigDecimal("500000")
            );

            PartnerResponse updateResponse = getRestClient()
                .put()
                .uri(API_PATH + "/PTN-UPD-001")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .body(PartnerResponse.class);

            // Then: 更新が反映されている
            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.partnerName()).isEqualTo("更新後取引先名");
            assertThat(updateResponse.isSupplier()).isTrue();
            assertThat(updateResponse.creditLimit()).isEqualByComparingTo(new BigDecimal("2000000"));
        }
    }

    @Nested
    @DisplayName("取引先削除フロー")
    class PartnerDeleteFlow {

        @Test
        @DisplayName("取引先を削除できる")
        void shouldDeletePartner() {
            // Given: 取引先を作成
            CreatePartnerRequest createRequest = new CreatePartnerRequest(
                "PTN-DEL-001",
                "削除テスト取引先",
                "サクジョテストトリヒキサキ",
                false,
                true,
                "300-0001",
                "埼玉県さいたま市",
                "4-4-4",
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal("300000"),
                BigDecimal.ZERO
            );

            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(PartnerResponse.class);

            // When: 取引先を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/PTN-DEL-001")
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/PTN-DEL-001")
                    .retrieve()
                    .body(PartnerResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("取引先一覧取得")
    class PartnerListFlow {

        @Test
        @DisplayName("取引先一覧を取得できる")
        void shouldGetAllPartners() {
            // Given: 複数の取引先を作成
            createTestPartner("PTN-LIST-001", "一覧テスト取引先1", 100_000);
            createTestPartner("PTN-LIST-002", "一覧テスト取引先2", 200_000);
            createTestPartner("PTN-LIST-003", "一覧テスト取引先3", 300_000);

            // When: 取引先一覧を取得
            PartnerResponse[] partners = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(PartnerResponse[].class);

            // Then: 作成した取引先が含まれている
            assertThat(partners).isNotNull();
            assertThat(partners.length).isGreaterThanOrEqualTo(3);
        }

        private void createTestPartner(String code, String name, int creditLimit) {
            CreatePartnerRequest request = new CreatePartnerRequest(
                code,
                name,
                "テストトリヒキサキ",
                true,
                false,
                "100-0001",
                "住所",
                null,
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal(creditLimit),
                BigDecimal.ZERO
            );
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PartnerResponse.class);
        }

        @Test
        @DisplayName("顧客のみをフィルタして取得できる")
        void shouldGetCustomersOnly() {
            // Given: 顧客と仕入先を作成
            CreatePartnerRequest customerRequest = new CreatePartnerRequest(
                "PTN-CUS-001",
                "顧客取引先",
                "コキャクトリヒキサキ",
                true,
                false,
                "100-0001",
                "東京都",
                null,
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal("500000"),
                BigDecimal.ZERO
            );
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(customerRequest)
                .retrieve()
                .body(PartnerResponse.class);

            CreatePartnerRequest supplierRequest = new CreatePartnerRequest(
                "PTN-SUP-001",
                "仕入先取引先",
                "シイレサキトリヒキサキ",
                false,
                true,
                "200-0001",
                "神奈川県",
                null,
                null,
                false,
                false,
                "GRP-001",
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(supplierRequest)
                .retrieve()
                .body(PartnerResponse.class);

            // When: 顧客のみを取得
            PartnerResponse[] customers = getRestClient()
                .get()
                .uri(API_PATH + "?type=customer")
                .retrieve()
                .body(PartnerResponse[].class);

            // Then: 顧客のみが含まれている
            assertThat(customers).isNotNull();
            assertThat(customers).allMatch(PartnerResponse::isCustomer);
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("取引先登録時にデータベースに正しく保存される")
        void shouldPersistPartnerToDatabase() {
            // Given: 取引先登録リクエスト
            CreatePartnerRequest createRequest = new CreatePartnerRequest(
                "PTN-DB-001",
                "DB検証用取引先",
                "DBケンショウヨウトリヒキサキ",
                true,
                true,
                "400-0001",
                "千葉県千葉市",
                "5-5-5",
                null,
                false,
                false,
                "GRP-001",
                new BigDecimal("750000"),
                new BigDecimal("100000")
            );

            // When: 取引先を登録
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(PartnerResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"取引先マスタ\" WHERE \"取引先コード\" = ?",
                Integer.class,
                "PTN-DB-001"
            );
            assertThat(count).isEqualTo(1);

            // Then: 保存された値を検証
            String partnerName = jdbcTemplate.queryForObject(
                "SELECT \"取引先名\" FROM \"取引先マスタ\" WHERE \"取引先コード\" = ?",
                String.class,
                "PTN-DB-001"
            );
            assertThat(partnerName).isEqualTo("DB検証用取引先");

            // Then: 顧客区分・仕入先区分が正しく保存されている
            Boolean isCustomer = jdbcTemplate.queryForObject(
                "SELECT \"顧客区分\" FROM \"取引先マスタ\" WHERE \"取引先コード\" = ?",
                Boolean.class,
                "PTN-DB-001"
            );
            assertThat(isCustomer).isTrue();
        }
    }
}
