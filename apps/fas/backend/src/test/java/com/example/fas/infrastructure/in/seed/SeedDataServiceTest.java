package com.example.fas.infrastructure.in.seed;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.DepartmentRepository;
import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Seed データ投入サービステスト.
 */
@DisplayName("Seedデータ投入サービス")
class SeedDataServiceTest extends BaseIntegrationTest {

    @Autowired
    private SeedDataService seedDataService;

    @Autowired
    private MasterDataSeeder masterDataSeeder;

    @Autowired
    private TaxTransactionRepository taxTransactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Nested
    @DisplayName("課税取引マスタ")
    class TaxTransactionSeedTests {

        @Test
        @DisplayName("課税取引マスタのSeedデータを投入できる")
        void canSeedTaxTransactions() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var tax10 = taxTransactionRepository.findByCode("10");
            assertThat(tax10).isPresent();
            assertThat(tax10.get().getTaxRate()).isEqualByComparingTo(new BigDecimal("0.100"));
        }

        @Test
        @DisplayName("免税データを投入できる")
        void canSeedTaxExemptData() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var tax00 = taxTransactionRepository.findByCode("00");
            assertThat(tax00).isPresent();
            assertThat(tax00.get().getTaxRate()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("勘定科目マスタ")
    class AccountSeedTests {

        @Test
        @DisplayName("現金勘定を投入できる")
        void canSeedCashAccount() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var cash = accountRepository.findByCode("11110");
            assertThat(cash).isPresent();
            assertThat(cash.get().getAccountName()).isEqualTo("現金");
        }

        @Test
        @DisplayName("売掛金勘定を投入できる")
        void canSeedAccountsReceivable() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var ar = accountRepository.findByCode("11210");
            assertThat(ar).isPresent();
            assertThat(ar.get().getAccountName()).isEqualTo("売掛金");
        }
    }

    @Nested
    @DisplayName("部門マスタ")
    class DepartmentSeedTests {

        @Test
        @DisplayName("全社部門を投入できる")
        void canSeedCompanyDepartment() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var company = departmentRepository.findByCode("10000");
            assertThat(company).isPresent();
            assertThat(company.get().getDepartmentName()).isEqualTo("全社");
            assertThat(company.get().getOrganizationLevel()).isZero();
        }

        @Test
        @DisplayName("営業本部を投入できる")
        void canSeedSalesDepartment() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var sales = departmentRepository.findByCode("11000");
            assertThat(sales).isPresent();
            assertThat(sales.get().getDepartmentName()).isEqualTo("営業本部");
            assertThat(sales.get().getOrganizationLevel()).isEqualTo(1);
        }

        @Test
        @DisplayName("最下層部門を投入できる")
        void canSeedLowestLevelDepartments() {
            // Seedを実行
            masterDataSeeder.seedAll();

            // Assert
            var lowestLevel = departmentRepository.findLowestLevel();
            assertThat(lowestLevel).isNotEmpty();
            assertThat(lowestLevel).allMatch(d -> d.getLowestLevelFlag() == 1);
        }
    }

    @Nested
    @DisplayName("冪等性")
    class IdempotencyTests {

        @Test
        @DisplayName("masterDataSeederを複数回実行してもエラーにならない")
        void seedIsIdempotent() {
            // Act - 複数回実行
            masterDataSeeder.seedAll();
            masterDataSeeder.seedAll();

            // Assert - エラーなく完了
            var tax10 = taxTransactionRepository.findByCode("10");
            assertThat(tax10).isPresent();
        }

        @Test
        @DisplayName("seedAllを複数回実行してもエラーにならない")
        void seedAllIsIdempotent() {
            // Arrange - 既存データをクリア
            seedDataService.cleanAllData();

            // Act - 複数回実行
            seedDataService.seedAll();
            seedDataService.seedAll();

            // Assert - エラーなく完了し、データは存在する
            assertThat(taxTransactionRepository.findByCode("10")).isPresent();
            assertThat(departmentRepository.findByCode("10000")).isPresent();
        }

        @Test
        @DisplayName("seedMasterDataOnlyを実行できる")
        void canSeedMasterDataOnly() {
            // Act
            seedDataService.seedMasterDataOnly();

            // Assert
            assertThat(taxTransactionRepository.findByCode("10")).isPresent();
            assertThat(accountRepository.findByCode("11110")).isPresent();
            assertThat(departmentRepository.findByCode("10000")).isPresent();
        }
    }
}
