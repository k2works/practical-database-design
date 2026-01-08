package com.example.fas.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 残高照会 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("残高照会 API 統合テスト")
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.AvoidDuplicateLiterals"})
class BalanceApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/balances";

    @BeforeEach
    void setUp() {
        // トランザクションデータのみクリーンアップ（勘定科目はSeedデータを使用）
        cleanupTransactionData();
        // 月次残高テストデータをセットアップ
        setupMonthlyBalanceTestData();
    }

    @AfterEach
    void tearDown() {
        cleanupTransactionData();
    }

    /**
     * 月次残高テストデータをセットアップ.
     */
    private void setupMonthlyBalanceTestData() {
        // 2025年1月の月次残高データを作成
        insertMonthlyBalance(2025, 1, "11110",
                new BigDecimal("100000"), new BigDecimal("50000"),
                new BigDecimal("30000"), new BigDecimal("120000"));
        insertMonthlyBalance(2025, 1, "41110",
                BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("80000"), new BigDecimal("80000"));
        insertMonthlyBalance(2025, 1, "51100",
                BigDecimal.ZERO, new BigDecimal("40000"),
                BigDecimal.ZERO, new BigDecimal("40000"));

        // 2025年2月の月次残高データを作成
        insertMonthlyBalance(2025, 2, "11110",
                new BigDecimal("120000"), new BigDecimal("60000"),
                new BigDecimal("25000"), new BigDecimal("155000"));
    }

    /**
     * 月次残高データを挿入.
     */
    private void insertMonthlyBalance(int fiscalYear, int month, String accountCode,
            BigDecimal openingBalance, BigDecimal debitAmount,
            BigDecimal creditAmount, BigDecimal closingBalance) {
        jdbcTemplate.update(
                "INSERT INTO \"月次勘定科目残高\" "
                        + "(\"決算期\", \"月度\", \"勘定科目コード\", \"補助科目コード\", "
                        + "\"部門コード\", \"プロジェクトコード\", \"決算仕訳フラグ\", "
                        + "\"月初残高\", \"借方金額\", \"貸方金額\", \"月末残高\", "
                        + "\"バージョン\", \"作成日時\", \"更新日時\") "
                        + "VALUES (?, ?, ?, '', '00000', '', 0, ?, ?, ?, ?, 1, "
                        + "CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING",
                fiscalYear, month, accountCode,
                openingBalance, debitAmount, creditAmount, closingBalance
        );
    }

    @Nested
    @DisplayName("合計残高試算表取得フロー")
    class TrialBalanceFlow {

        @Test
        @DisplayName("合計残高試算表を取得できる")
        void shouldGetTrialBalance() {
            // When: 試算表を取得
            TrialBalanceResponse response = getRestClient()
                    .get()
                    .uri(API_PATH + "/trial-balance?fiscalYear=2025&month=1")
                    .retrieve()
                    .body(TrialBalanceResponse.class);

            // Then: 試算表が取得される
            assertThat(response).isNotNull();
            assertThat(response.getFiscalYear()).isEqualTo(2025);
            assertThat(response.getMonth()).isEqualTo(1);
            assertThat(response.getLines()).isNotEmpty();
        }

        @Test
        @DisplayName("BSPL区分で絞り込んだ試算表を取得できる")
        void shouldGetTrialBalanceByBsPlType() {
            // When: BS科目のみの試算表を取得
            TrialBalanceResponse response = getRestClient()
                    .get()
                    .uri(API_PATH + "/trial-balance?fiscalYear=2025&month=1&bsPlType=BS")
                    .retrieve()
                    .body(TrialBalanceResponse.class);

            // Then: BS科目のみが取得される
            assertThat(response).isNotNull();
            assertThat(response.getLines()).allMatch(
                    line -> "BS".equals(line.getBsPlType()));
        }

        @Test
        @DisplayName("借方合計と貸方合計が取得できる")
        void shouldGetTotalDebitAndCredit() {
            // When: 試算表を取得
            TrialBalanceResponse response = getRestClient()
                    .get()
                    .uri(API_PATH + "/trial-balance?fiscalYear=2025&month=1")
                    .retrieve()
                    .body(TrialBalanceResponse.class);

            // Then: 合計が計算されている
            assertThat(response).isNotNull();
            assertThat(response.getTotalDebit()).isNotNull();
            assertThat(response.getTotalCredit()).isNotNull();
        }
    }

    @Nested
    @DisplayName("月次残高一覧取得フロー")
    class MonthlyBalanceFlow {

        @Test
        @DisplayName("月次残高一覧を取得できる")
        void shouldGetMonthlyBalances() {
            // When: 月次残高一覧を取得
            MonthlyBalanceResponse[] response = getRestClient()
                    .get()
                    .uri(API_PATH + "/monthly?fiscalYear=2025&month=1")
                    .retrieve()
                    .body(MonthlyBalanceResponse[].class);

            // Then: 月次残高が取得される
            assertThat(response).isNotNull();
            assertThat(response.length).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("勘定科目別の月次残高推移を取得できる")
        void shouldGetMonthlyBalancesByAccountCode() {
            // When: 現金勘定の月次推移を取得
            MonthlyBalanceResponse[] response = getRestClient()
                    .get()
                    .uri(API_PATH + "/monthly/account/11110?fiscalYear=2025")
                    .retrieve()
                    .body(MonthlyBalanceResponse[].class);

            // Then: 月次推移が取得される（1月と2月）
            assertThat(response).isNotNull();
            assertThat(response.length).isGreaterThanOrEqualTo(2);
            assertThat(response).allMatch(
                    balance -> "11110".equals(balance.getAccountCode()));
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("月次残高データがデータベースに存在することを確認")
        void shouldHaveMonthlyBalanceInDatabase() {
            // Given: テストデータはsetUpで作成済み

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"月次勘定科目残高\" "
                            + "WHERE \"決算期\" = ? AND \"月度\" = ?",
                    Integer.class,
                    2025, 1
            );
            assertThat(count).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("APIレスポンスとデータベースの整合性を確認")
        void shouldMatchApiResponseWithDatabase() {
            // When: APIから月次残高を取得
            MonthlyBalanceResponse[] apiResponse = getRestClient()
                    .get()
                    .uri(API_PATH + "/monthly?fiscalYear=2025&month=1")
                    .retrieve()
                    .body(MonthlyBalanceResponse[].class);

            // When: データベースから直接取得
            Integer dbCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"月次勘定科目残高\" "
                            + "WHERE \"決算期\" = ? AND \"月度\" = ?",
                    Integer.class,
                    2025, 1
            );

            // Then: 件数が一致
            assertThat(apiResponse).isNotNull();
            assertThat(apiResponse.length).isEqualTo(dbCount);
        }
    }
}
