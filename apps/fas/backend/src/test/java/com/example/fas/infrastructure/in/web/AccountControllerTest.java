package com.example.fas.infrastructure.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AccountController の統合テスト.
 */
@AutoConfigureMockMvc
@DisplayName("勘定科目マスタ API")
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.TooManyStaticImports"})
class AccountControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    private Account createTestAccount(String code, String name) {
        return Account.builder()
                .accountCode(code)
                .accountName(name)
                .accountShortName(name)
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING)
                .build();
    }

    @Nested
    @DisplayName("GET /api/accounts/{accountCode}")
    class GetAccountTest {

        @Test
        @DisplayName("勘定科目を取得できる")
        void canGetAccount() throws Exception {
            // Given
            Account account = createTestAccount("11110", "現金");
            accountRepository.save(account);

            // When & Then
            mockMvc.perform(get("/api/accounts/{accountCode}", "11110"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountCode").value("11110"))
                    .andExpect(jsonPath("$.accountName").value("現金"))
                    .andExpect(jsonPath("$.bsPlType").value("BS"));
        }

        @Test
        @DisplayName("存在しない勘定科目は404を返す")
        void notFoundForNonExistent() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/accounts/{accountCode}", "99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts")
    class GetAccountsTest {

        @Test
        @DisplayName("全勘定科目を取得できる")
        void canGetAllAccounts() throws Exception {
            // Given
            Account account = createTestAccount("11110", "現金");
            accountRepository.save(account);

            // When & Then
            mockMvc.perform(get("/api/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].accountCode").value("11110"));
        }

        @Test
        @DisplayName("BSPL区分で勘定科目を取得できる")
        void canGetAccountsByBsPlType() throws Exception {
            // Given
            Account bsAccount = createTestAccount("11110", "現金");
            accountRepository.save(bsAccount);

            Account plAccount = Account.builder()
                    .accountCode("41110")
                    .accountName("売上高")
                    .accountShortName("売上高")
                    .bsplType(BSPLType.PL)
                    .debitCreditType(DebitCreditType.CREDIT)
                    .transactionElementType(TransactionElementType.REVENUE)
                    .aggregationType(AggregationType.POSTING)
                    .build();
            accountRepository.save(plAccount);

            // When & Then
            mockMvc.perform(get("/api/accounts").param("bsPlType", "BS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].bsPlType").value("BS"));
        }

        @Test
        @DisplayName("計上科目のみ取得できる")
        void canGetPostingAccountsOnly() throws Exception {
            // Given
            Account postingAccount = createTestAccount("11110", "現金");
            accountRepository.save(postingAccount);

            // When & Then
            mockMvc.perform(get("/api/accounts").param("postingOnly", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].summaryType").value("計上科目"));
        }
    }

    @Nested
    @DisplayName("POST /api/accounts")
    class CreateAccountTest {

        @Test
        @DisplayName("勘定科目を登録できる")
        void canCreateAccount() throws Exception {
            // Given
            String json = """
                {
                    "accountCode": "11150",
                    "accountName": "手許現金",
                    "bsPlType": "BS",
                    "dcType": "借方",
                    "elementType": "資産",
                    "summaryType": "計上科目"
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountCode").value("11150"));
        }

        @Test
        @DisplayName("バリデーションエラーで400を返す")
        void badRequestForValidationError() throws Exception {
            // Given
            String json = """
                {
                    "accountCode": ""
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/accounts/{accountCode}")
    class UpdateAccountTest {

        @Test
        @DisplayName("勘定科目を更新できる")
        void canUpdateAccount() throws Exception {
            // Given
            Account account = createTestAccount("11110", "現金");
            accountRepository.save(account);

            String json = """
                {
                    "accountName": "現金(更新)"
                }
                """;

            // When & Then
            mockMvc.perform(put("/api/accounts/{accountCode}", "11110")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountName").value("現金(更新)"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/accounts/{accountCode}")
    class DeleteAccountTest {

        @Test
        @DisplayName("勘定科目を削除できる")
        void canDeleteAccount() throws Exception {
            // Given
            Account account = createTestAccount("11110", "現金");
            accountRepository.save(account);

            // When & Then
            mockMvc.perform(delete("/api/accounts/{accountCode}", "11110"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("存在しない勘定科目の削除は404を返す")
        void notFoundForNonExistent() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/accounts/{accountCode}", "99999"))
                    .andExpect(status().isNotFound());
        }
    }
}
