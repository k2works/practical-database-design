package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.AccountStructureRepository;
import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AccountStructure;
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
 * 勘定科目構成リポジトリテスト.
 */
@DisplayName("勘定科目構成リポジトリ")
class AccountStructureRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AccountStructureRepository accountStructureRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaxTransactionRepository taxTransactionRepository;

    @BeforeEach
    void setUp() {
        accountStructureRepository.deleteAll();
        accountRepository.deleteAll();
        taxTransactionRepository.deleteAll();
    }

    private void setupMasterData() {
        TaxTransaction taxTransaction = TaxTransaction.builder()
                .taxCode("10")
                .taxName("課税売上10%")
                .taxRate(new BigDecimal("0.10"))
                .updatedBy("テストユーザー")
                .build();
        taxTransactionRepository.save(taxTransaction);

        // 親勘定科目
        accountRepository.save(Account.builder()
                .accountCode("1000")
                .accountName("流動資産")
                .accountShortName("流動資産")
                .accountNameKana("リュウドウシサン")
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.HEADER)
                .managementAccountingType("1")
                .expenseType("1")
                .ledgerOutputType("1")
                .subAccountType("1")
                .consumptionTaxType("対象外")
                .taxTransactionCode("10")
                .dueDateManagementType("1")
                .updatedBy("テストユーザー")
                .build());

        // 子勘定科目
        accountRepository.save(Account.builder()
                .accountCode("1100")
                .accountName("現金預金")
                .accountShortName("現金預金")
                .accountNameKana("ゲンキンヨキン")
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.SUMMARY)
                .managementAccountingType("1")
                .expenseType("1")
                .ledgerOutputType("1")
                .subAccountType("1")
                .consumptionTaxType("対象外")
                .taxTransactionCode("10")
                .dueDateManagementType("1")
                .updatedBy("テストユーザー")
                .build());

        // 孫勘定科目
        accountRepository.save(Account.builder()
                .accountCode("1110")
                .accountName("現金")
                .accountShortName("現金")
                .accountNameKana("ゲンキン")
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING)
                .managementAccountingType("1")
                .expenseType("1")
                .ledgerOutputType("1")
                .subAccountType("1")
                .consumptionTaxType("対象外")
                .taxTransactionCode("10")
                .dueDateManagementType("1")
                .updatedBy("テストユーザー")
                .build());
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("勘定科目構成を登録できる")
        void canRegisterAccountStructure() {
            // Arrange
            setupMasterData();
            AccountStructure structure = AccountStructure.builder()
                    .accountCode("1000")
                    .accountPath("1000")
                    .updatedBy("テストユーザー")
                    .build();

            // Act
            accountStructureRepository.save(structure);

            // Assert
            Optional<AccountStructure> found = accountStructureRepository.findByCode("1000");
            assertThat(found).isPresent();
            assertThat(found.get().getAccountPath()).isEqualTo("1000");
        }

        @Test
        @DisplayName("階層構造を登録できる")
        void canRegisterHierarchy() {
            // Arrange
            setupMasterData();

            // Act - チルダ連結方式で階層を表現
            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1000")
                    .accountPath("1000")
                    .updatedBy("テストユーザー")
                    .build());
            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1100")
                    .accountPath("1000~1100")
                    .updatedBy("テストユーザー")
                    .build());
            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1110")
                    .accountPath("1000~1100~1110")
                    .updatedBy("テストユーザー")
                    .build());

            // Assert
            Optional<AccountStructure> root = accountStructureRepository.findByCode("1000");
            Optional<AccountStructure> child = accountStructureRepository.findByCode("1100");
            Optional<AccountStructure> grandchild = accountStructureRepository.findByCode("1110");

            assertThat(root).isPresent();
            assertThat(root.get().getDepth()).isEqualTo(1);

            assertThat(child).isPresent();
            assertThat(child.get().getDepth()).isEqualTo(2);
            assertThat(child.get().getParentCode()).isEqualTo("1000");

            assertThat(grandchild).isPresent();
            assertThat(grandchild.get().getDepth()).isEqualTo(3);
            assertThat(grandchild.get().getParentCode()).isEqualTo("1100");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            setupMasterData();

            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1000")
                    .accountPath("1000")
                    .updatedBy("テストユーザー")
                    .build());
            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1100")
                    .accountPath("1000~1100")
                    .updatedBy("テストユーザー")
                    .build());
            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1110")
                    .accountPath("1000~1100~1110")
                    .updatedBy("テストユーザー")
                    .build());
        }

        @Test
        @DisplayName("コードで検索できる")
        void canFindByCode() {
            // Act
            Optional<AccountStructure> found = accountStructureRepository.findByCode("1100");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getAccountPath()).isEqualTo("1000~1100");
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<AccountStructure> structures = accountStructureRepository.findAll();

            // Assert
            assertThat(structures).hasSize(3);
        }

        @Test
        @DisplayName("パス部分一致で検索できる")
        void canFindByPathContaining() {
            // Act
            List<AccountStructure> found = accountStructureRepository.findByPathContaining("1100");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).extracting(AccountStructure::getAccountCode)
                    .containsExactlyInAnyOrder("1100", "1110");
        }

        @Test
        @DisplayName("子要素を検索できる")
        void canFindChildren() {
            // Act
            List<AccountStructure> children = accountStructureRepository.findChildren("1000");

            // Assert
            assertThat(children).hasSize(1);
            assertThat(children.get(0).getAccountCode()).isEqualTo("1100");
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("勘定科目構成を更新できる")
        void canUpdateAccountStructure() {
            // Arrange
            setupMasterData();
            accountStructureRepository.save(AccountStructure.builder()
                    .accountCode("1100")
                    .accountPath("1100")
                    .updatedBy("テストユーザー")
                    .build());

            // Act
            AccountStructure updated = AccountStructure.builder()
                    .accountCode("1100")
                    .accountPath("1000~1100")
                    .updatedBy("更新ユーザー")
                    .build();
            accountStructureRepository.update(updated);

            // Assert
            Optional<AccountStructure> found = accountStructureRepository.findByCode("1100");
            assertThat(found).isPresent();
            assertThat(found.get().getAccountPath()).isEqualTo("1000~1100");
            assertThat(found.get().getUpdatedBy()).isEqualTo("更新ユーザー");
        }
    }
}
