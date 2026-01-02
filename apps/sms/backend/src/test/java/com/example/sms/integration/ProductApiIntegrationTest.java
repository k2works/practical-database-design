package com.example.sms.integration;

import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.infrastructure.in.rest.dto.CreateProductRequest;
import com.example.sms.infrastructure.in.rest.dto.ProductResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateProductRequest;
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
 * 商品 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("商品 API 統合テスト")
class ProductApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/v1/products";

    @BeforeEach
    void setUp() {
        cleanupAllData();
        createProductClassification("CAT-BEEF", "牛肉");
        createProductClassification("CAT-PORK", "豚肉");
    }

    @AfterEach
    void tearDown() {
        cleanupAllData();
    }

    @Nested
    @DisplayName("商品登録・取得フロー")
    class ProductCrudFlow {

        @Test
        @DisplayName("商品を登録して取得できる")
        void shouldCreateAndRetrieveProduct() {
            // Given: 商品登録リクエスト
            CreateProductRequest createRequest = new CreateProductRequest(
                "BEEF-INT-001",
                "統合テスト用商品正式名",
                "統合テスト用商品",
                "トウゴウテストヨウショウヒン",
                ProductCategory.PRODUCT,
                "MODEL-001",
                new BigDecimal("5000"),
                new BigDecimal("3000"),
                TaxCategory.EXCLUSIVE,
                "CAT-BEEF",
                false,
                true,
                true,
                null,
                null
            );

            // When: 商品を登録（実際の HTTP POST リクエスト）
            ProductResponse createResponse = getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ProductResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.productCode()).isEqualTo("BEEF-INT-001");
            assertThat(createResponse.productName()).isEqualTo("統合テスト用商品");

            // When: 登録した商品を取得（実際の HTTP GET リクエスト）
            ProductResponse getResponse = getRestClient()
                .get()
                .uri(API_PATH + "/BEEF-INT-001")
                .retrieve()
                .body(ProductResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.productCode()).isEqualTo("BEEF-INT-001");
            assertThat(getResponse.productName()).isEqualTo("統合テスト用商品");
            assertThat(getResponse.sellingPrice()).isEqualByComparingTo(new BigDecimal("5000"));
            assertThat(getResponse.purchasePrice()).isEqualByComparingTo(new BigDecimal("3000"));
        }

        @Test
        @DisplayName("存在しない商品を取得すると404エラー")
        void shouldReturn404WhenProductNotFound() {
            // When & Then: 存在しない商品を取得すると例外
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/NOT-EXIST")
                    .retrieve()
                    .body(ProductResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("商品更新フロー")
    class ProductUpdateFlow {

        @Test
        @DisplayName("商品を更新できる")
        void shouldUpdateProduct() {
            // Given: 商品を作成
            CreateProductRequest createRequest = new CreateProductRequest(
                "BEEF-UPD-001",
                "更新テスト商品正式名",
                "更新テスト商品",
                "コウシンテストショウヒン",
                ProductCategory.PRODUCT,
                "MODEL-UPD",
                new BigDecimal("4000"),
                new BigDecimal("2500"),
                TaxCategory.EXCLUSIVE,
                "CAT-BEEF",
                false,
                true,
                true,
                null,
                null
            );

            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ProductResponse.class);

            // When: 商品を更新
            UpdateProductRequest updateRequest = new UpdateProductRequest(
                "更新後商品正式名",
                "更新後商品名",
                "コウシンゴショウヒンメイ",
                ProductCategory.PRODUCT,
                "MODEL-UPD-2",
                new BigDecimal("6000"),
                new BigDecimal("4000"),
                TaxCategory.EXCLUSIVE,
                "CAT-PORK",
                false,
                true,
                true,
                null,
                null
            );

            ProductResponse updateResponse = getRestClient()
                .put()
                .uri(API_PATH + "/BEEF-UPD-001")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .body(ProductResponse.class);

            // Then: 更新が反映されている
            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.productName()).isEqualTo("更新後商品名");
            assertThat(updateResponse.sellingPrice()).isEqualByComparingTo(new BigDecimal("6000"));
            assertThat(updateResponse.classificationCode()).isEqualTo("CAT-PORK");
        }
    }

    @Nested
    @DisplayName("商品削除フロー")
    class ProductDeleteFlow {

        @Test
        @DisplayName("商品を削除できる")
        void shouldDeleteProduct() {
            // Given: 商品を作成
            CreateProductRequest createRequest = new CreateProductRequest(
                "BEEF-DEL-001",
                "削除テスト商品正式名",
                "削除テスト商品",
                "サクジョテストショウヒン",
                ProductCategory.PRODUCT,
                "MODEL-DEL",
                new BigDecimal("3000"),
                new BigDecimal("2000"),
                TaxCategory.EXCLUSIVE,
                "CAT-BEEF",
                false,
                true,
                true,
                null,
                null
            );

            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ProductResponse.class);

            // When: 商品を削除
            getRestClient()
                .delete()
                .uri(API_PATH + "/BEEF-DEL-001")
                .retrieve()
                .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                getRestClient()
                    .get()
                    .uri(API_PATH + "/BEEF-DEL-001")
                    .retrieve()
                    .body(ProductResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
             .satisfies(ex -> {
                 HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                 assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
             });
        }
    }

    @Nested
    @DisplayName("商品一覧取得")
    class ProductListFlow {

        @Test
        @DisplayName("商品一覧を取得できる")
        void shouldGetAllProducts() {
            // Given: 複数の商品を作成
            createTestProduct("BEEF-LIST-001", "一覧テスト商品1", 1000, 500);
            createTestProduct("BEEF-LIST-002", "一覧テスト商品2", 2000, 1000);
            createTestProduct("BEEF-LIST-003", "一覧テスト商品3", 3000, 1500);

            // When: 商品一覧を取得
            ProductResponse[] products = getRestClient()
                .get()
                .uri(API_PATH)
                .retrieve()
                .body(ProductResponse[].class);

            // Then: 作成した商品が含まれている
            assertThat(products).isNotNull();
            assertThat(products.length).isGreaterThanOrEqualTo(3);
        }

        private void createTestProduct(
                String code, String name, int sellingPrice, int purchasePrice) {
            CreateProductRequest request = new CreateProductRequest(
                code,
                name + "正式名",
                name,
                "テストショウヒン",
                ProductCategory.PRODUCT,
                "MODEL-LIST",
                new BigDecimal(sellingPrice),
                new BigDecimal(purchasePrice),
                TaxCategory.EXCLUSIVE,
                "CAT-BEEF",
                false,
                true,
                true,
                null,
                null
            );
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ProductResponse.class);
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("商品登録時にデータベースに正しく保存される")
        void shouldPersistProductToDatabase() {
            // Given: 商品登録リクエスト
            CreateProductRequest createRequest = new CreateProductRequest(
                "BEEF-DB-001",
                "DB検証用商品正式名",
                "DB検証用商品",
                "DBケンショウヨウショウヒン",
                ProductCategory.PRODUCT,
                "MODEL-DB-001",
                new BigDecimal("7000"),
                new BigDecimal("4500"),
                TaxCategory.EXCLUSIVE,
                "CAT-BEEF",
                false,
                true,
                true,
                null,
                null
            );

            // When: 商品を登録
            getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest)
                .retrieve()
                .body(ProductResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"商品マスタ\" WHERE \"商品コード\" = ?",
                Integer.class,
                "BEEF-DB-001"
            );
            assertThat(count).isEqualTo(1);

            // Then: 保存された値を検証
            String productName = jdbcTemplate.queryForObject(
                "SELECT \"商品名\" FROM \"商品マスタ\" WHERE \"商品コード\" = ?",
                String.class,
                "BEEF-DB-001"
            );
            assertThat(productName).isEqualTo("DB検証用商品");
        }
    }
}
