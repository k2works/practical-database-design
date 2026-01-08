package com.example.fas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
import com.example.fas.application.port.out.MonthlyAccountBalanceRepository;
import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * BalanceApplicationService のユニットテスト.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("残高照会アプリケーションサービス")
class BalanceApplicationServiceTest {

    @Mock
    private MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;

    @InjectMocks
    private BalanceApplicationService balanceApplicationService;

    private TrialBalanceLine testTrialBalanceLine;
    private MonthlyAccountBalance testMonthlyBalance;

    @BeforeEach
    void setUp() {
        testTrialBalanceLine = TrialBalanceLine.builder()
                .fiscalYear(2025)
                .month(1)
                .accountCode("11110")
                .accountName("現金")
                .bsplType("BS")
                .debitCreditType("借方")
                .openingBalance(new BigDecimal("100000"))
                .debitTotal(new BigDecimal("50000"))
                .creditTotal(new BigDecimal("30000"))
                .closingBalance(new BigDecimal("120000"))
                .build();

        testMonthlyBalance = MonthlyAccountBalance.builder()
                .fiscalYear(2025)
                .month(1)
                .accountCode("11110")
                .openingBalance(new BigDecimal("100000"))
                .debitAmount(new BigDecimal("50000"))
                .creditAmount(new BigDecimal("30000"))
                .closingBalance(new BigDecimal("120000"))
                .build();
    }

    @Nested
    @DisplayName("getTrialBalance")
    class GetTrialBalanceTest {

        @Test
        @DisplayName("合計残高試算表を取得できる")
        void canGetTrialBalance() {
            // Given
            when(monthlyAccountBalanceRepository.getTrialBalance(2025, 1))
                    .thenReturn(List.of(testTrialBalanceLine));

            // When
            TrialBalanceResponse response = balanceApplicationService.getTrialBalance(2025, 1);

            // Then
            assertThat(response.getFiscalYear()).isEqualTo(2025);
            assertThat(response.getMonth()).isEqualTo(1);
            assertThat(response.getLines()).hasSize(1);
            assertThat(response.getLines().get(0).getAccountCode()).isEqualTo("11110");
        }
    }

    @Nested
    @DisplayName("getTrialBalanceByBsPlType")
    class GetTrialBalanceByBsPlTypeTest {

        @Test
        @DisplayName("BSPL区分別の試算表を取得できる")
        void canGetTrialBalanceByBsPlType() {
            // Given
            when(monthlyAccountBalanceRepository.getTrialBalanceByBSPL(2025, 1, "BS"))
                    .thenReturn(List.of(testTrialBalanceLine));

            // When
            TrialBalanceResponse response =
                    balanceApplicationService.getTrialBalanceByBsPlType(2025, 1, "BS");

            // Then
            assertThat(response.getLines()).hasSize(1);
            assertThat(response.getLines().get(0).getBsPlType()).isEqualTo("BS");
        }
    }

    @Nested
    @DisplayName("getMonthlyBalances")
    class GetMonthlyBalancesTest {

        @Test
        @DisplayName("月次残高一覧を取得できる")
        void canGetMonthlyBalances() {
            // Given
            when(monthlyAccountBalanceRepository.findByFiscalYearAndMonth(2025, 1))
                    .thenReturn(List.of(testMonthlyBalance));

            // When
            List<MonthlyBalanceResponse> responses =
                    balanceApplicationService.getMonthlyBalances(2025, 1);

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getAccountCode()).isEqualTo("11110");
            assertThat(responses.get(0).getClosingBalance())
                    .isEqualByComparingTo(new BigDecimal("120000"));
        }
    }

    @Nested
    @DisplayName("getMonthlyBalancesByAccountCode")
    class GetMonthlyBalancesByAccountCodeTest {

        @Test
        @DisplayName("勘定科目別の月次残高推移を取得できる")
        void canGetMonthlyBalancesByAccountCode() {
            // Given
            when(monthlyAccountBalanceRepository.findByAccountCode(2025, "11110"))
                    .thenReturn(List.of(testMonthlyBalance));

            // When
            List<MonthlyBalanceResponse> responses =
                    balanceApplicationService.getMonthlyBalancesByAccountCode(2025, "11110");

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getAccountCode()).isEqualTo("11110");
        }
    }
}
