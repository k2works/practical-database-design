package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.MonthlyBalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.domain.model.common.PageResult;
import java.math.BigDecimal;
import java.util.List;
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

/**
 * 月次残高照会画面コントローラーテスト.
 */
@WebMvcTest(MonthlyBalanceWebController.class)
@DisplayName("月次残高照会画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class MonthlyBalanceWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MonthlyBalanceUseCase monthlyBalanceUseCase;

    @Nested
    @DisplayName("GET /balances/monthly")
    class ListMonthlyBalances {

        @Test
        @DisplayName("月次残高一覧画面を表示できる")
        void shouldDisplayMonthlyBalanceList() throws Exception {
            MonthlyBalanceResponse response = createTestMonthlyBalance();
            PageResult<MonthlyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 0, 20, 1);
            Mockito.when(monthlyBalanceUseCase.getMonthlyBalances(
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/monthly"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/monthly/list"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("balances"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("決算期と月度でフィルタできる")
        void shouldFilterByFiscalYearAndMonth() throws Exception {
            MonthlyBalanceResponse response = createTestMonthlyBalance();
            PageResult<MonthlyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 0, 20, 1);
            Mockito.when(monthlyBalanceUseCase.getMonthlyBalances(
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.eq(2025),
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/monthly")
                            .param("fiscalYear", "2025")
                            .param("month", "1"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/monthly/list"))
                    .andExpect(MockMvcResultMatchers.model().attribute("fiscalYear", 2025))
                    .andExpect(MockMvcResultMatchers.model().attribute("month", 1));
        }

        @Test
        @DisplayName("勘定科目コードでフィルタできる")
        void shouldFilterByAccountCode() throws Exception {
            MonthlyBalanceResponse response = createTestMonthlyBalance();
            PageResult<MonthlyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 0, 20, 1);
            Mockito.when(monthlyBalanceUseCase.getMonthlyBalances(
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.eq("111")
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/monthly")
                            .param("accountCode", "111"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/monthly/list"))
                    .andExpect(MockMvcResultMatchers.model().attribute("accountCode", "111"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            MonthlyBalanceResponse response = createTestMonthlyBalance();
            PageResult<MonthlyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 1, 10, 15);
            Mockito.when(monthlyBalanceUseCase.getMonthlyBalances(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/monthly")
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }
    }

    private MonthlyBalanceResponse createTestMonthlyBalance() {
        return MonthlyBalanceResponse.builder()
                .fiscalYear(2025)
                .month(1)
                .accountCode("11110")
                .accountName("現金")
                .bsPlType("BS")
                .debitCreditType("借方")
                .openingBalance(new BigDecimal("100000"))
                .debitTotal(new BigDecimal("50000"))
                .creditTotal(new BigDecimal("10000"))
                .closingBalance(new BigDecimal("140000"))
                .build();
    }
}
