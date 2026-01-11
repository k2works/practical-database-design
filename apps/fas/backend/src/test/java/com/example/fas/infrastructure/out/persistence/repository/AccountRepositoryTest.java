package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.tax.TaxTransaction;
import com.example.fas.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 勘定科目リポジトリテスト.
 */
@DisplayName("勘定科目リポジトリ")
class AccountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaxTransactionRepository taxTransactionRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        taxTransactionRepository.deleteAll();
    }

    private TaxTransaction createTaxTransaction(String code, String name, BigDecimal rate) {
        return TaxTransaction.builder()
                .taxCode(code)
                .taxName(name)
                .taxRate(rate)
                .updatedBy("テストユーザー")
                .build();
    }

    private Account createAccount(String code, String name, BSPLType bsplType,
                                  DebitCreditType debitCreditType,
                                  TransactionElementType transactionElementType,
                                  AggregationType aggregationType) {
        return Account.builder()
                .accountCode(code)
                .accountName(name)
                .accountShortName(name)
                .accountNameKana("カナ")
                .bsplType(bsplType)
                .debitCreditType(debitCreditType)
                .transactionElementType(transactionElementType)
                .aggregationType(aggregationType)
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

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("勘定科目を登録できる")
        void canRegisterAccount() {
            // Arrange
            TaxTransaction taxTransaction = createTaxTransaction("10", "課税売上10%", new BigDecimal("0.10"));
            taxTransactionRepository.save(taxTransaction);

            Account account = createAccount(
                    "1000",
                    "現金",
                    BSPLType.BS,
                    DebitCreditType.DEBIT,
                    TransactionElementType.ASSET,
                    AggregationType.POSTING
            );

            // Act
            accountRepository.save(account);

            // Assert
            Optional<Account> found = accountRepository.findByCode("1000");
            assertThat(found).isPresent();
            assertThat(found.get().getAccountName()).isEqualTo("現金");
            assertThat(found.get().getBsplType()).isEqualTo(BSPLType.BS);
            assertThat(found.get().getDebitCreditType()).isEqualTo(DebitCreditType.DEBIT);
            assertThat(found.get().getTransactionElementType()).isEqualTo(TransactionElementType.ASSET);
            assertThat(found.get().getAggregationType()).isEqualTo(AggregationType.POSTING);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            TaxTransaction taxTransaction = createTaxTransaction("10", "課税売上10%", new BigDecimal("0.10"));
            taxTransactionRepository.save(taxTransaction);

            accountRepository.save(createAccount("1000", "流動資産", BSPLType.BS, DebitCreditType.DEBIT,
                    TransactionElementType.ASSET, AggregationType.HEADER));
            accountRepository.save(createAccount("1100", "現金預金", BSPLType.BS, DebitCreditType.DEBIT,
                    TransactionElementType.ASSET, AggregationType.SUMMARY));
            accountRepository.save(createAccount("1110", "現金", BSPLType.BS, DebitCreditType.DEBIT,
                    TransactionElementType.ASSET, AggregationType.POSTING));
            accountRepository.save(createAccount("4000", "売上高", BSPLType.PL, DebitCreditType.CREDIT,
                    TransactionElementType.REVENUE, AggregationType.POSTING));
        }

        @Test
        @DisplayName("コードで勘定科目を検索できる")
        void canFindByCode() {
            // Act
            Optional<Account> found = accountRepository.findByCode("1110");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getAccountName()).isEqualTo("現金");
        }

        @Test
        @DisplayName("存在しないコードで検索すると空を返す")
        void returnsEmptyForNonExistentCode() {
            // Act
            Optional<Account> found = accountRepository.findByCode("9999");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Account> accounts = accountRepository.findAll();

            // Assert
            assertThat(accounts).hasSize(4);
        }

        @Test
        @DisplayName("BSPL区分で検索できる")
        void canFindByBSPLType() {
            // Act
            List<Account> bsAccounts = accountRepository.findByBSPLType(BSPLType.BS);
            List<Account> plAccounts = accountRepository.findByBSPLType(BSPLType.PL);

            // Assert
            assertThat(bsAccounts).hasSize(3);
            assertThat(plAccounts).hasSize(1);
        }

        @Test
        @DisplayName("取引要素区分で検索できる")
        void canFindByTransactionElementType() {
            // Act
            List<Account> assetAccounts = accountRepository.findByTransactionElementType(TransactionElementType.ASSET);
            List<Account> revenueAccounts = accountRepository.findByTransactionElementType(TransactionElementType.REVENUE);

            // Assert
            assertThat(assetAccounts).hasSize(3);
            assertThat(revenueAccounts).hasSize(1);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("勘定科目を更新できる")
        void canUpdateAccount() {
            // Arrange
            TaxTransaction taxTransaction = createTaxTransaction("10", "課税売上10%", new BigDecimal("0.10"));
            taxTransactionRepository.save(taxTransaction);

            Account account = createAccount("1000", "現金", BSPLType.BS, DebitCreditType.DEBIT,
                    TransactionElementType.ASSET, AggregationType.POSTING);
            accountRepository.save(account);

            // Act
            Account updated = Account.builder()
                    .accountCode("1000")
                    .accountName("現金及び預金")
                    .accountShortName("現預金")
                    .accountNameKana("ゲンキンオヨビヨキン")
                    .bsplType(BSPLType.BS)
                    .debitCreditType(DebitCreditType.DEBIT)
                    .transactionElementType(TransactionElementType.ASSET)
                    .aggregationType(AggregationType.SUMMARY)
                    .managementAccountingType("2")
                    .expenseType("2")
                    .ledgerOutputType("2")
                    .subAccountType("2")
                    .consumptionTaxType("非課税")
                    .taxTransactionCode("10")
                    .dueDateManagementType("2")
                    .updatedBy("更新ユーザー")
                    .build();
            accountRepository.update(updated);

            // Assert
            Optional<Account> found = accountRepository.findByCode("1000");
            assertThat(found).isPresent();
            assertThat(found.get().getAccountName()).isEqualTo("現金及び預金");
            assertThat(found.get().getAccountShortName()).isEqualTo("現預金");
            assertThat(found.get().getAggregationType()).isEqualTo(AggregationType.SUMMARY);
            assertThat(found.get().getUpdatedBy()).isEqualTo("更新ユーザー");
        }
    }
}
