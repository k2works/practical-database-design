package com.example.fas.infrastructure.out.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.DailyAccountBalanceRepository;
import com.example.fas.application.port.out.MonthlyAccountBalanceRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.balance.DailyAccountBalance;
import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 月次勘定科目残高リポジトリテスト.
 */
@DisplayName("月次勘定科目残高リポジトリ")
class MonthlyAccountBalanceRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private MonthlyAccountBalanceRepository monthlyBalanceRepository;

    @Autowired
    private DailyAccountBalanceRepository dailyBalanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        monthlyBalanceRepository.deleteAll();
        dailyBalanceRepository.deleteAll();
        accountRepository.deleteAll();
        setUpMasterData();
    }

    private void setUpMasterData() {
        accountRepository.save(createAccount("11110", "現金", BSPLType.BS,
                DebitCreditType.DEBIT, TransactionElementType.ASSET));
        accountRepository.save(createAccount("11300", "売掛金", BSPLType.BS,
                DebitCreditType.DEBIT, TransactionElementType.ASSET));
        accountRepository.save(createAccount("41100", "売上高", BSPLType.PL,
                DebitCreditType.CREDIT, TransactionElementType.REVENUE));
    }

    private Account createAccount(String code, String name, BSPLType bsplType,
                                  DebitCreditType debitCreditType,
                                  TransactionElementType transactionElementType) {
        return Account.builder()
                .accountCode(code)
                .accountName(name)
                .accountShortName(name)
                .accountNameKana("カナ")
                .bsplType(bsplType)
                .debitCreditType(debitCreditType)
                .transactionElementType(transactionElementType)
                .aggregationType(AggregationType.POSTING)
                .managementAccountingType("1")
                .expenseType("1")
                .ledgerOutputType("1")
                .subAccountType("1")
                .consumptionTaxType("課税")
                .taxTransactionCode("10")
                .dueDateManagementType("1")
                .updatedBy("テストユーザー")
                .build();
    }

    private MonthlyAccountBalance createTestBalance(int month, String accountCode) {
        return MonthlyAccountBalance.builder()
                .fiscalYear(2024)
                .month(month)
                .accountCode(accountCode)
                .subAccountCode("")
                .departmentCode("00000")
                .projectCode("")
                .closingJournalFlag(false)
                .openingBalance(new BigDecimal("100000"))
                .debitAmount(new BigDecimal("50000"))
                .creditAmount(new BigDecimal("20000"))
                .closingBalance(new BigDecimal("130000"))
                .build();
    }

    @Nested
    @DisplayName("月次残高の登録と検索")
    class RegistrationTests {

        @Test
        @DisplayName("月次残高を登録して取得できる")
        void canSaveAndFind() {
            // Arrange
            var balance = createTestBalance(4, "11110");

            // Act
            monthlyBalanceRepository.save(balance);
            var key = MonthlyAccountBalance.CompositeKey.builder()
                    .fiscalYear(2024)
                    .month(4)
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();
            var found = monthlyBalanceRepository.findByKey(key);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getOpeningBalance()).isEqualByComparingTo("100000");
            assertThat(found.get().getClosingBalance()).isEqualByComparingTo("130000");
        }

        @Test
        @DisplayName("決算期と月度で検索できる")
        void canFindByFiscalYearAndMonth() {
            // Arrange
            monthlyBalanceRepository.save(createTestBalance(4, "11110"));
            monthlyBalanceRepository.save(createTestBalance(4, "11300"));
            monthlyBalanceRepository.save(createTestBalance(5, "11110"));

            // Act
            var results = monthlyBalanceRepository.findByFiscalYearAndMonth(2024, 4);

            // Assert
            assertThat(results).hasSize(2);
        }
    }

    @Nested
    @DisplayName("月次残高の繰越処理")
    class CarryForwardTests {

        @Test
        @DisplayName("前月末残高を翌月初残高として繰越できる")
        void canCarryForwardToNextMonth() {
            // Arrange: 4月末残高が存在
            monthlyBalanceRepository.save(createTestBalance(4, "11110"));

            // Act: 5月の月初残高として繰越
            int count = monthlyBalanceRepository.carryForward(2024, 4, 5);

            // Assert
            assertThat(count).isGreaterThan(0);
            var mayBalance = monthlyBalanceRepository.findByKey(
                    MonthlyAccountBalance.CompositeKey.builder()
                            .fiscalYear(2024)
                            .month(5)
                            .accountCode("11110")
                            .subAccountCode("")
                            .departmentCode("00000")
                            .projectCode("")
                            .closingJournalFlag(false)
                            .build());
            assertThat(mayBalance).isPresent();
            assertThat(mayBalance.get().getOpeningBalance()).isEqualByComparingTo("130000");
        }
    }

    @Nested
    @DisplayName("日次残高からの集計")
    class AggregationTests {

        @Test
        @DisplayName("日次残高から月次残高を集計できる")
        void canAggregateFromDaily() {
            // Arrange: 日次残高データを作成
            dailyBalanceRepository.upsert(DailyAccountBalance.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .debitAmount(new BigDecimal("100000"))
                    .creditAmount(BigDecimal.ZERO)
                    .build());

            dailyBalanceRepository.upsert(DailyAccountBalance.builder()
                    .postingDate(LocalDate.of(2024, 4, 15))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .debitAmount(new BigDecimal("50000"))
                    .creditAmount(new BigDecimal("30000"))
                    .build());

            // Act
            int count = monthlyBalanceRepository.aggregateFromDaily(
                    2024, 4,
                    LocalDate.of(2024, 4, 1),
                    LocalDate.of(2024, 4, 30));

            // Assert
            assertThat(count).isGreaterThan(0);
            var monthlyBalance = monthlyBalanceRepository.findByKey(
                    MonthlyAccountBalance.CompositeKey.builder()
                            .fiscalYear(2024)
                            .month(4)
                            .accountCode("11110")
                            .subAccountCode("")
                            .departmentCode("00000")
                            .projectCode("")
                            .closingJournalFlag(false)
                            .build());
            assertThat(monthlyBalance).isPresent();
            assertThat(monthlyBalance.get().getDebitAmount()).isEqualByComparingTo("150000");
            assertThat(monthlyBalance.get().getCreditAmount()).isEqualByComparingTo("30000");
        }
    }

    @Nested
    @DisplayName("合計残高試算表")
    class TrialBalanceTests {

        @Test
        @DisplayName("合計残高試算表データを取得できる")
        void canGetTrialBalance() {
            // Arrange
            monthlyBalanceRepository.save(createTestBalance(4, "11110"));

            // Act
            var trialBalance = monthlyBalanceRepository.getTrialBalance(2024, 4);

            // Assert
            assertThat(trialBalance).isNotEmpty();
            assertThat(trialBalance.get(0).getAccountCode()).isEqualTo("11110");
            assertThat(trialBalance.get(0).getAccountName()).isEqualTo("現金");
        }

        @Test
        @DisplayName("BSPL区分別試算表データを取得できる")
        void canGetTrialBalanceByBSPL() {
            // Arrange
            monthlyBalanceRepository.save(createTestBalance(4, "11110")); // BS
            monthlyBalanceRepository.save(createTestBalance(4, "41100")); // PL

            // Act
            var bsTrialBalance = monthlyBalanceRepository.getTrialBalanceByBSPL(2024, 4, "BS");
            var plTrialBalance = monthlyBalanceRepository.getTrialBalanceByBSPL(2024, 4, "PL");

            // Assert
            assertThat(bsTrialBalance).hasSize(1);
            assertThat(plTrialBalance).hasSize(1);
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLockTests {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            // Arrange
            monthlyBalanceRepository.save(createTestBalance(4, "11110"));

            var key = MonthlyAccountBalance.CompositeKey.builder()
                    .fiscalYear(2024)
                    .month(4)
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();
            var fetched = monthlyBalanceRepository.findByKey(key).orElseThrow();
            fetched.setClosingBalance(new BigDecimal("200000"));

            // Act
            monthlyBalanceRepository.updateWithOptimisticLock(fetched);

            // Assert
            var updated = monthlyBalanceRepository.findByKey(key).orElseThrow();
            assertThat(updated.getClosingBalance()).isEqualByComparingTo("200000");
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            // Arrange
            monthlyBalanceRepository.save(createTestBalance(4, "11110"));

            var key = MonthlyAccountBalance.CompositeKey.builder()
                    .fiscalYear(2024)
                    .month(4)
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();

            var balanceA = monthlyBalanceRepository.findByKey(key).orElseThrow();
            var balanceB = monthlyBalanceRepository.findByKey(key).orElseThrow();

            // 処理Aが更新（成功）
            balanceA.setClosingBalance(new BigDecimal("200000"));
            monthlyBalanceRepository.updateWithOptimisticLock(balanceA);

            // Act & Assert: 処理Bが古いバージョンで更新（失敗）
            balanceB.setClosingBalance(new BigDecimal("300000"));
            assertThatThrownBy(() -> monthlyBalanceRepository.updateWithOptimisticLock(balanceB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }
    }
}
