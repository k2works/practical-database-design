package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.infrastructure.in.rest.dto.CreateItemRequest;
import com.example.pms.infrastructure.in.rest.dto.ItemResponse;
import com.example.pms.infrastructure.in.rest.dto.UpdateItemRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 品目 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("品目 API 統合テスト")
@SuppressWarnings("PMD.TooManyStaticImports")
class ItemApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/items";

    @BeforeEach
    void setUp() {
        // 必要なマスタデータを作成
        createUnit("個", "個", "個");
        createUnit("kg", "kg", "キログラム");
        cleanupItemData();
    }

    @AfterEach
    void tearDown() {
        cleanupItemData();
    }

    @Nested
    @DisplayName("品目登録・取得フロー")
    class ItemCrudFlow {

        @Test
        @DisplayName("品目を登録して取得できる")
        void shouldCreateAndRetrieveItem() {
            // Given: 品目登録リクエスト
            CreateItemRequest createRequest = new CreateItemRequest();
            createRequest.setItemCode("TEST001");
            createRequest.setItemName("テスト品目");
            createRequest.setItemCategory(ItemCategory.PRODUCT);
            createRequest.setUnitCode("個");

            // When: 品目を登録
            ItemResponse createResponse = getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(ItemResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getItemCode()).isEqualTo("TEST001");
            assertThat(createResponse.getItemName()).isEqualTo("テスト品目");
            assertThat(createResponse.getItemCategory()).isEqualTo(ItemCategory.PRODUCT);

            // When: 登録した品目を取得
            ItemResponse getResponse = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST001")
                    .retrieve()
                    .body(ItemResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.getItemCode()).isEqualTo("TEST001");
            assertThat(getResponse.getItemName()).isEqualTo("テスト品目");
        }

        @Test
        @DisplayName("存在しない品目を取得すると404エラー")
        void shouldReturn404WhenItemNotFound() {
            // When & Then: 存在しない品目を取得すると例外
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/NOT-EXIST")
                            .retrieve()
                            .body(ItemResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }

        @Test
        @DisplayName("重複した品目コードで登録すると409エラー")
        void shouldReturn409WhenDuplicateItemCode() {
            // Given: 品目を作成
            CreateItemRequest createRequest = new CreateItemRequest();
            createRequest.setItemCode("TEST002");
            createRequest.setItemName("テスト品目2");
            createRequest.setItemCategory(ItemCategory.PRODUCT);
            createRequest.setUnitCode("個");
            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(ItemResponse.class);

            // When & Then: 同じコードで登録すると409
            assertThatThrownBy(() ->
                    getRestClient()
                            .post()
                            .uri(API_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(createRequest)
                            .retrieve()
                            .body(ItemResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    });
        }
    }

    @Nested
    @DisplayName("品目更新フロー")
    class ItemUpdateFlow {

        @Test
        @DisplayName("品目を更新できる")
        void shouldUpdateItem() {
            // Given: 品目を作成
            CreateItemRequest createRequest = new CreateItemRequest();
            createRequest.setItemCode("TEST003");
            createRequest.setItemName("更新前品目");
            createRequest.setItemCategory(ItemCategory.PRODUCT);
            createRequest.setUnitCode("個");
            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(ItemResponse.class);

            // When: 品目を更新
            UpdateItemRequest updateRequest = new UpdateItemRequest();
            updateRequest.setItemName("更新後品目");
            updateRequest.setItemCategory(ItemCategory.SEMI_PRODUCT);

            ItemResponse updateResponse = getRestClient()
                    .put()
                    .uri(API_PATH + "/TEST003")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateRequest)
                    .retrieve()
                    .body(ItemResponse.class);

            // Then: 更新が反映されている
            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.getItemName()).isEqualTo("更新後品目");
            assertThat(updateResponse.getItemCategory()).isEqualTo(ItemCategory.SEMI_PRODUCT);
        }

        @Test
        @DisplayName("存在しない品目を更新すると404エラー")
        void shouldReturn404WhenUpdatingNonExistentItem() {
            // Given: 更新リクエスト
            UpdateItemRequest updateRequest = new UpdateItemRequest();
            updateRequest.setItemName("更新後品目");

            // When & Then: 存在しない品目を更新すると404
            assertThatThrownBy(() ->
                    getRestClient()
                            .put()
                            .uri(API_PATH + "/NOT-EXIST")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(updateRequest)
                            .retrieve()
                            .body(ItemResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("品目一覧取得")
    class ItemListFlow {

        @Test
        @DisplayName("品目一覧を取得できる")
        void shouldGetAllItems() {
            // Given: 複数の品目を作成
            createItemViaApi("TEST010", "テスト品目1", ItemCategory.PRODUCT);
            createItemViaApi("TEST011", "テスト品目2", ItemCategory.PART);
            createItemViaApi("TEST012", "テスト品目3", ItemCategory.MATERIAL);

            // When: 品目一覧を取得
            ItemResponse[] items = getRestClient()
                    .get()
                    .uri(API_PATH)
                    .retrieve()
                    .body(ItemResponse[].class);

            // Then: 作成した品目が含まれている
            assertThat(items).isNotNull();
            assertThat(items.length).isGreaterThanOrEqualTo(3);
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("品目登録時にデータベースに正しく保存される")
        void shouldPersistItemToDatabase() {
            // Given: 品目登録リクエスト
            CreateItemRequest createRequest = new CreateItemRequest();
            createRequest.setItemCode("TEST099");
            createRequest.setItemName("DB検証用品目");
            createRequest.setItemCategory(ItemCategory.PRODUCT);
            createRequest.setUnitCode("個");

            // When: 品目を登録
            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(ItemResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"品目マスタ\" WHERE \"品目コード\" = ?",
                    Integer.class,
                    "TEST099"
            );
            assertThat(count).isEqualTo(1);
        }
    }

    /**
     * API 経由で品目を作成.
     */
    private void createItemViaApi(String itemCode, String itemName, ItemCategory category) {
        CreateItemRequest request = new CreateItemRequest();
        request.setItemCode(itemCode);
        request.setItemName(itemName);
        request.setItemCategory(category);
        request.setUnitCode("個");
        getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ItemResponse.class);
    }
}
