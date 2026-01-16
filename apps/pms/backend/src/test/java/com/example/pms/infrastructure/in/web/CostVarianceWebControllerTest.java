package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CostVarianceUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.CostVariance;
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
 * 原価差異画面コントローラーテスト.
 */
@WebMvcTest(CostVarianceWebController.class)
@DisplayName("原価差異画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class CostVarianceWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CostVarianceUseCase costVarianceUseCase;

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
    @DisplayName("GET /cost-variances - 原価差異一覧")
    class ListCostVariances {

        @Test
        @DisplayName("原価差異一覧画面を表示できる")
        void shouldDisplayCostVarianceList() throws Exception {
            CostVariance variance = createTestCostVariance("WO-001", "ITEM-001");
            PageResult<CostVariance> pageResult = new PageResult<>(List.of(variance), 0, 20, 1);
            Mockito.when(costVarianceUseCase.getCostVarianceList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cost-variances/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("varianceList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            CostVariance variance = createTestCostVariance("WO-001", "ITEM-001");
            PageResult<CostVariance> pageResult = new PageResult<>(List.of(variance), 0, 20, 1);
            Mockito.when(costVarianceUseCase.getCostVarianceList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("WO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances")
                    .param("keyword", "WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cost-variances/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "WO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            CostVariance variance = createTestCostVariance("WO-001", "ITEM-001");
            PageResult<CostVariance> pageResult = new PageResult<>(List.of(variance), 1, 10, 25);
            Mockito.when(costVarianceUseCase.getCostVarianceList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /cost-variances/new - 原価差異登録画面")
    class NewCostVariance {

        @Test
        @DisplayName("原価差異登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cost-variances/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrders"));
        }
    }

    @Nested
    @DisplayName("POST /cost-variances - 原価差異登録処理")
    class CreateCostVariance {

        @Test
        @DisplayName("原価差異を登録できる")
        void shouldCreateCostVariance() throws Exception {
            CostVariance created = createTestCostVariance("WO-001", "ITEM-001");
            Mockito.when(costVarianceUseCase.createCostVariance(
                    ArgumentMatchers.any(CostVariance.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/cost-variances")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("materialCostVariance", "5000")
                    .param("laborCostVariance", "3000")
                    .param("expenseVariance", "2000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cost-variances"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/cost-variances")
                    .param("workOrderNumber", "")
                    .param("itemCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cost-variances/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "workOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /cost-variances/{workOrderNumber} - 原価差異詳細画面")
    class ShowCostVariance {

        @Test
        @DisplayName("原価差異詳細画面を表示できる")
        void shouldDisplayCostVarianceDetail() throws Exception {
            CostVariance variance = createTestCostVariance("WO-001", "ITEM-001");
            Mockito.when(costVarianceUseCase.getCostVariance("WO-001"))
                .thenReturn(Optional.of(variance));

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances/WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cost-variances/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("variance"));
        }

        @Test
        @DisplayName("原価差異が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(costVarianceUseCase.getCostVariance("WO-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances/WO-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cost-variances"));
        }
    }

    @Nested
    @DisplayName("GET /cost-variances/{workOrderNumber}/edit - 原価差異編集画面")
    class EditCostVariance {

        @Test
        @DisplayName("原価差異編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            CostVariance variance = createTestCostVariance("WO-001", "ITEM-001");
            Mockito.when(costVarianceUseCase.getCostVariance("WO-001"))
                .thenReturn(Optional.of(variance));

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances/WO-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cost-variances/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrders"));
        }

        @Test
        @DisplayName("原価差異が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(costVarianceUseCase.getCostVariance("WO-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/cost-variances/WO-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cost-variances"));
        }
    }

    @Nested
    @DisplayName("POST /cost-variances/{workOrderNumber} - 原価差異更新処理")
    class UpdateCostVariance {

        @Test
        @DisplayName("原価差異を更新できる")
        void shouldUpdateCostVariance() throws Exception {
            CostVariance updated = createTestCostVariance("WO-001", "ITEM-001");
            Mockito.when(costVarianceUseCase.updateCostVariance(
                    ArgumentMatchers.eq("WO-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/cost-variances/WO-001")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("materialCostVariance", "6000")
                    .param("laborCostVariance", "4000")
                    .param("expenseVariance", "2500"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cost-variances"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /cost-variances/{workOrderNumber}/delete - 原価差異削除処理")
    class DeleteCostVariance {

        @Test
        @DisplayName("原価差異を削除できる")
        void shouldDeleteCostVariance() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/cost-variances/WO-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cost-variances"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(costVarianceUseCase).deleteCostVariance("WO-001");
        }
    }

    private CostVariance createTestCostVariance(String workOrderNumber, String itemCode) {
        return CostVariance.builder()
            .id(1)
            .workOrderNumber(workOrderNumber)
            .itemCode(itemCode)
            .materialCostVariance(new BigDecimal("5000"))
            .laborCostVariance(new BigDecimal("3000"))
            .expenseVariance(new BigDecimal("2000"))
            .totalVariance(new BigDecimal("10000"))
            .build();
    }
}
