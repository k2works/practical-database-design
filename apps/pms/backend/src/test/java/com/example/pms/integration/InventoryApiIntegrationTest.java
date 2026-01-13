package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.pms.infrastructure.in.rest.dto.InventorySummaryResponse;
import com.example.pms.infrastructure.in.rest.dto.StockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * 在庫 API 統合テスト.
 */
@DisplayName("在庫 API 統合テスト")
@SuppressWarnings("PMD.TooManyStaticImports")
class InventoryApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/inventory";

    @BeforeEach
    void setUp() {
        createUnit("個", "個", "個");
        createItem("TEST-ITEM001", "テスト品目1", "材料");
        createItem("TEST-ITEM002", "テスト品目2", "材料");
        createLocation("TEST-LOC001", "テスト倉庫");
        cleanupInventoryData();
        createInventoryData();
    }

    @AfterEach
    void tearDown() {
        cleanupInventoryData();
    }

    /**
     * 在庫テストデータをクリーンアップする.
     */
    private void cleanupInventoryData() {
        jdbcTemplate.execute("DELETE FROM \"在庫情報\" WHERE \"品目コード\" LIKE 'TEST%'");
    }

    /**
     * 在庫テストデータを作成する.
     */
    private void createInventoryData() {
        jdbcTemplate.update(
                "INSERT INTO \"在庫情報\" (\"場所コード\", \"品目コード\", \"在庫数量\", \"合格数\", \"不良数\", \"未検査数\") "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (\"場所コード\", \"品目コード\") DO UPDATE SET "
                        + "\"在庫数量\" = EXCLUDED.\"在庫数量\", \"合格数\" = EXCLUDED.\"合格数\"",
                "TEST-LOC001", "TEST-ITEM001", new BigDecimal("100"), new BigDecimal("80"), new BigDecimal("5"), new BigDecimal("15")
        );
        jdbcTemplate.update(
                "INSERT INTO \"在庫情報\" (\"場所コード\", \"品目コード\", \"在庫数量\", \"合格数\", \"不良数\", \"未検査数\") "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (\"場所コード\", \"品目コード\") DO UPDATE SET "
                        + "\"在庫数量\" = EXCLUDED.\"在庫数量\", \"合格数\" = EXCLUDED.\"合格数\"",
                "TEST-LOC001", "TEST-ITEM002", new BigDecimal("50"), new BigDecimal("50"), BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    @Nested
    @DisplayName("在庫一覧取得")
    class InventoryList {

        @Test
        @DisplayName("在庫一覧を取得できる")
        void shouldGetAllInventory() {
            StockResponse[] stocks = getRestClient()
                    .get()
                    .uri(API_PATH)
                    .retrieve()
                    .body(StockResponse[].class);

            assertThat(stocks).isNotNull();
            assertThat(stocks.length).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("品目コードで在庫をフィルタリングできる")
        void shouldFilterByItemCode() {
            StockResponse[] stocks = getRestClient()
                    .get()
                    .uri(API_PATH + "?itemCode=TEST-ITEM001")
                    .retrieve()
                    .body(StockResponse[].class);

            assertThat(stocks).isNotNull();
            assertThat(stocks).allMatch(s -> "TEST-ITEM001".equals(s.getItemCode()));
        }

        @Test
        @DisplayName("場所コードで在庫をフィルタリングできる")
        void shouldFilterByLocationCode() {
            StockResponse[] stocks = getRestClient()
                    .get()
                    .uri(API_PATH + "?locationCode=TEST-LOC001")
                    .retrieve()
                    .body(StockResponse[].class);

            assertThat(stocks).isNotNull();
            assertThat(stocks).allMatch(s -> "TEST-LOC001".equals(s.getLocationCode()));
        }
    }

    @Nested
    @DisplayName("在庫サマリー取得")
    class InventorySummary {

        @Test
        @DisplayName("在庫サマリーを取得できる")
        void shouldGetInventorySummary() {
            InventorySummaryResponse[] summaries = getRestClient()
                    .get()
                    .uri(API_PATH + "/summary")
                    .retrieve()
                    .body(InventorySummaryResponse[].class);

            assertThat(summaries).isNotNull();
        }
    }

    @Nested
    @DisplayName("在庫不足品目取得")
    class ShortageItems {

        @Test
        @DisplayName("在庫不足品目を取得できる")
        void shouldGetShortageItems() {
            InventorySummaryResponse[] shortages = getRestClient()
                    .get()
                    .uri(API_PATH + "/shortage")
                    .retrieve()
                    .body(InventorySummaryResponse[].class);

            assertThat(shortages).isNotNull();
        }
    }
}
