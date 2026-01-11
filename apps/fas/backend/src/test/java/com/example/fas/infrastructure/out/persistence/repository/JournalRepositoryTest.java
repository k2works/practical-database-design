package com.example.fas.infrastructure.out.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import com.example.fas.domain.model.journal.JournalVoucherType;
import com.example.fas.domain.model.tax.TaxTransaction;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 仕訳リポジトリテスト.
 */
@DisplayName("仕訳リポジトリ")
class JournalRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaxTransactionRepository taxTransactionRepository;

    @BeforeEach
    void setUp() {
        journalRepository.deleteAll();
        accountRepository.deleteAll();
        taxTransactionRepository.deleteAll();
        setUpMasterData();
    }

    private void setUpMasterData() {
        TaxTransaction taxTransaction = TaxTransaction.builder()
                .taxCode("10")
                .taxName("課税売上10%")
                .taxRate(new BigDecimal("0.10"))
                .updatedBy("テストユーザー")
                .build();
        taxTransactionRepository.save(taxTransaction);

        accountRepository.save(createAccount("11110", "現金", BSPLType.BS,
                DebitCreditType.DEBIT, TransactionElementType.ASSET));
        accountRepository.save(createAccount("11200", "受取手形", BSPLType.BS,
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

    private Journal createSimpleSalesJournal(String voucherNumber) {
        var debitDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11110")
                .amount(new BigDecimal("100000"))
                .currencyCode("JPY")
                .exchangeRate(BigDecimal.ONE)
                .build();

        var creditDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.CREDIT)
                .accountCode("41100")
                .amount(new BigDecimal("100000"))
                .currencyCode("JPY")
                .exchangeRate(BigDecimal.ONE)
                .build();

        var journalDetail = JournalDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .lineSummary("現金売上")
                .debitCreditDetails(List.of(debitDetail, creditDetail))
                .build();

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(LocalDate.of(2024, 4, 1))
                .entryDate(LocalDate.now())
                .closingJournalFlag(false)
                .singleEntryFlag(true)
                .voucherType(JournalVoucherType.NORMAL)
                .periodicPostingFlag(false)
                .redSlipFlag(false)
                .details(List.of(journalDetail))
                .build();
    }

    private Journal createCompoundJournal(String voucherNumber) {
        var debit1 = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11110")
                .amount(new BigDecimal("80000"))
                .currencyCode("JPY")
                .exchangeRate(BigDecimal.ONE)
                .build();

        var credit1 = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.CREDIT)
                .accountCode("11300")
                .amount(new BigDecimal("100000"))
                .currencyCode("JPY")
                .exchangeRate(BigDecimal.ONE)
                .build();

        var debit2 = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(2)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11200")
                .amount(new BigDecimal("20000"))
                .currencyCode("JPY")
                .exchangeRate(BigDecimal.ONE)
                .build();

        var detail1 = JournalDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .lineSummary("現金入金")
                .debitCreditDetails(List.of(debit1, credit1))
                .build();

        var detail2 = JournalDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(2)
                .lineSummary("手形入金")
                .debitCreditDetails(List.of(debit2))
                .build();

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(LocalDate.of(2024, 4, 5))
                .entryDate(LocalDate.now())
                .closingJournalFlag(false)
                .singleEntryFlag(false)
                .voucherType(JournalVoucherType.NORMAL)
                .periodicPostingFlag(false)
                .redSlipFlag(false)
                .details(List.of(detail1, detail2))
                .build();
    }

    private Journal createUnbalancedJournal(String voucherNumber) {
        var debitDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11110")
                .amount(new BigDecimal("100000"))
                .build();

        var creditDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.CREDIT)
                .accountCode("41100")
                .amount(new BigDecimal("90000"))
                .build();

        var journalDetail = JournalDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditDetails(List.of(debitDetail, creditDetail))
                .build();

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(LocalDate.of(2024, 4, 1))
                .entryDate(LocalDate.now())
                .details(List.of(journalDetail))
                .build();
    }

    @Nested
    @DisplayName("仕訳の登録")
    class Registration {

        @Test
        @DisplayName("単純仕訳（1行借方・1行貸方）を登録できる")
        void canRegisterSimpleJournal() {
            // Arrange
            var journal = createSimpleSalesJournal("J0001");

            // Act
            journalRepository.save(journal);

            // Assert
            var savedJournal = journalRepository.findByVoucherNumber("J0001");
            assertThat(savedJournal).isPresent();
            assertThat(savedJournal.get().getPostingDate()).isEqualTo(LocalDate.of(2024, 4, 1));
            assertThat(savedJournal.get().isBalanced()).isTrue();
        }

        @Test
        @DisplayName("複合仕訳（複数行）を登録できる")
        void canRegisterCompoundJournal() {
            // Arrange
            var journal = createCompoundJournal("J0002");

            // Act
            journalRepository.save(journal);

            // Assert
            var savedJournal = journalRepository.findByVoucherNumber("J0002");
            assertThat(savedJournal).isPresent();
            assertThat(savedJournal.get().getDetails()).hasSize(2);
            assertThat(savedJournal.get().isBalanced()).isTrue();
        }

        @Test
        @DisplayName("貸借が一致しない仕訳はバランスチェックでfalseを返す")
        void shouldReturnFalseForUnbalancedJournal() {
            // Arrange
            var journal = createUnbalancedJournal("J0003");

            // Assert
            assertThat(journal.isBalanced()).isFalse();
        }
    }

    @Nested
    @DisplayName("仕訳の検索")
    class Search {

        @BeforeEach
        void setUpTestData() {
            journalRepository.save(createSimpleSalesJournal("J0001"));
            journalRepository.save(createCompoundJournal("J0002"));
        }

        @Test
        @DisplayName("起票日範囲で仕訳を検索できる")
        void canFindByDateRange() {
            // Act
            var journals = journalRepository.findByPostingDateBetween(
                    LocalDate.of(2024, 4, 1),
                    LocalDate.of(2024, 4, 30));

            // Assert
            assertThat(journals).hasSize(2);
        }

        @Test
        @DisplayName("勘定科目コードで仕訳を検索できる")
        void canFindByAccountCode() {
            // Act
            var journals = journalRepository.findByAccountCode("11110");

            // Assert
            assertThat(journals).isNotEmpty();
        }

        @Test
        @DisplayName("伝票番号で仕訳を検索できる")
        void canFindByVoucherNumber() {
            // Act
            var journal = journalRepository.findByVoucherNumber("J0001");

            // Assert
            assertThat(journal).isPresent();
            assertThat(journal.get().getVoucherType()).isEqualTo(JournalVoucherType.NORMAL);
        }

        @Test
        @DisplayName("存在しない伝票番号で検索すると空を返す")
        void returnsEmptyForNonExistentVoucherNumber() {
            // Act
            var journal = journalRepository.findByVoucherNumber("J9999");

            // Assert
            assertThat(journal).isEmpty();
        }
    }

    @Nested
    @DisplayName("仕訳の削除")
    class Delete {

        @Test
        @DisplayName("仕訳を削除できる")
        void canDeleteJournal() {
            // Arrange
            journalRepository.save(createSimpleSalesJournal("J0001"));

            // Act
            journalRepository.delete("J0001");

            // Assert
            var journal = journalRepository.findByVoucherNumber("J0001");
            assertThat(journal).isEmpty();
        }
    }

    @Nested
    @DisplayName("ドメインロジック")
    class DomainLogic {

        @Test
        @DisplayName("借方合計を取得できる")
        void canGetDebitTotal() {
            // Arrange
            var journal = createCompoundJournal("J0001");

            // Act
            var debitTotal = journal.getDebitTotal();

            // Assert
            assertThat(debitTotal).isEqualByComparingTo(new BigDecimal("100000"));
        }

        @Test
        @DisplayName("貸方合計を取得できる")
        void canGetCreditTotal() {
            // Arrange
            var journal = createCompoundJournal("J0001");

            // Act
            var creditTotal = journal.getCreditTotal();

            // Assert
            assertThat(creditTotal).isEqualByComparingTo(new BigDecimal("100000"));
        }

        @Test
        @DisplayName("貸借一致チェックが正しく動作する")
        void isBalancedWorksCorrectly() {
            // Arrange
            var balancedJournal = createSimpleSalesJournal("J0001");
            var unbalancedJournal = createUnbalancedJournal("J0002");

            // Assert
            assertThat(balancedJournal.isBalanced()).isTrue();
            assertThat(unbalancedJournal.isBalanced()).isFalse();
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("JOINで仕訳と明細を一括取得できる")
        void canFindWithDetails() {
            // Arrange
            var journal = createCompoundJournal("J0001");
            journalRepository.save(journal);

            // Act
            var result = journalRepository.findWithDetails("J0001");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getJournalVoucherNumber()).isEqualTo("J0001");
            assertThat(result.get().getDetails()).hasSize(2);
            assertThat(result.get().getDetails().get(0).getDebitCreditDetails()).isNotEmpty();
        }

        @Test
        @DisplayName("仕訳更新時にバージョンが更新される")
        void versionIncreasesOnUpdate() {
            // Arrange
            var journal = createSimpleSalesJournal("J0001");
            journalRepository.save(journal);

            var savedJournal = journalRepository.findWithDetails("J0001").orElseThrow();
            assertThat(savedJournal.getVersion()).isEqualTo(1);

            // 更新用データを作成
            savedJournal.setPostingDate(LocalDate.of(2024, 5, 1));
            savedJournal.getDetails().get(0).setLineSummary("更新後の摘要");

            // Act
            journalRepository.update(savedJournal);

            // Assert
            var updatedJournal = journalRepository.findWithDetails("J0001").orElseThrow();
            assertThat(updatedJournal.getVersion()).isEqualTo(2);
            assertThat(updatedJournal.getPostingDate()).isEqualTo(LocalDate.of(2024, 5, 1));
        }

        @Test
        @DisplayName("古いバージョンで更新するとOptimisticLockExceptionが発生する")
        void throwsExceptionOnVersionMismatch() {
            // Arrange
            var journal = createSimpleSalesJournal("J0001");
            journalRepository.save(journal);

            // 2つの操作が同じデータを取得
            var journal1 = journalRepository.findWithDetails("J0001").orElseThrow();
            var journal2 = journalRepository.findWithDetails("J0001").orElseThrow();

            // 1つ目の更新は成功
            journal1.setPostingDate(LocalDate.of(2024, 5, 1));
            journalRepository.update(journal1);

            // 2つ目の更新は失敗（古いバージョン）
            journal2.setPostingDate(LocalDate.of(2024, 6, 1));

            // Act & Assert
            assertThatThrownBy(() -> journalRepository.update(journal2))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除済みの仕訳を更新するとOptimisticLockExceptionが発生する")
        void throwsExceptionOnDeletedJournal() {
            // Arrange
            var journal = createSimpleSalesJournal("J0001");
            journalRepository.save(journal);

            var savedJournal = journalRepository.findWithDetails("J0001").orElseThrow();

            // 別の操作で削除
            journalRepository.delete("J0001");

            // Act & Assert
            assertThatThrownBy(() -> journalRepository.update(savedJournal))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }

        @Test
        @DisplayName("findWithDetailsで存在しない仕訳を検索すると空を返す")
        void returnsEmptyForNonExistentJournal() {
            // Act
            var result = journalRepository.findWithDetails("J9999");

            // Assert
            assertThat(result).isEmpty();
        }
    }
}
