package com.example.fas.infrastructure.in.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fas.application.port.in.BalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse.TrialBalanceLineResponse;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * BalanceController の統合テスト.
 */
@AutoConfigureMockMvc
@DisplayName("残高照会 API")
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.UseUnderscoresInNumericLiterals"})
class BalanceControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BalanceUseCase balanceUseCase;

    @Nested
    @DisplayName("GET /api/balances/trial-balance")
    class GetTrialBalanceTest {

        @Test
        @DisplayName("合計残高試算表を取得できる")
        void canGetTrialBalance() throws Exception {
            // Given
            TrialBalanceLineResponse line = TrialBalanceLineResponse.builder()
                    .accountCode("11110")
                    .accountName("現金")
                    .bsPlType("BS")
                    .dcType("借方")
                    .openingBalance(new BigDecimal("100000"))
                    .debitTotal(new BigDecimal("50000"))
                    .creditTotal(new BigDecimal("30000"))
                    .closingBalance(new BigDecimal("120000"))
                    .build();

            TrialBalanceResponse response = TrialBalanceResponse.builder()
                    .fiscalYear(2025)
                    .month(1)
                    .lines(List.of(line))
                    .totalDebit(new BigDecimal("50000"))
                    .totalCredit(new BigDecimal("30000"))
                    .build();

            when(balanceUseCase.getTrialBalance(2025, 1)).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/balances/trial-balance")
                            .param("fiscalYear", "2025")
                            .param("month", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fiscalYear").value(2025))
                    .andExpect(jsonPath("$.month").value(1))
                    .andExpect(jsonPath("$.lines").isArray())
                    .andExpect(jsonPath("$.lines[0].accountCode").value("11110"))
                    .andExpect(jsonPath("$.totalDebit").value(50000))
                    .andExpect(jsonPath("$.totalCredit").value(30000));
        }

        @Test
        @DisplayName("BSPL区分指定で試算表を取得できる")
        void canGetTrialBalanceByBsPlType() throws Exception {
            // Given
            TrialBalanceLineResponse line = TrialBalanceLineResponse.builder()
                    .accountCode("11110")
                    .accountName("現金")
                    .bsPlType("BS")
                    .dcType("借方")
                    .openingBalance(new BigDecimal("100000"))
                    .debitTotal(new BigDecimal("50000"))
                    .creditTotal(new BigDecimal("30000"))
                    .closingBalance(new BigDecimal("120000"))
                    .build();

            TrialBalanceResponse response = TrialBalanceResponse.builder()
                    .fiscalYear(2025)
                    .month(1)
                    .lines(List.of(line))
                    .totalDebit(new BigDecimal("50000"))
                    .totalCredit(new BigDecimal("30000"))
                    .build();

            when(balanceUseCase.getTrialBalanceByBsPlType(2025, 1, "BS")).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/balances/trial-balance")
                            .param("fiscalYear", "2025")
                            .param("month", "1")
                            .param("bsPlType", "BS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lines[0].bsPlType").value("BS"));
        }
    }

    @Nested
    @DisplayName("GET /api/balances/monthly")
    class GetMonthlyBalancesTest {

        @Test
        @DisplayName("月次残高一覧を取得できる")
        void canGetMonthlyBalances() throws Exception {
            // Given
            MonthlyBalanceResponse balance = MonthlyBalanceResponse.builder()
                    .fiscalYear(2025)
                    .month(1)
                    .accountCode("11110")
                    .openingBalance(new BigDecimal("100000"))
                    .debitAmount(new BigDecimal("50000"))
                    .creditAmount(new BigDecimal("30000"))
                    .closingBalance(new BigDecimal("120000"))
                    .build();

            when(balanceUseCase.getMonthlyBalances(2025, 1)).thenReturn(List.of(balance));

            // When & Then
            mockMvc.perform(get("/api/balances/monthly")
                            .param("fiscalYear", "2025")
                            .param("month", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].accountCode").value("11110"))
                    .andExpect(jsonPath("$[0].closingBalance").value(120000));
        }
    }

    @Nested
    @DisplayName("GET /api/balances/monthly/account/{accountCode}")
    class GetMonthlyBalancesByAccountCodeTest {

        @Test
        @DisplayName("勘定科目別の月次残高推移を取得できる")
        void canGetMonthlyBalancesByAccountCode() throws Exception {
            // Given
            MonthlyBalanceResponse balance1 = MonthlyBalanceResponse.builder()
                    .fiscalYear(2025)
                    .month(1)
                    .accountCode("11110")
                    .openingBalance(new BigDecimal("100000"))
                    .debitAmount(new BigDecimal("50000"))
                    .creditAmount(new BigDecimal("30000"))
                    .closingBalance(new BigDecimal("120000"))
                    .build();

            MonthlyBalanceResponse balance2 = MonthlyBalanceResponse.builder()
                    .fiscalYear(2025)
                    .month(2)
                    .accountCode("11110")
                    .openingBalance(new BigDecimal("120000"))
                    .debitAmount(new BigDecimal("40000"))
                    .creditAmount(new BigDecimal("20000"))
                    .closingBalance(new BigDecimal("140000"))
                    .build();

            when(balanceUseCase.getMonthlyBalancesByAccountCode(2025, "11110"))
                    .thenReturn(List.of(balance1, balance2));

            // When & Then
            mockMvc.perform(get("/api/balances/monthly/account/11110")
                            .param("fiscalYear", "2025"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].month").value(1))
                    .andExpect(jsonPath("$[1].month").value(2));
        }
    }
}
