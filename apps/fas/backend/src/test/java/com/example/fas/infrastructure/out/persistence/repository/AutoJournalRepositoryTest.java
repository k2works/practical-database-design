package com.example.fas.infrastructure.out.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.AutoJournalRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.autojournal.AutoJournalEntry;
import com.example.fas.domain.model.autojournal.AutoJournalHistory;
import com.example.fas.domain.model.autojournal.AutoJournalPattern;
import com.example.fas.domain.model.autojournal.AutoJournalStatus;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自動仕訳リポジトリテスト.
 */
@DisplayName("自動仕訳リポジトリ")
class AutoJournalRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AutoJournalRepository autoJournalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        autoJournalRepository.deleteAllHistories();
        autoJournalRepository.deleteAllEntries();
        autoJournalRepository.deleteAllPatterns();
        accountRepository.deleteAll();
        setUpMasterData();
    }

    private void setUpMasterData() {
        accountRepository.save(createAccount("11300", "売掛金", BSPLType.BS,
                DebitCreditType.DEBIT, TransactionElementType.ASSET));
        accountRepository.save(createAccount("41100", "売上高", BSPLType.PL,
                DebitCreditType.CREDIT, TransactionElementType.REVENUE));
        accountRepository.save(createAccount("41110", "売上加工品", BSPLType.PL,
                DebitCreditType.CREDIT, TransactionElementType.REVENUE));
        accountRepository.save(createAccount("41120", "売上生鮮品", BSPLType.PL,
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

    private AutoJournalPattern createTestPattern(String patternCode) {
        return AutoJournalPattern.builder()
                .patternCode(patternCode)
                .patternName("テストパターン")
                .productGroup("ALL")
                .customerGroup("ALL")
                .salesType("01")
                .debitAccountCode("11300")
                .creditAccountCode("41100")
                .taxProcessingType("01")
                .validFrom(LocalDate.of(2024, 1, 1))
                .validTo(LocalDate.of(9999, 12, 31))
                .priority(100)
                .build();
    }

    private AutoJournalEntry createTestEntry(String autoJournalNumber, String patternCode) {
        return AutoJournalEntry.builder()
                .autoJournalNumber(autoJournalNumber)
                .salesNumber("S0001")
                .salesLineNumber(1)
                .patternCode(patternCode)
                .postingDate(LocalDate.of(2024, 4, 1))
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11300")
                .amount(new BigDecimal("100000"))
                .taxAmount(new BigDecimal("10000"))
                .status(AutoJournalStatus.PENDING)
                .postedFlag(false)
                .build();
    }

    @Nested
    @DisplayName("パターンマスタ操作")
    class PatternOperations {

        @Test
        @DisplayName("パターンを登録して取得できる")
        void canSaveAndFindPattern() {
            // Arrange
            var pattern = createTestPattern("P001");

            // Act
            autoJournalRepository.savePattern(pattern);
            var found = autoJournalRepository.findPatternByCode("P001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getPatternName()).isEqualTo("テストパターン");
            assertThat(found.get().getDebitAccountCode()).isEqualTo("11300");
        }

        @Test
        @DisplayName("有効なパターンを取得できる")
        void canFindValidPatterns() {
            // Arrange
            var activePattern = AutoJournalPattern.builder()
                    .patternCode("P001")
                    .patternName("有効パターン")
                    .productGroup("ALL")
                    .customerGroup("ALL")
                    .debitAccountCode("11300")
                    .creditAccountCode("41100")
                    .validFrom(LocalDate.of(2024, 1, 1))
                    .validTo(LocalDate.of(2024, 12, 31))
                    .priority(100)
                    .build();

            var expiredPattern = AutoJournalPattern.builder()
                    .patternCode("P002")
                    .patternName("無効パターン")
                    .productGroup("ALL")
                    .customerGroup("ALL")
                    .debitAccountCode("11300")
                    .creditAccountCode("41100")
                    .validFrom(LocalDate.of(2023, 1, 1))
                    .validTo(LocalDate.of(2023, 12, 31))
                    .priority(100)
                    .build();

            autoJournalRepository.savePattern(activePattern);
            autoJournalRepository.savePattern(expiredPattern);

            // Act
            var validPatterns = autoJournalRepository.findValidPatterns(LocalDate.of(2024, 6, 15));

            // Assert
            assertThat(validPatterns).hasSize(1);
            assertThat(validPatterns.get(0).getPatternCode()).isEqualTo("P001");
        }

        @Test
        @DisplayName("パターンを更新できる（楽観ロック）")
        void canUpdatePatternWithOptimisticLock() {
            // Arrange
            var pattern = createTestPattern("P001");
            autoJournalRepository.savePattern(pattern);

            var saved = autoJournalRepository.findPatternByCode("P001").orElseThrow();
            saved.setPriority(50);

            // Act
            autoJournalRepository.updatePattern(saved);

            // Assert
            var updated = autoJournalRepository.findPatternByCode("P001").orElseThrow();
            assertThat(updated.getPriority()).isEqualTo(50);
            assertThat(updated.getVersion()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("自動仕訳エントリ操作")
    class EntryOperations {

        @BeforeEach
        void setUpPattern() {
            autoJournalRepository.savePattern(createTestPattern("P001"));
        }

        @Test
        @DisplayName("エントリを登録して取得できる")
        void canSaveAndFindEntry() {
            // Arrange
            var entry = createTestEntry("AJ0001", "P001");

            // Act
            autoJournalRepository.saveEntry(entry);
            var found = autoJournalRepository.findEntryByNumber("AJ0001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getSalesNumber()).isEqualTo("S0001");
            assertThat(found.get().getAmount()).isEqualByComparingTo(new BigDecimal("100000"));
        }

        @Test
        @DisplayName("売上番号でエントリを検索できる")
        void canFindEntriesBySalesNumber() {
            // Arrange
            autoJournalRepository.saveEntry(createTestEntry("AJ0001", "P001"));
            autoJournalRepository.saveEntry(AutoJournalEntry.builder()
                    .autoJournalNumber("AJ0002")
                    .salesNumber("S0001")
                    .salesLineNumber(2)
                    .patternCode("P001")
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .debitCreditType(DebitCreditType.CREDIT)
                    .accountCode("41100")
                    .amount(new BigDecimal("100000"))
                    .status(AutoJournalStatus.PENDING)
                    .postedFlag(false)
                    .build());

            // Act
            var entries = autoJournalRepository.findEntriesBySalesNumber("S0001");

            // Assert
            assertThat(entries).hasSize(2);
        }

        @Test
        @DisplayName("ステータスでエントリを検索できる")
        void canFindEntriesByStatus() {
            // Arrange
            autoJournalRepository.saveEntry(createTestEntry("AJ0001", "P001"));

            var completedEntry = AutoJournalEntry.builder()
                    .autoJournalNumber("AJ0002")
                    .salesNumber("S0002")
                    .salesLineNumber(1)
                    .patternCode("P001")
                    .postingDate(LocalDate.of(2024, 4, 1))
                    .debitCreditType(DebitCreditType.DEBIT)
                    .accountCode("11300")
                    .amount(new BigDecimal("50000"))
                    .status(AutoJournalStatus.COMPLETED)
                    .postedFlag(false)
                    .build();
            autoJournalRepository.saveEntry(completedEntry);

            // Act
            var pendingEntries = autoJournalRepository.findEntriesByStatus(AutoJournalStatus.PENDING);
            var completedEntries = autoJournalRepository
                    .findEntriesByStatus(AutoJournalStatus.COMPLETED);

            // Assert
            assertThat(pendingEntries).hasSize(1);
            assertThat(completedEntries).hasSize(1);
        }

        @Test
        @DisplayName("パターン情報付きでエントリを取得できる")
        void canFindEntryWithPattern() {
            // Arrange
            autoJournalRepository.saveEntry(createTestEntry("AJ0001", "P001"));

            // Act
            var found = autoJournalRepository.findEntryWithPatternByNumber("AJ0001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getPattern()).isNotNull();
            assertThat(found.get().getPattern().getPatternCode()).isEqualTo("P001");
            assertThat(found.get().getPattern().getPatternName()).isEqualTo("テストパターン");
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @BeforeEach
        void setUpPattern() {
            autoJournalRepository.savePattern(createTestPattern("P001"));
        }

        @Test
        @DisplayName("同じバージョンでエントリを更新できる")
        void canUpdateEntryWithSameVersion() {
            // Arrange
            autoJournalRepository.saveEntry(createTestEntry("AJ0001", "P001"));

            var entry = autoJournalRepository.findEntryByNumber("AJ0001").orElseThrow();
            entry.setStatus(AutoJournalStatus.COMPLETED);

            // Act
            autoJournalRepository.updateEntry(entry);

            // Assert
            var updated = autoJournalRepository.findEntryByNumber("AJ0001").orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(AutoJournalStatus.COMPLETED);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            // Arrange
            autoJournalRepository.saveEntry(createTestEntry("AJ0001", "P001"));

            var entryA = autoJournalRepository.findEntryByNumber("AJ0001").orElseThrow();
            var entryB = autoJournalRepository.findEntryByNumber("AJ0001").orElseThrow();

            // 処理Aが更新（成功）
            entryA.setStatus(AutoJournalStatus.COMPLETED);
            autoJournalRepository.updateEntry(entryA);

            // Act & Assert: 処理Bが古いバージョンで更新（失敗）
            entryB.setStatus(AutoJournalStatus.ERROR);
            assertThatThrownBy(() -> autoJournalRepository.updateEntry(entryB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除済みのエントリを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntryDeleted() {
            // Arrange
            autoJournalRepository.saveEntry(createTestEntry("AJ0001", "P001"));
            var entry = autoJournalRepository.findEntryByNumber("AJ0001").orElseThrow();

            // 削除
            autoJournalRepository.deleteEntry("AJ0001");

            // Act & Assert
            entry.setStatus(AutoJournalStatus.COMPLETED);
            assertThatThrownBy(() -> autoJournalRepository.updateEntry(entry))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("処理履歴")
    class HistoryOperations {

        @Test
        @DisplayName("処理履歴を登録して取得できる")
        void canSaveAndFindHistory() {
            // Arrange
            var history = AutoJournalHistory.builder()
                    .processNumber("H0001")
                    .processDateTime(LocalDateTime.of(2024, 4, 1, 10, 0))
                    .targetFromDate(LocalDate.of(2024, 4, 1))
                    .targetToDate(LocalDate.of(2024, 4, 30))
                    .totalCount(100)
                    .successCount(98)
                    .errorCount(2)
                    .totalAmount(new BigDecimal("1000000"))
                    .processedBy("テストユーザー")
                    .remarks("月次処理")
                    .build();

            // Act
            autoJournalRepository.saveHistory(history);
            var found = autoJournalRepository.findHistoryByNumber("H0001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getTotalCount()).isEqualTo(100);
            assertThat(found.get().getSuccessCount()).isEqualTo(98);
        }

        @Test
        @DisplayName("日付範囲で処理履歴を検索できる")
        void canFindHistoriesByDateRange() {
            // Arrange
            autoJournalRepository.saveHistory(AutoJournalHistory.builder()
                    .processNumber("H0001")
                    .processDateTime(LocalDateTime.of(2024, 4, 1, 10, 0))
                    .targetFromDate(LocalDate.of(2024, 4, 1))
                    .targetToDate(LocalDate.of(2024, 4, 30))
                    .totalCount(100)
                    .successCount(100)
                    .errorCount(0)
                    .totalAmount(BigDecimal.ZERO)
                    .build());

            autoJournalRepository.saveHistory(AutoJournalHistory.builder()
                    .processNumber("H0002")
                    .processDateTime(LocalDateTime.of(2024, 5, 1, 10, 0))
                    .targetFromDate(LocalDate.of(2024, 5, 1))
                    .targetToDate(LocalDate.of(2024, 5, 31))
                    .totalCount(50)
                    .successCount(50)
                    .errorCount(0)
                    .totalAmount(BigDecimal.ZERO)
                    .build());

            // Act
            var histories = autoJournalRepository.findHistoriesByDateRange(
                    LocalDate.of(2024, 4, 1),
                    LocalDate.of(2024, 4, 30));

            // Assert
            assertThat(histories).hasSize(1);
            assertThat(histories.get(0).getProcessNumber()).isEqualTo("H0001");
        }
    }
}
