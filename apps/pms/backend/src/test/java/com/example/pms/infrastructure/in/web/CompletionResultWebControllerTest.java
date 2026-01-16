package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CompletionResultUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.CompletionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 完成実績画面コントローラーテスト.
 */
@WebMvcTest(CompletionResultWebController.class)
@DisplayName("完成実績画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class CompletionResultWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompletionResultUseCase completionResultUseCase;

    @MockitoBean
    private WorkOrderUseCase workOrderUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(workOrderUseCase.getAllWorkOrders()).thenReturn(Collections.emptyList());
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /completion-results - 完成実績一覧")
    class ListCompletionResults {

        @Test
        @DisplayName("完成実績一覧画面を表示できる")
        void shouldDisplayCompletionResultsList() throws Exception {
            CompletionResult completionResult = createTestCompletionResult("CR-001", "WO-001");
            PageResult<CompletionResult> pageResult = new PageResult<>(List.of(completionResult), 0, 20, 1);
            Mockito.when(completionResultUseCase.getCompletionResultList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("completion-results/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("completionResultList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            CompletionResult completionResult = createTestCompletionResult("CR-001", "WO-001");
            PageResult<CompletionResult> pageResult = new PageResult<>(List.of(completionResult), 0, 20, 1);
            Mockito.when(completionResultUseCase.getCompletionResultList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("WO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results")
                    .param("keyword", "WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("completion-results/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "WO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            CompletionResult completionResult = createTestCompletionResult("CR-001", "WO-001");
            PageResult<CompletionResult> pageResult = new PageResult<>(List.of(completionResult), 1, 10, 25);
            Mockito.when(completionResultUseCase.getCompletionResultList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /completion-results/new - 完成実績登録画面")
    class NewCompletionResult {

        @Test
        @DisplayName("完成実績登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("completion-results/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /completion-results - 完成実績登録処理")
    class CreateCompletionResult {

        @Test
        @DisplayName("完成実績を登録できる")
        void shouldCreateCompletionResult() throws Exception {
            CompletionResult created = createTestCompletionResult("CR-001", "WO-001");
            Mockito.when(completionResultUseCase.createCompletionResult(
                    ArgumentMatchers.any(CompletionResult.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/completion-results")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("completionDate", "2024-01-20")
                    .param("completedQuantity", "100")
                    .param("goodQuantity", "95")
                    .param("defectQuantity", "5"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/completion-results"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/completion-results")
                    .param("workOrderNumber", "")
                    .param("completedQuantity", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("completion-results/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "workOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /completion-results/{completionResultNumber} - 完成実績詳細画面")
    class ShowCompletionResult {

        @Test
        @DisplayName("完成実績詳細画面を表示できる")
        void shouldDisplayCompletionResultDetail() throws Exception {
            CompletionResult completionResult = createTestCompletionResult("CR-001", "WO-001");
            Mockito.when(completionResultUseCase.getCompletionResult("CR-001"))
                .thenReturn(Optional.of(completionResult));

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results/CR-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("completion-results/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("completionResult"));
        }

        @Test
        @DisplayName("完成実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(completionResultUseCase.getCompletionResult("CR-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results/CR-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/completion-results"));
        }
    }

    @Nested
    @DisplayName("GET /completion-results/{completionResultNumber}/edit - 完成実績編集画面")
    class EditCompletionResult {

        @Test
        @DisplayName("完成実績編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            CompletionResult completionResult = createTestCompletionResult("CR-001", "WO-001");
            Mockito.when(completionResultUseCase.getCompletionResult("CR-001"))
                .thenReturn(Optional.of(completionResult));

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results/CR-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("completion-results/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("完成実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(completionResultUseCase.getCompletionResult("CR-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/completion-results/CR-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/completion-results"));
        }
    }

    @Nested
    @DisplayName("POST /completion-results/{completionResultNumber} - 完成実績更新処理")
    class UpdateCompletionResult {

        @Test
        @DisplayName("完成実績を更新できる")
        void shouldUpdateCompletionResult() throws Exception {
            CompletionResult updated = createTestCompletionResult("CR-001", "WO-001");
            Mockito.when(completionResultUseCase.updateCompletionResult(
                    ArgumentMatchers.eq("CR-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/completion-results/CR-001")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("completionDate", "2024-01-20")
                    .param("completedQuantity", "150")
                    .param("goodQuantity", "140")
                    .param("defectQuantity", "10"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/completion-results"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /completion-results/{completionResultNumber}/delete - 完成実績削除処理")
    class DeleteCompletionResult {

        @Test
        @DisplayName("完成実績を削除できる")
        void shouldDeleteCompletionResult() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/completion-results/CR-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/completion-results"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(completionResultUseCase).deleteCompletionResult("CR-001");
        }
    }

    private CompletionResult createTestCompletionResult(String completionResultNumber, String workOrderNumber) {
        return CompletionResult.builder()
            .id(1)
            .completionResultNumber(completionResultNumber)
            .workOrderNumber(workOrderNumber)
            .itemCode("ITEM-001")
            .completionDate(LocalDate.of(2024, 1, 20))
            .completedQuantity(new BigDecimal("100"))
            .goodQuantity(new BigDecimal("95"))
            .defectQuantity(new BigDecimal("5"))
            .remarks("テスト用")
            .version(1)
            .build();
    }
}
