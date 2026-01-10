package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.DailyBalanceUseCase;
import com.example.fas.application.port.in.dto.DailyBalanceResponse;
import com.example.fas.domain.model.common.PageResult;
import java.math.BigDecimal;
import java.time.LocalDate;
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
 * 日次残高照会画面コントローラーテスト.
 */
@WebMvcTest(DailyBalanceWebController.class)
@DisplayName("日次残高照会画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class DailyBalanceWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DailyBalanceUseCase dailyBalanceUseCase;

    @Nested
    @DisplayName("GET /balances/daily")
    class ListDailyBalances {

        @Test
        @DisplayName("日次残高一覧画面を表示できる")
        void shouldDisplayDailyBalanceList() throws Exception {
            DailyBalanceResponse response = createTestDailyBalance();
            PageResult<DailyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 0, 20, 1);
            Mockito.when(dailyBalanceUseCase.getDailyBalances(
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/daily"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/daily/list"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("balances"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("日付範囲でフィルタできる")
        void shouldFilterByDateRange() throws Exception {
            DailyBalanceResponse response = createTestDailyBalance();
            PageResult<DailyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 0, 20, 1);
            LocalDate fromDate = LocalDate.of(2025, 1, 1);
            LocalDate toDate = LocalDate.of(2025, 1, 31);
            Mockito.when(dailyBalanceUseCase.getDailyBalances(
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.eq(fromDate),
                    ArgumentMatchers.eq(toDate),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/daily")
                            .param("fromDate", "2025-01-01")
                            .param("toDate", "2025-01-31"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/daily/list"))
                    .andExpect(MockMvcResultMatchers.model().attribute("fromDate", fromDate))
                    .andExpect(MockMvcResultMatchers.model().attribute("toDate", toDate));
        }

        @Test
        @DisplayName("勘定科目コードでフィルタできる")
        void shouldFilterByAccountCode() throws Exception {
            DailyBalanceResponse response = createTestDailyBalance();
            PageResult<DailyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 0, 20, 1);
            Mockito.when(dailyBalanceUseCase.getDailyBalances(
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.eq("111")
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/daily")
                            .param("accountCode", "111"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/daily/list"))
                    .andExpect(MockMvcResultMatchers.model().attribute("accountCode", "111"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            DailyBalanceResponse response = createTestDailyBalance();
            PageResult<DailyBalanceResponse> pageResult = new PageResult<>(
                    List.of(response), 1, 10, 15);
            Mockito.when(dailyBalanceUseCase.getDailyBalances(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/daily")
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("balances/daily/list"))
                    .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }

        @Test
        @DisplayName("ページサイズの上限を100に制限する")
        void shouldLimitPageSizeTo100() throws Exception {
            PageResult<DailyBalanceResponse> pageResult = new PageResult<>(
                    List.of(), 0, 100, 0);
            Mockito.when(dailyBalanceUseCase.getDailyBalances(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(100),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/balances/daily")
                            .param("size", "500"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 100));
        }
    }

    private DailyBalanceResponse createTestDailyBalance() {
        return DailyBalanceResponse.builder()
                .postingDate(LocalDate.of(2025, 1, 15))
                .accountCode("11110")
                .accountName("現金")
                .bsPlType("BS")
                .debitCreditType("借方")
                .debitTotal(new BigDecimal("50000"))
                .creditTotal(new BigDecimal("10000"))
                .balance(new BigDecimal("40000"))
                .build();
    }
}
