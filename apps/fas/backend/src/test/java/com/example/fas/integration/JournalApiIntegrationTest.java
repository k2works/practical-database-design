package com.example.fas.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.DebitCreditCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.JournalDetailCommand;
import com.example.fas.application.port.in.dto.JournalResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 仕訳 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("仕訳 API 統合テスト")
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.AvoidDuplicateLiterals"})
class JournalApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/journals";

    @BeforeEach
    void setUp() {
        // トランザクションデータのみクリーンアップ（勘定科目はSeedデータを使用）
        cleanupTransactionData();
    }

    @AfterEach
    void tearDown() {
        cleanupTransactionData();
    }

    @Nested
    @DisplayName("仕訳登録・取得フロー")
    class JournalCrudFlow {

        @Test
        @DisplayName("仕訳を登録して取得できる")
        void shouldCreateAndRetrieveJournal() {
            // Given: 仕訳登録リクエスト（現金/売上の仕訳）
            CreateJournalCommand createRequest = createJournalCommand(
                    LocalDate.of(2025, 1, 15),
                    "11110", "41110", new BigDecimal("10000"),
                    "現金売上"
            );

            // When: 仕訳を登録
            JournalResponse createResponse = getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(JournalResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getJournalVoucherNumber()).isNotBlank();
            assertThat(createResponse.getPostingDate()).isEqualTo(LocalDate.of(2025, 1, 15));
            assertThat(createResponse.getDebitTotal()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(createResponse.getCreditTotal()).isEqualByComparingTo(new BigDecimal("10000"));

            // When: 登録した仕訳を取得
            String voucherNumber = createResponse.getJournalVoucherNumber();
            JournalResponse getResponse = getRestClient()
                    .get()
                    .uri(API_PATH + "/" + voucherNumber)
                    .retrieve()
                    .body(JournalResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.getJournalVoucherNumber()).isEqualTo(voucherNumber);
            assertThat(getResponse.getDetails()).hasSize(1);
        }

        @Test
        @DisplayName("存在しない仕訳を取得すると404エラー")
        void shouldReturn404WhenJournalNotFound() {
            // When & Then: 存在しない仕訳を取得すると例外
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/NOT-EXIST")
                            .retrieve()
                            .body(JournalResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("仕訳検索フロー")
    class JournalSearchFlow {

        @Test
        @DisplayName("期間指定で仕訳を検索できる")
        void shouldSearchJournalsByDateRange() {
            // Given: 複数の仕訳を作成
            createJournalViaApi(LocalDate.of(2025, 1, 10), "11110", "41110",
                    new BigDecimal("5000"), "売上1");
            createJournalViaApi(LocalDate.of(2025, 1, 15), "11110", "41110",
                    new BigDecimal("8000"), "売上2");
            createJournalViaApi(LocalDate.of(2025, 1, 20), "11110", "41110",
                    new BigDecimal("12000"), "売上3");

            // When: 期間指定で検索
            JournalResponse[] journals = getRestClient()
                    .get()
                    .uri(API_PATH + "?fromDate=2025-01-10&toDate=2025-01-20")
                    .retrieve()
                    .body(JournalResponse[].class);

            // Then: 期間内の仕訳が取得される
            assertThat(journals).isNotNull();
            assertThat(journals.length).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("勘定科目コードで仕訳を検索できる")
        void shouldSearchJournalsByAccountCode() {
            // Given: 異なる勘定科目の仕訳を作成
            createJournalViaApi(LocalDate.of(2025, 1, 15), "11110", "41110",
                    new BigDecimal("5000"), "現金売上");
            createJournalViaApi(LocalDate.of(2025, 1, 16), "11210", "41110",
                    new BigDecimal("8000"), "預金売上");

            // When: 現金勘定で検索
            JournalResponse[] journals = getRestClient()
                    .get()
                    .uri(API_PATH + "/by-account/11110")
                    .retrieve()
                    .body(JournalResponse[].class);

            // Then: 現金を含む仕訳が取得される
            assertThat(journals).isNotNull();
            assertThat(journals.length).isGreaterThanOrEqualTo(1);
        }
    }

    @Nested
    @DisplayName("仕訳取消フロー")
    class JournalCancelFlow {

        @Test
        @DisplayName("仕訳を取消（赤伝処理）できる")
        void shouldCancelJournal() {
            // Given: 仕訳を作成
            JournalResponse original = createJournalViaApi(
                    LocalDate.of(2025, 1, 15), "11110", "41110",
                    new BigDecimal("10000"), "取消テスト");

            // When: 仕訳を取消
            JournalResponse cancelResponse = getRestClient()
                    .post()
                    .uri(API_PATH + "/" + original.getJournalVoucherNumber() + "/cancel")
                    .retrieve()
                    .body(JournalResponse.class);

            // Then: 赤伝票が作成される
            assertThat(cancelResponse).isNotNull();
            assertThat(cancelResponse.getRedSlipFlag()).isTrue();
            // 赤伝票は元の仕訳と逆の金額
            assertThat(cancelResponse.getDebitTotal())
                    .isEqualByComparingTo(new BigDecimal("10000"));
        }
    }

    @Nested
    @DisplayName("仕訳削除フロー")
    class JournalDeleteFlow {

        @Test
        @DisplayName("仕訳を削除できる")
        void shouldDeleteJournal() {
            // Given: 仕訳を作成
            JournalResponse journal = createJournalViaApi(
                    LocalDate.of(2025, 1, 15), "11110", "41110",
                    new BigDecimal("10000"), "削除テスト");

            // When: 仕訳を削除
            getRestClient()
                    .delete()
                    .uri(API_PATH + "/" + journal.getJournalVoucherNumber())
                    .retrieve()
                    .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/" + journal.getJournalVoucherNumber())
                            .retrieve()
                            .body(JournalResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("仕訳登録時にデータベースに正しく保存される")
        void shouldPersistJournalToDatabase() {
            // Given: 仕訳登録リクエスト
            CreateJournalCommand createRequest = createJournalCommand(
                    LocalDate.of(2025, 1, 15),
                    "11110", "41110", new BigDecimal("15000"),
                    "DB検証仕訳"
            );

            // When: 仕訳を登録
            JournalResponse response = getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(JournalResponse.class);

            // Then: データベースに保存されていることを確認
            Integer journalCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"仕訳\" WHERE \"仕訳伝票番号\" = ?",
                    Integer.class,
                    response.getJournalVoucherNumber()
            );
            assertThat(journalCount).isEqualTo(1);

            // 仕訳明細も保存されている
            Integer detailCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"仕訳明細\" WHERE \"仕訳伝票番号\" = ?",
                    Integer.class,
                    response.getJournalVoucherNumber()
            );
            assertThat(detailCount).isEqualTo(1);

            // 借方・貸方明細も保存されている
            Integer dcCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"仕訳貸借明細\" WHERE \"仕訳伝票番号\" = ?",
                    Integer.class,
                    response.getJournalVoucherNumber()
            );
            assertThat(dcCount).isEqualTo(2); // 借方と貸方で2件
        }
    }

    /**
     * テスト用の仕訳登録コマンドを作成.
     * 単純な借方/貸方の仕訳を作成する。
     */
    private CreateJournalCommand createJournalCommand(
            LocalDate postingDate, String debitAccountCode, String creditAccountCode,
            BigDecimal amount, String summary) {
        return new CreateJournalCommand(
                postingDate,
                null, // entryDate
                null, // voucherType
                null, // closingJournalFlag
                null, // singleEntryFlag
                null, // periodicPostingFlag
                null, // employeeCode
                null, // departmentCode
                List.of(
                        new JournalDetailCommand(
                                summary,
                                List.of(
                                        new DebitCreditCommand(
                                                "借方",
                                                debitAccountCode,
                                                null, // subAccountCode
                                                null, // departmentCode
                                                amount,
                                                null, // currencyCode
                                                null, // exchangeRate
                                                null, // baseCurrencyAmount
                                                null, // taxType
                                                null, // taxRate
                                                null, // taxCalcType
                                                null, // dueDate
                                                null  // cashFlowFlag
                                        ),
                                        new DebitCreditCommand(
                                                "貸方",
                                                creditAccountCode,
                                                null, // subAccountCode
                                                null, // departmentCode
                                                amount,
                                                null, // currencyCode
                                                null, // exchangeRate
                                                null, // baseCurrencyAmount
                                                null, // taxType
                                                null, // taxRate
                                                null, // taxCalcType
                                                null, // dueDate
                                                null  // cashFlowFlag
                                        )
                                )
                        )
                )
        );
    }

    /**
     * API 経由で仕訳を作成.
     */
    private JournalResponse createJournalViaApi(
            LocalDate postingDate, String debitAccountCode, String creditAccountCode,
            BigDecimal amount, String summary) {
        CreateJournalCommand request = createJournalCommand(
                postingDate, debitAccountCode, creditAccountCode, amount, summary);
        return getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JournalResponse.class);
    }
}
