package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountStructureUseCase;
import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.dto.AccountStructureResponse;
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
 * 勘定科目構成マスタ画面コントローラーテスト.
 */
@WebMvcTest(AccountStructureWebController.class)
@DisplayName("勘定科目構成マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class AccountStructureWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountStructureUseCase accountStructureUseCase;

    @MockitoBean
    private AccountUseCase accountUseCase;

    @Nested
    @DisplayName("GET /account-structures")
    class ListAccountStructures {

        @Test
        @DisplayName("勘定科目構成一覧画面を表示できる")
        void shouldDisplayAccountStructureList() throws Exception {
            AccountStructureResponse response = createTestAccountStructure("10100", "現金", "10000~10100", 1);
            PageResult<AccountStructureResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(accountStructureUseCase.getAccountStructures(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/account-structures"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("structures"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("キーワードでフィルタできる")
        void shouldFilterByKeyword() throws Exception {
            AccountStructureResponse response = createTestAccountStructure("10100", "現金", "10000~10100", 1);
            PageResult<AccountStructureResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(accountStructureUseCase.getAccountStructures(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.eq("現金")
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/account-structures")
                    .param("keyword", "現金"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "現金"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            AccountStructureResponse response = createTestAccountStructure("10100", "現金", "10000~10100", 1);
            PageResult<AccountStructureResponse> pageResult = new PageResult<>(List.of(response), 1, 10, 15);
            Mockito.when(accountStructureUseCase.getAccountStructures(
                ArgumentMatchers.eq(1),
                ArgumentMatchers.eq(10),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/account-structures")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }
    }

    @Nested
    @DisplayName("GET /account-structures/{accountCode}")
    class ShowAccountStructure {

        @Test
        @DisplayName("勘定科目構成詳細画面を表示できる")
        void shouldDisplayAccountStructureDetail() throws Exception {
            AccountStructureResponse response = createTestAccountStructure("10100", "現金", "10000~10100", 1);
            Mockito.when(accountStructureUseCase.getAccountStructure("10100")).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get("/account-structures/10100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("structure"));
        }
    }

    @Nested
    @DisplayName("GET /account-structures/new")
    class NewAccountStructureForm {

        @Test
        @DisplayName("勘定科目構成登録フォームを表示できる")
        void shouldDisplayNewAccountStructureForm() throws Exception {
            Mockito.when(accountUseCase.getAllAccounts()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/account-structures/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("accounts"));
        }
    }

    @Nested
    @DisplayName("POST /account-structures")
    class CreateAccountStructure {

        @Test
        @DisplayName("勘定科目構成を登録できる")
        void shouldCreateAccountStructure() throws Exception {
            AccountStructureResponse created = createTestAccountStructure("99001", "テスト科目", "10000~99001", 1);
            Mockito.when(accountStructureUseCase.createAccountStructure(ArgumentMatchers.any()))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/account-structures")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accountCode", "99001")
                    .param("accountPath", "10000~99001"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account-structures"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は登録フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            Mockito.when(accountUseCase.getAllAccounts()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.post("/account-structures")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accountCode", "")
                    .param("accountPath", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /account-structures/{accountCode}/edit")
    class EditAccountStructureForm {

        @Test
        @DisplayName("勘定科目構成編集フォームを表示できる")
        void shouldDisplayEditAccountStructureForm() throws Exception {
            AccountStructureResponse response = createTestAccountStructure("10100", "現金", "10000~10100", 1);
            Mockito.when(accountStructureUseCase.getAccountStructure("10100")).thenReturn(response);
            Mockito.when(accountUseCase.getAllAccounts()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/account-structures/10100/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("account-structures/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("accounts"));
        }
    }

    @Nested
    @DisplayName("POST /account-structures/{accountCode}")
    class UpdateAccountStructure {

        @Test
        @DisplayName("勘定科目構成を更新できる")
        void shouldUpdateAccountStructure() throws Exception {
            AccountStructureResponse updated = createTestAccountStructure("10100", "現金", "10000~10100", 1);
            Mockito.when(accountStructureUseCase.updateAccountStructure(
                ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/account-structures/10100")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accountCode", "10100")
                    .param("accountPath", "10000~10100"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account-structures/10100"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /account-structures/{accountCode}/delete")
    class DeleteAccountStructure {

        @Test
        @DisplayName("勘定科目構成を削除できる")
        void shouldDeleteAccountStructure() throws Exception {
            Mockito.doNothing().when(accountStructureUseCase).deleteAccountStructure("99999");

            mockMvc.perform(MockMvcRequestBuilders.post("/account-structures/99999/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account-structures"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private AccountStructureResponse createTestAccountStructure(String code, String name,
                                                                  String path, int depth) {
        return AccountStructureResponse.builder()
            .accountCode(code)
            .accountName(name)
            .accountPath(path)
            .parentCode(path.contains("~") ? path.split("~")[0] : null)
            .depth(depth)
            .build();
    }
}
