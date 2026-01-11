package com.example.fas.domain.model.balance;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fas.domain.model.account.DebitCreditType;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 勘定科目残高ドメインモデルテスト.
 */
@DisplayName("勘定科目残高ドメインモデル")
class AccountBalanceTest {

    @Nested
    @DisplayName("日次残高")
    class DailyAccountBalanceTest {

        @Test
        @DisplayName("残高を計算できる（借方 - 貸方）")
        void canCalculateBalance() {
            // Arrange
            var balance = DailyAccountBalance.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .debitAmount(new BigDecimal("100000"))
                    .creditAmount(new BigDecimal("30000"))
                    .build();

            // Act & Assert
            assertThat(balance.getBalance()).isEqualByComparingTo("70000");
        }

        @Test
        @DisplayName("借方科目の残高を取得できる")
        void canGetBalanceForDebitType() {
            // Arrange
            var balance = DailyAccountBalance.builder()
                    .debitAmount(new BigDecimal("100000"))
                    .creditAmount(new BigDecimal("30000"))
                    .build();

            // Act & Assert
            assertThat(balance.getBalanceByType(DebitCreditType.DEBIT))
                    .isEqualByComparingTo("70000");
        }

        @Test
        @DisplayName("貸方科目の残高を取得できる")
        void canGetBalanceForCreditType() {
            // Arrange
            var balance = DailyAccountBalance.builder()
                    .debitAmount(new BigDecimal("30000"))
                    .creditAmount(new BigDecimal("100000"))
                    .build();

            // Act & Assert
            assertThat(balance.getBalanceByType(DebitCreditType.CREDIT))
                    .isEqualByComparingTo("70000");
        }
    }

    @Nested
    @DisplayName("月次残高")
    class MonthlyAccountBalanceTest {

        @Test
        @DisplayName("借方科目の月末残高を再計算できる")
        void canRecalculateClosingBalanceForDebitAccount() {
            // Arrange
            var balance = MonthlyAccountBalance.builder()
                    .openingBalance(new BigDecimal("100000"))
                    .debitAmount(new BigDecimal("50000"))
                    .creditAmount(new BigDecimal("20000"))
                    .build();

            // Act & Assert
            // 借方科目: 月初残高 + 借方金額 - 貸方金額
            assertThat(balance.recalculateClosingBalance(DebitCreditType.DEBIT))
                    .isEqualByComparingTo("130000");
        }

        @Test
        @DisplayName("貸方科目の月末残高を再計算できる")
        void canRecalculateClosingBalanceForCreditAccount() {
            // Arrange
            var balance = MonthlyAccountBalance.builder()
                    .openingBalance(new BigDecimal("100000"))
                    .debitAmount(new BigDecimal("20000"))
                    .creditAmount(new BigDecimal("50000"))
                    .build();

            // Act & Assert
            // 貸方科目: 月初残高 - 借方金額 + 貸方金額
            assertThat(balance.recalculateClosingBalance(DebitCreditType.CREDIT))
                    .isEqualByComparingTo("130000");
        }

        @Test
        @DisplayName("当月の増減額を取得できる")
        void canGetNetChange() {
            // Arrange
            var balance = MonthlyAccountBalance.builder()
                    .openingBalance(new BigDecimal("100000"))
                    .closingBalance(new BigDecimal("130000"))
                    .build();

            // Act & Assert
            assertThat(balance.getNetChange()).isEqualByComparingTo("30000");
        }
    }

    @Nested
    @DisplayName("日計表行")
    class DailyReportLineTest {

        @Test
        @DisplayName("借方科目の残高を計算できる")
        void canCalculateBalanceForDebitAccount() {
            // Arrange
            var line = DailyReportLine.builder()
                    .debitCreditType("借方")
                    .debitTotal(new BigDecimal("100000"))
                    .creditTotal(new BigDecimal("30000"))
                    .build();

            // Act & Assert
            assertThat(line.calculateBalance()).isEqualByComparingTo("70000");
        }

        @Test
        @DisplayName("貸方科目の残高を計算できる")
        void canCalculateBalanceForCreditAccount() {
            // Arrange
            var line = DailyReportLine.builder()
                    .debitCreditType("貸方")
                    .debitTotal(new BigDecimal("30000"))
                    .creditTotal(new BigDecimal("100000"))
                    .build();

            // Act & Assert
            assertThat(line.calculateBalance()).isEqualByComparingTo("70000");
        }
    }

    @Nested
    @DisplayName("合計残高試算表行")
    class TrialBalanceLineTest {

        @Test
        @DisplayName("累計残高を取得できる")
        void canGetCumulativeBalance() {
            // Arrange
            var line = TrialBalanceLine.builder()
                    .closingBalance(new BigDecimal("130000"))
                    .build();

            // Act & Assert
            assertThat(line.getCumulativeBalance()).isEqualByComparingTo("130000");
        }

        @Test
        @DisplayName("当月増減を取得できる")
        void canGetMonthlyChange() {
            // Arrange
            var line = TrialBalanceLine.builder()
                    .openingBalance(new BigDecimal("100000"))
                    .closingBalance(new BigDecimal("130000"))
                    .build();

            // Act & Assert
            assertThat(line.getMonthlyChange()).isEqualByComparingTo("30000");
        }
    }
}
