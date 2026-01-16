package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ActualCostUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.ActualCost;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 製造原価画面コントローラーテスト.
 */
@WebMvcTest(ActualCostWebController.class)
@DisplayName("製造原価画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ActualCostWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActualCostUseCase actualCostUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private WorkOrderUseCase workOrderUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(workOrderUseCase.getAllWorkOrders()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /manufacturing-costs - 製造原価一覧")
    class ListActualCosts {

        @Test
        @DisplayName("製造原価一覧画面を表示できる")
        void shouldDisplayActualCostList() throws Exception {
            ActualCost actualCost = createTestActualCost("WO-001", "ITEM-001");
            PageResult<ActualCost> pageResult = new PageResult<>(List.of(actualCost), 0, 20, 1);
            Mockito.when(actualCostUseCase.getActualCostList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("manufacturing-costs/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("actualCostList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            ActualCost actualCost = createTestActualCost("WO-001", "ITEM-001");
            PageResult<ActualCost> pageResult = new PageResult<>(List.of(actualCost), 0, 20, 1);
            Mockito.when(actualCostUseCase.getActualCostList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("WO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs")
                    .param("keyword", "WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("manufacturing-costs/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "WO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            ActualCost actualCost = createTestActualCost("WO-001", "ITEM-001");
            PageResult<ActualCost> pageResult = new PageResult<>(List.of(actualCost), 1, 10, 25);
            Mockito.when(actualCostUseCase.getActualCostList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /manufacturing-costs/new - 製造原価登録画面")
    class NewActualCost {

        @Test
        @DisplayName("製造原価登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("manufacturing-costs/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrders"));
        }
    }

    @Nested
    @DisplayName("POST /manufacturing-costs - 製造原価登録処理")
    class CreateActualCost {

        @Test
        @DisplayName("製造原価を登録できる")
        void shouldCreateActualCost() throws Exception {
            ActualCost created = createTestActualCost("WO-001", "ITEM-001");
            Mockito.when(actualCostUseCase.createActualCost(
                    ArgumentMatchers.any(ActualCost.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/manufacturing-costs")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("completedQuantity", "100")
                    .param("actualMaterialCost", "50000")
                    .param("actualLaborCost", "30000")
                    .param("actualExpense", "20000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/manufacturing-costs"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/manufacturing-costs")
                    .param("workOrderNumber", "")
                    .param("itemCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("manufacturing-costs/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "workOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /manufacturing-costs/{workOrderNumber} - 製造原価詳細画面")
    class ShowActualCost {

        @Test
        @DisplayName("製造原価詳細画面を表示できる")
        void shouldDisplayActualCostDetail() throws Exception {
            ActualCost actualCost = createTestActualCost("WO-001", "ITEM-001");
            Mockito.when(actualCostUseCase.getActualCost("WO-001"))
                .thenReturn(Optional.of(actualCost));

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs/WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("manufacturing-costs/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("actualCost"));
        }

        @Test
        @DisplayName("製造原価が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(actualCostUseCase.getActualCost("WO-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs/WO-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/manufacturing-costs"));
        }
    }

    @Nested
    @DisplayName("GET /manufacturing-costs/{workOrderNumber}/edit - 製造原価編集画面")
    class EditActualCost {

        @Test
        @DisplayName("製造原価編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            ActualCost actualCost = createTestActualCost("WO-001", "ITEM-001");
            Mockito.when(actualCostUseCase.getActualCost("WO-001"))
                .thenReturn(Optional.of(actualCost));

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs/WO-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("manufacturing-costs/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrders"));
        }

        @Test
        @DisplayName("製造原価が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(actualCostUseCase.getActualCost("WO-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/manufacturing-costs/WO-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/manufacturing-costs"));
        }
    }

    @Nested
    @DisplayName("POST /manufacturing-costs/{workOrderNumber} - 製造原価更新処理")
    class UpdateActualCost {

        @Test
        @DisplayName("製造原価を更新できる")
        void shouldUpdateActualCost() throws Exception {
            ActualCost updated = createTestActualCost("WO-001", "ITEM-001");
            Mockito.when(actualCostUseCase.updateActualCost(
                    ArgumentMatchers.eq("WO-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/manufacturing-costs/WO-001")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("completedQuantity", "100")
                    .param("actualMaterialCost", "55000")
                    .param("actualLaborCost", "32000")
                    .param("actualExpense", "21000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/manufacturing-costs"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /manufacturing-costs/{workOrderNumber}/delete - 製造原価削除処理")
    class DeleteActualCost {

        @Test
        @DisplayName("製造原価を削除できる")
        void shouldDeleteActualCost() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/manufacturing-costs/WO-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/manufacturing-costs"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(actualCostUseCase).deleteActualCost("WO-001");
        }
    }

    private ActualCost createTestActualCost(String workOrderNumber, String itemCode) {
        return ActualCost.builder()
            .id(1)
            .workOrderNumber(workOrderNumber)
            .itemCode(itemCode)
            .completedQuantity(new BigDecimal("100"))
            .actualMaterialCost(new BigDecimal("50000"))
            .actualLaborCost(new BigDecimal("30000"))
            .actualExpense(new BigDecimal("20000"))
            .actualManufacturingCost(new BigDecimal("100000"))
            .unitCost(new BigDecimal("1000"))
            .build();
    }
}
