package com.example.fas.infrastructure.out.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.DailyAccountBalanceRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.balance.DailyAccountBalance;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 日次勘定科目残高リポジトリテスト.
 */
@DisplayName("日次勘定科目残高リポジトリ")
class DailyAccountBalanceRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private DailyAccountBalanceRepository dailyBalanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
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

    private DailyAccountBalance createTestBalance(LocalDate postingDate, String accountCode) {
        return DailyAccountBalance.builder()
                .postingDate(postingDate)
                .accountCode(accountCode)
                .subAccountCode("")
                .departmentCode("00000")
                .projectCode("")
                .closingJournalFlag(false)
                .debitAmount(new BigDecimal("100000"))
                .creditAmount(BigDecimal.ZERO)
                .build();
    }

    @Nested
    @DisplayName("日次残高の登録")
    class RegistrationTests {

        @Test
        @DisplayName("新規の日次残高を登録できる")
        void canRegisterNewDailyBalance() {
            // Arrange
            var balance = createTestBalance(LocalDate.of(2024, 4, 1), "11110");

            // Act
            dailyBalanceRepository.upsert(balance);
            var key = DailyAccountBalance.CompositeKey.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();
            var found = dailyBalanceRepository.findByKey(key);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getDebitAmount()).isEqualByComparingTo("100000");
            assertThat(found.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("既存の日次残高に金額を加算できる（UPSERT）")
        void canAddAmountToExistingBalance() {
            // Arrange
            var balance = createTestBalance(LocalDate.of(2024, 4, 1), "11110");
            dailyBalanceRepository.upsert(balance);

            // Act: 同じキーで追加
            var additionalBalance = DailyAccountBalance.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .debitAmount(new BigDecimal("50000"))
                    .creditAmount(BigDecimal.ZERO)
                    .build();
            dailyBalanceRepository.upsert(additionalBalance);

            // Assert
            var key = DailyAccountBalance.CompositeKey.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();
            var found = dailyBalanceRepository.findByKey(key);
            assertThat(found).isPresent();
            assertThat(found.get().getDebitAmount()).isEqualByComparingTo("150000");
            assertThat(found.get().getVersion()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("日次残高の検索")
    class SearchTests {

        @Test
        @DisplayName("起票日で検索できる")
        void canFindByPostingDate() {
            // Arrange
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 1), "11110"));
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 1), "11300"));
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 2), "11110"));

            // Act
            var results = dailyBalanceRepository.findByPostingDate(LocalDate.of(2024, 4, 1));

            // Assert
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("勘定科目コードと期間で検索できる")
        void canFindByAccountCodeAndDateRange() {
            // Arrange
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 1), "11110"));
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 15), "11110"));
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 5, 1), "11110"));

            // Act
            var results = dailyBalanceRepository.findByAccountCodeAndDateRange(
                    "11110",
                    LocalDate.of(2024, 4, 1),
                    LocalDate.of(2024, 4, 30));

            // Assert
            assertThat(results).hasSize(2);
        }
    }

    @Nested
    @DisplayName("日計表")
    class DailyReportTests {

        @Test
        @DisplayName("日計表データを取得できる")
        void canGetDailyReport() {
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
            dailyBalanceRepository.upsert(balance);

            // Act
            var report = dailyBalanceRepository.getDailyReport(LocalDate.of(2024, 4, 1));

            // Assert
            assertThat(report).hasSize(1);
            assertThat(report.get(0).getAccountCode()).isEqualTo("11110");
            assertThat(report.get(0).getAccountName()).isEqualTo("現金");
            assertThat(report.get(0).getDebitTotal()).isEqualByComparingTo("100000");
            assertThat(report.get(0).getCreditTotal()).isEqualByComparingTo("30000");
            assertThat(report.get(0).getBalance()).isEqualByComparingTo("70000");
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLockTests {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            // Arrange
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 1), "11110"));

            var key = DailyAccountBalance.CompositeKey.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();
            var fetched = dailyBalanceRepository.findByKey(key).orElseThrow();
            fetched.setDebitAmount(new BigDecimal("200000"));

            // Act
            dailyBalanceRepository.updateWithOptimisticLock(fetched);

            // Assert
            var updated = dailyBalanceRepository.findByKey(key).orElseThrow();
            assertThat(updated.getDebitAmount()).isEqualByComparingTo("200000");
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            // Arrange
            dailyBalanceRepository.upsert(createTestBalance(LocalDate.of(2024, 4, 1), "11110"));

            var key = DailyAccountBalance.CompositeKey.builder()
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .accountCode("11110")
                    .subAccountCode("")
                    .departmentCode("00000")
                    .projectCode("")
                    .closingJournalFlag(false)
                    .build();

            var balanceA = dailyBalanceRepository.findByKey(key).orElseThrow();
            var balanceB = dailyBalanceRepository.findByKey(key).orElseThrow();

            // 処理Aが更新（成功）
            balanceA.setDebitAmount(new BigDecimal("200000"));
            dailyBalanceRepository.updateWithOptimisticLock(balanceA);

            // Act & Assert: 処理Bが古いバージョンで更新（失敗）
            balanceB.setDebitAmount(new BigDecimal("300000"));
            assertThatThrownBy(() -> dailyBalanceRepository.updateWithOptimisticLock(balanceB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }
    }
}
