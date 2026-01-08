package com.example.fas.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.in.dto.CreateAccountCommand;
import com.example.fas.application.port.in.dto.UpdateAccountCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 勘定科目 API 統合テスト.
 * 実際の HTTP リクエストを使用してエンドポイントをテストする。
 */
@DisplayName("勘定科目 API 統合テスト")
@SuppressWarnings("PMD.TooManyStaticImports")
class AccountApiIntegrationTest extends IntegrationTestBase {

    private static final String API_PATH = "/api/accounts";

    @BeforeEach
    void setUp() {
        cleanupAllData();
    }

    @AfterEach
    void tearDown() {
        cleanupAllData();
    }

    @Nested
    @DisplayName("勘定科目登録・取得フロー")
    class AccountCrudFlow {

        @Test
        @DisplayName("勘定科目を登録して取得できる")
        void shouldCreateAndRetrieveAccount() {
            // Given: 勘定科目登録リクエスト
            CreateAccountCommand createRequest = createAccountCommand(
                    "99001", "テスト現金", "BS", "借方", "資産", "計上科目");

            // When: 勘定科目を登録
            AccountResponse createResponse = getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(AccountResponse.class);

            // Then: 登録成功
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getAccountCode()).isEqualTo("99001");
            assertThat(createResponse.getAccountName()).isEqualTo("テスト現金");
            assertThat(createResponse.getBsPlType()).isEqualTo("BS");
            assertThat(createResponse.getDcType()).isEqualTo("借方");

            // When: 登録した勘定科目を取得
            AccountResponse getResponse = getRestClient()
                    .get()
                    .uri(API_PATH + "/99001")
                    .retrieve()
                    .body(AccountResponse.class);

            // Then: 取得成功
            assertThat(getResponse).isNotNull();
            assertThat(getResponse.getAccountCode()).isEqualTo("99001");
            assertThat(getResponse.getAccountName()).isEqualTo("テスト現金");
        }

        @Test
        @DisplayName("存在しない勘定科目を取得すると404エラー")
        void shouldReturn404WhenAccountNotFound() {
            // When & Then: 存在しない勘定科目を取得すると例外
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/NOT-EXIST")
                            .retrieve()
                            .body(AccountResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("勘定科目更新フロー")
    class AccountUpdateFlow {

        @Test
        @DisplayName("勘定科目を更新できる")
        void shouldUpdateAccount() {
            // Given: 勘定科目を作成
            CreateAccountCommand createRequest = createAccountCommand(
                    "99002", "更新前科目", "BS", "借方", "資産", "計上科目");
            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(AccountResponse.class);

            // When: 勘定科目を更新
            UpdateAccountCommand updateRequest = UpdateAccountCommand.builder()
                    .accountName("更新後科目")
                    .accountShortName("更新後")
                    .build();

            AccountResponse updateResponse = getRestClient()
                    .put()
                    .uri(API_PATH + "/99002")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateRequest)
                    .retrieve()
                    .body(AccountResponse.class);

            // Then: 更新が反映されている
            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.getAccountName()).isEqualTo("更新後科目");
            assertThat(updateResponse.getAccountShortName()).isEqualTo("更新後");
        }
    }

    @Nested
    @DisplayName("勘定科目削除フロー")
    class AccountDeleteFlow {

        @Test
        @DisplayName("勘定科目を削除できる")
        void shouldDeleteAccount() {
            // Given: 勘定科目を作成
            CreateAccountCommand createRequest = createAccountCommand(
                    "99003", "削除テスト科目", "BS", "借方", "資産", "計上科目");
            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(AccountResponse.class);

            // When: 勘定科目を削除
            getRestClient()
                    .delete()
                    .uri(API_PATH + "/99003")
                    .retrieve()
                    .toBodilessEntity();

            // Then: 削除後は取得できない
            assertThatThrownBy(() ->
                    getRestClient()
                            .get()
                            .uri(API_PATH + "/99003")
                            .retrieve()
                            .body(AccountResponse.class)
            ).isInstanceOf(HttpClientErrorException.class)
                    .satisfies(ex -> {
                        HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                        assertThat(httpEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("勘定科目一覧取得")
    class AccountListFlow {

        @Test
        @DisplayName("勘定科目一覧を取得できる")
        void shouldGetAllAccounts() {
            // Given: 複数の勘定科目を作成
            createAccountViaApi("99010", "テスト科目1", "BS", "借方", "資産", "計上科目");
            createAccountViaApi("99011", "テスト科目2", "BS", "貸方", "負債", "計上科目");
            createAccountViaApi("99012", "テスト科目3", "PL", "貸方", "収益", "計上科目");

            // When: 勘定科目一覧を取得
            AccountResponse[] accounts = getRestClient()
                    .get()
                    .uri(API_PATH)
                    .retrieve()
                    .body(AccountResponse[].class);

            // Then: 作成した勘定科目が含まれている
            assertThat(accounts).isNotNull();
            assertThat(accounts.length).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("BSPL区分で勘定科目を絞り込める")
        void shouldGetAccountsByBsPlType() {
            // Given: BS/PL それぞれの勘定科目を作成
            createAccountViaApi("99020", "BS科目", "BS", "借方", "資産", "計上科目");
            createAccountViaApi("99021", "PL科目", "PL", "借方", "費用", "計上科目");

            // When: BSで絞り込み
            AccountResponse[] bsAccounts = getRestClient()
                    .get()
                    .uri(API_PATH + "?bsPlType=BS")
                    .retrieve()
                    .body(AccountResponse[].class);

            // Then: BS勘定科目のみが取得される
            assertThat(bsAccounts).isNotNull();
            assertThat(bsAccounts).allMatch(a -> "BS".equals(a.getBsPlType()));
        }
    }

    @Nested
    @DisplayName("データベース状態の検証")
    class DatabaseStateVerification {

        @Test
        @DisplayName("勘定科目登録時にデータベースに正しく保存される")
        void shouldPersistAccountToDatabase() {
            // Given: 勘定科目登録リクエスト
            CreateAccountCommand createRequest = createAccountCommand(
                    "99099", "DB検証用科目", "BS", "借方", "資産", "計上科目");

            // When: 勘定科目を登録
            getRestClient()
                    .post()
                    .uri(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest)
                    .retrieve()
                    .body(AccountResponse.class);

            // Then: データベースに保存されていることを確認
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"勘定科目マスタ\" WHERE \"勘定科目コード\" = ?",
                    Integer.class,
                    "99099"
            );
            assertThat(count).isEqualTo(1);
        }
    }

    /**
     * テスト用の勘定科目登録コマンドを作成.
     */
    private CreateAccountCommand createAccountCommand(
            String accountCode, String accountName, String bsPlType,
            String dcType, String elementType, String summaryType) {
        return CreateAccountCommand.builder()
                .accountCode(accountCode)
                .accountName(accountName)
                .accountShortName(accountName.length() > 10
                        ? accountName.substring(0, 10) : accountName)
                .bsPlType(bsPlType)
                .dcType(dcType)
                .elementType(elementType)
                .summaryType(summaryType)
                .build();
    }

    /**
     * API 経由で勘定科目を作成.
     */
    private void createAccountViaApi(String accountCode, String accountName,
            String bsPlType, String dcType, String elementType, String summaryType) {
        CreateAccountCommand request = createAccountCommand(
                accountCode, accountName, bsPlType, dcType, elementType, summaryType);
        getRestClient()
                .post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AccountResponse.class);
    }
}
