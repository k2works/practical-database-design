package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.pms.infrastructure.in.rest.dto.ExecuteMrpRequest;
import com.example.pms.infrastructure.in.rest.dto.MrpResultResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MRP API 統合テスト.
 */
@DisplayName("MRP API 統合テスト")
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.BigIntegerInstantiation"})
class MrpApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/mrp";

    @BeforeEach
    void setUp() {
        createUnit("個", "個", "個");
        createItem("TEST-PROD001", "テスト製品", "製品");
        createItem("TEST-SEMI001", "テスト半製品", "半製品");
        createItem("TEST-MAT001", "テスト材料1", "材料");
        createLocation("TEST-LOC001", "テスト倉庫");
        cleanupMrpData();
        createMrpTestData();
    }

    @AfterEach
    void tearDown() {
        cleanupMrpData();
    }

    /**
     * MRP テストデータをクリーンアップする.
     */
    private void cleanupMrpData() {
        jdbcTemplate.execute("DELETE FROM \"部品構成表\" WHERE \"親品目コード\" LIKE 'TEST%'");
        jdbcTemplate.execute("DELETE FROM \"在庫情報\" WHERE \"品目コード\" LIKE 'TEST%'");
    }

    /**
     * MRP テストデータを作成する.
     */
    private void createMrpTestData() {
        LocalDate today = LocalDate.now();

        // BOM: 製品 -> 半製品
        jdbcTemplate.update(
                "INSERT INTO \"部品構成表\" (\"親品目コード\", \"子品目コード\", \"適用開始日\", \"基準数量\", \"必要数量\", \"不良率\", \"工順\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "TEST-PROD001", "TEST-SEMI001", today, new BigDecimal("1"), new BigDecimal("2"), BigDecimal.ZERO, 1
        );
        // BOM: 半製品 -> 材料
        jdbcTemplate.update(
                "INSERT INTO \"部品構成表\" (\"親品目コード\", \"子品目コード\", \"適用開始日\", \"基準数量\", \"必要数量\", \"不良率\", \"工順\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "TEST-SEMI001", "TEST-MAT001", today, new BigDecimal("1"), new BigDecimal("3"), BigDecimal.ZERO, 1
        );

        // 在庫データ
        jdbcTemplate.update(
                "INSERT INTO \"在庫情報\" (\"場所コード\", \"品目コード\", \"在庫数量\", \"合格数\", \"不良数\", \"未検査数\") "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (\"場所コード\", \"品目コード\") DO UPDATE SET \"在庫数量\" = EXCLUDED.\"在庫数量\"",
                "TEST-LOC001", "TEST-MAT001", new BigDecimal("10"), new BigDecimal("10"), BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    @Nested
    @DisplayName("MRP 実行")
    class MrpExecution {

        @Test
        @DisplayName("MRP を実行できる")
        void shouldExecuteMrp() {
            ExecuteMrpRequest request = new ExecuteMrpRequest();
            request.setStartDate(LocalDate.now());
            request.setEndDate(LocalDate.now().plusDays(30));

            MrpResultResponse response = getRestClient()
                    .post()
                    .uri(API_PATH + "/execute")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MrpResultResponse.class);

            assertThat(response).isNotNull();
            assertThat(response.getExecutionTime()).isNotNull();
            assertThat(response.getPeriodStart()).isEqualTo(LocalDate.now());
            assertThat(response.getPeriodEnd()).isEqualTo(LocalDate.now().plusDays(30));
            assertThat(response.getPlannedOrders()).isNotNull();
            assertThat(response.getShortageItems()).isNotNull();
        }
    }

    @Nested
    @DisplayName("MRP 結果照会")
    class MrpResults {

        @Test
        @DisplayName("MRP 結果照会エンドポイントが存在する")
        void shouldHaveResultsEndpoint() {
            String response = getRestClient()
                    .get()
                    .uri(API_PATH + "/results")
                    .retrieve()
                    .body(String.class);

            // 未実装のため "未実装" 文字列が返る
            assertThat(response).isEqualTo("未実装");
        }
    }

    @Nested
    @DisplayName("計画オーダ一覧")
    class PlannedOrders {

        @Test
        @DisplayName("計画オーダ一覧エンドポイントが存在する")
        void shouldHavePlannedOrdersEndpoint() {
            String response = getRestClient()
                    .get()
                    .uri(API_PATH + "/planned-orders")
                    .retrieve()
                    .body(String.class);

            // 未実装のため "未実装" 文字列が返る
            assertThat(response).isEqualTo("未実装");
        }
    }
}
