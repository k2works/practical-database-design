package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.domain.model.common.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

/**
 * 勘定科目マスタ画面コントローラーテスト.
 */
@WebMvcTest(AccountWebController.class)
@DisplayName("勘定科目マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class AccountWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountUseCase accountUseCase;

    @Nested
    @DisplayName("GET /accounts")
    class ListAccounts {

        @Test
        @DisplayName("勘定科目一覧画面を表示できる")
        void shouldDisplayAccountList() throws Exception {
            AccountResponse account = createTestAccount("10100", "現金", "BS", "借方");
            PageResult<AccountResponse> pageResult = new PageResult<>(List.of(account), 0, 20, 1);
            Mockito.when(accountUseCase.getAccounts(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("accounts"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("BS/PL区分でフィルタできる")
        void shouldFilterByBsPlType() throws Exception {
            AccountResponse bsAccount = createTestAccount("10100", "現金", "BS", "借方");
            PageResult<AccountResponse> pageResult = new PageResult<>(List.of(bsAccount), 0, 20, 1);
            Mockito.when(accountUseCase.getAccounts(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.eq("BS"),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                    .param("bsPlType", "BS"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedBsPlType", "BS"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            AccountResponse account = createTestAccount("10100", "現金", "BS", "借方");
            PageResult<AccountResponse> pageResult = new PageResult<>(List.of(account), 1, 10, 15);
            Mockito.when(accountUseCase.getAccounts(
                ArgumentMatchers.eq(1),
                ArgumentMatchers.eq(10),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }
    }

    @Nested
    @DisplayName("GET /accounts/{accountCode}")
    class ShowAccount {

        @Test
        @DisplayName("勘定科目詳細画面を表示できる")
        void shouldDisplayAccountDetail() throws Exception {
            AccountResponse account = createTestAccount("10100", "現金", "BS", "借方");
            Mockito.when(accountUseCase.getAccount("10100")).thenReturn(account);

            mockMvc.perform(MockMvcRequestBuilders.get("/accounts/10100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("account"));
        }
    }

    @Nested
    @DisplayName("GET /accounts/new")
    class NewAccountForm {

        @Test
        @DisplayName("勘定科目登録フォームを表示できる")
        void shouldDisplayNewAccountForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/accounts/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /accounts")
    class CreateAccount {

        @Test
        @DisplayName("勘定科目を登録できる")
        void shouldCreateAccount() throws Exception {
            AccountResponse created = createTestAccount("99001", "テスト科目", "BS", "借方");
            Mockito.when(accountUseCase.createAccount(ArgumentMatchers.any()))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accountCode", "99001")
                    .param("accountName", "テスト科目")
                    .param("accountShortName", "テスト")
                    .param("bsPlType", "BS")
                    .param("dcType", "借方")
                    .param("elementType", "資産")
                    .param("summaryType", "計上科目"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/accounts"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は登録フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accountCode", "")
                    .param("accountName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /accounts/{accountCode}/edit")
    class EditAccountForm {

        @Test
        @DisplayName("勘定科目編集フォームを表示できる")
        void shouldDisplayEditAccountForm() throws Exception {
            AccountResponse account = createTestAccount("10100", "現金", "BS", "借方");
            Mockito.when(accountUseCase.getAccount("10100")).thenReturn(account);

            mockMvc.perform(MockMvcRequestBuilders.get("/accounts/10100/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accounts/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /accounts/{accountCode}")
    class UpdateAccount {

        @Test
        @DisplayName("勘定科目を更新できる")
        void shouldUpdateAccount() throws Exception {
            AccountResponse updated = createTestAccount("10100", "更新後現金", "BS", "借方");
            Mockito.when(accountUseCase.updateAccount(
                ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/accounts/10100")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accountCode", "10100")
                    .param("accountName", "更新後現金")
                    .param("accountShortName", "現金")
                    .param("bsPlType", "BS")
                    .param("dcType", "借方")
                    .param("elementType", "資産")
                    .param("summaryType", "計上科目"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/accounts/10100"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /accounts/{accountCode}/delete")
    class DeleteAccount {

        @Test
        @DisplayName("勘定科目を削除できる")
        void shouldDeleteAccount() throws Exception {
            Mockito.doNothing().when(accountUseCase).deleteAccount("99999");

            mockMvc.perform(MockMvcRequestBuilders.post("/accounts/99999/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/accounts"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private AccountResponse createTestAccount(String code, String name, String bsPlType,
                                               String dcType) {
        return AccountResponse.builder()
            .accountCode(code)
            .accountName(name)
            .accountShortName(name.length() > 10 ? name.substring(0, 10) : name)
            .bsPlType(bsPlType)
            .dcType(dcType)
            .elementType("資産")
            .summaryType("計上科目")
            .build();
    }
}
