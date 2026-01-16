package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.pms.infrastructure.in.rest.dto.BomExplosionResponse;
import com.example.pms.infrastructure.in.rest.dto.BomResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * BOM API 統合テスト.
 */
@DisplayName("BOM API 統合テスト")
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.BigIntegerInstantiation"})
class BomApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/bom";

    @BeforeEach
    void setUp() {
        createUnit("個", "個", "個");
        // 製品 -> 半製品 -> 材料 の構成
        createItem("TEST-PROD001", "テスト製品", "製品");
        createItem("TEST-SEMI001", "テスト半製品", "半製品");
        createItem("TEST-MAT001", "テスト材料1", "材料");
        createItem("TEST-MAT002", "テスト材料2", "材料");
        cleanupBomData();
        createBomData();
    }

    @AfterEach
    void tearDown() {
        cleanupBomData();
    }

    /**
     * BOM テストデータをクリーンアップする.
     */
    private void cleanupBomData() {
        jdbcTemplate.execute("DELETE FROM \"部品構成表\" WHERE \"親品目コード\" LIKE 'TEST%'");
    }

    /**
     * BOM テストデータを作成する.
     */
    private void createBomData() {
        LocalDate today = LocalDate.now();
        // 製品 -> 半製品
        jdbcTemplate.update(
                "INSERT INTO \"部品構成表\" (\"親品目コード\", \"子品目コード\", \"適用開始日\", \"基準数量\", \"必要数量\", \"不良率\", \"工順\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "TEST-PROD001", "TEST-SEMI001", today, new BigDecimal("1"), new BigDecimal("2"), BigDecimal.ZERO, 1
        );
        // 半製品 -> 材料1
        jdbcTemplate.update(
                "INSERT INTO \"部品構成表\" (\"親品目コード\", \"子品目コード\", \"適用開始日\", \"基準数量\", \"必要数量\", \"不良率\", \"工順\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "TEST-SEMI001", "TEST-MAT001", today, new BigDecimal("1"), new BigDecimal("3"), BigDecimal.ZERO, 1
        );
        // 半製品 -> 材料2
        jdbcTemplate.update(
                "INSERT INTO \"部品構成表\" (\"親品目コード\", \"子品目コード\", \"適用開始日\", \"基準数量\", \"必要数量\", \"不良率\", \"工順\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "TEST-SEMI001", "TEST-MAT002", today, new BigDecimal("1"), new BigDecimal("1"), BigDecimal.ZERO, 2
        );
    }

    @Nested
    @DisplayName("BOM 取得")
    class BomRetrieval {

        @Test
        @DisplayName("親品目コードで BOM を取得できる")
        void shouldGetBomByParentItem() {
            BomResponse[] boms = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST-PROD001")
                    .retrieve()
                    .body(BomResponse[].class);

            assertThat(boms).isNotNull();
            assertThat(boms).hasSize(1);
            assertThat(boms[0].getParentItemCode()).isEqualTo("TEST-PROD001");
            assertThat(boms[0].getChildItemCode()).isEqualTo("TEST-SEMI001");
        }

        @Test
        @DisplayName("複数の子品目を持つ BOM を取得できる")
        void shouldGetBomWithMultipleChildren() {
            BomResponse[] boms = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST-SEMI001")
                    .retrieve()
                    .body(BomResponse[].class);

            assertThat(boms).isNotNull();
            assertThat(boms).hasSize(2);
            assertThat(boms).allMatch(b -> "TEST-SEMI001".equals(b.getParentItemCode()));
        }
    }

    @Nested
    @DisplayName("BOM 展開（部品展開）")
    class BomExplosion {

        @Test
        @DisplayName("BOM を展開できる")
        void shouldExplodeBom() {
            BomExplosionResponse[] explosions = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST-PROD001/explode?quantity=1")
                    .retrieve()
                    .body(BomExplosionResponse[].class);

            assertThat(explosions).isNotNull();
            // 製品から展開すると、半製品、材料1、材料2 が展開される
            assertThat(explosions.length).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("指定数量で BOM を展開できる")
        void shouldExplodeBomWithQuantity() {
            BomExplosionResponse[] explosions = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST-PROD001/explode?quantity=10")
                    .retrieve()
                    .body(BomExplosionResponse[].class);

            assertThat(explosions).isNotNull();
        }
    }

    @Nested
    @DisplayName("逆展開（使用先照会）")
    class WhereUsed {

        @Test
        @DisplayName("使用先を照会できる")
        void shouldGetWhereUsed() {
            BomResponse[] usages = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST-SEMI001/where-used")
                    .retrieve()
                    .body(BomResponse[].class);

            assertThat(usages).isNotNull();
            assertThat(usages).hasSize(1);
            assertThat(usages[0].getParentItemCode()).isEqualTo("TEST-PROD001");
        }

        @Test
        @DisplayName("材料の使用先を照会できる")
        void shouldGetWhereUsedForMaterial() {
            BomResponse[] usages = getRestClient()
                    .get()
                    .uri(API_PATH + "/TEST-MAT001/where-used")
                    .retrieve()
                    .body(BomResponse[].class);

            assertThat(usages).isNotNull();
            assertThat(usages).hasSize(1);
            assertThat(usages[0].getParentItemCode()).isEqualTo("TEST-SEMI001");
        }
    }
}
