package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.TaxTransactionUseCase;
import com.example.fas.application.port.in.dto.TaxTransactionResponse;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * 課税取引マスタ画面コントローラーテスト.
 */
@WebMvcTest(TaxTransactionWebController.class)
@DisplayName("課税取引マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class TaxTransactionWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxTransactionUseCase taxTransactionUseCase;

    @Nested
    @DisplayName("GET /tax-transactions")
    class ListTaxTransactions {

        @Test
        @DisplayName("課税取引一覧画面を表示できる")
        void shouldDisplayTaxTransactionList() throws Exception {
            TaxTransactionResponse response = createTestTaxTransaction("010", "課税売上10%", "0.10");
            PageResult<TaxTransactionResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(taxTransactionUseCase.getTaxTransactions(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/tax-transactions"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("taxTransactions"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("キーワードでフィルタできる")
        void shouldFilterByKeyword() throws Exception {
            TaxTransactionResponse response = createTestTaxTransaction("010", "課税売上10%", "0.10");
            PageResult<TaxTransactionResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(taxTransactionUseCase.getTaxTransactions(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.eq("課税")
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/tax-transactions")
                    .param("keyword", "課税"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "課税"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            TaxTransactionResponse response = createTestTaxTransaction("010", "課税売上10%", "0.10");
            PageResult<TaxTransactionResponse> pageResult = new PageResult<>(List.of(response), 1, 10, 15);
            Mockito.when(taxTransactionUseCase.getTaxTransactions(
                ArgumentMatchers.eq(1),
                ArgumentMatchers.eq(10),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/tax-transactions")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }
    }

    @Nested
    @DisplayName("GET /tax-transactions/{taxCode}")
    class ShowTaxTransaction {

        @Test
        @DisplayName("課税取引詳細画面を表示できる")
        void shouldDisplayTaxTransactionDetail() throws Exception {
            TaxTransactionResponse response = createTestTaxTransaction("010", "課税売上10%", "0.10");
            Mockito.when(taxTransactionUseCase.getTaxTransaction("010")).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get("/tax-transactions/010"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("taxTransaction"));
        }
    }

    @Nested
    @DisplayName("GET /tax-transactions/new")
    class NewTaxTransactionForm {

        @Test
        @DisplayName("課税取引登録フォームを表示できる")
        void shouldDisplayNewTaxTransactionForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/tax-transactions/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /tax-transactions")
    class CreateTaxTransaction {

        @Test
        @DisplayName("課税取引を登録できる")
        void shouldCreateTaxTransaction() throws Exception {
            TaxTransactionResponse created = createTestTaxTransaction("999", "テスト課税", "0.10");
            Mockito.when(taxTransactionUseCase.createTaxTransaction(ArgumentMatchers.any()))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/tax-transactions")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("taxCode", "999")
                    .param("taxName", "テスト課税")
                    .param("taxRate", "0.10"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/tax-transactions"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は登録フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/tax-transactions")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("taxCode", "")
                    .param("taxName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /tax-transactions/{taxCode}/edit")
    class EditTaxTransactionForm {

        @Test
        @DisplayName("課税取引編集フォームを表示できる")
        void shouldDisplayEditTaxTransactionForm() throws Exception {
            TaxTransactionResponse response = createTestTaxTransaction("010", "課税売上10%", "0.10");
            Mockito.when(taxTransactionUseCase.getTaxTransaction("010")).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get("/tax-transactions/010/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tax-transactions/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /tax-transactions/{taxCode}")
    class UpdateTaxTransaction {

        @Test
        @DisplayName("課税取引を更新できる")
        void shouldUpdateTaxTransaction() throws Exception {
            TaxTransactionResponse updated = createTestTaxTransaction("010", "更新後課税", "0.08");
            Mockito.when(taxTransactionUseCase.updateTaxTransaction(
                ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/tax-transactions/010")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("taxCode", "010")
                    .param("taxName", "更新後課税")
                    .param("taxRate", "0.08"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/tax-transactions/010"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /tax-transactions/{taxCode}/delete")
    class DeleteTaxTransaction {

        @Test
        @DisplayName("課税取引を削除できる")
        void shouldDeleteTaxTransaction() throws Exception {
            Mockito.doNothing().when(taxTransactionUseCase).deleteTaxTransaction("999");

            mockMvc.perform(MockMvcRequestBuilders.post("/tax-transactions/999/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/tax-transactions"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private TaxTransactionResponse createTestTaxTransaction(String code, String name, String rate) {
        return TaxTransactionResponse.builder()
            .taxCode(code)
            .taxName(name)
            .taxRate(new BigDecimal(rate))
            .build();
    }
}
