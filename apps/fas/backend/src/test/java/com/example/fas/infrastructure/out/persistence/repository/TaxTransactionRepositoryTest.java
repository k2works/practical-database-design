package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.TaxTransactionRepository;
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
 * 課税取引リポジトリテスト.
 */
@DisplayName("課税取引リポジトリ")
class TaxTransactionRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TaxTransactionRepository taxTransactionRepository;

    @BeforeEach
    void setUp() {
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

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("課税取引を登録できる")
        void canRegisterTaxTransaction() {
            // Arrange
            TaxTransaction taxTransaction = createTaxTransaction("01", "課税売上10%", new BigDecimal("0.10"));

            // Act
            taxTransactionRepository.save(taxTransaction);

            // Assert
            Optional<TaxTransaction> found = taxTransactionRepository.findByCode("01");
            assertThat(found).isPresent();
            assertThat(found.get().getTaxName()).isEqualTo("課税売上10%");
            assertThat(found.get().getTaxRate()).isEqualByComparingTo(new BigDecimal("0.10"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            taxTransactionRepository.save(createTaxTransaction("01", "課税売上10%", new BigDecimal("0.10")));
            taxTransactionRepository.save(createTaxTransaction("02", "課税売上8%", new BigDecimal("0.08")));
            taxTransactionRepository.save(createTaxTransaction("03", "非課税売上", new BigDecimal("0.00")));
        }

        @Test
        @DisplayName("コードで検索できる")
        void canFindByCode() {
            // Act
            Optional<TaxTransaction> found = taxTransactionRepository.findByCode("02");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getTaxName()).isEqualTo("課税売上8%");
        }

        @Test
        @DisplayName("存在しないコードで検索すると空を返す")
        void returnsEmptyForNonExistentCode() {
            // Act
            Optional<TaxTransaction> found = taxTransactionRepository.findByCode("99");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<TaxTransaction> transactions = taxTransactionRepository.findAll();

            // Assert
            assertThat(transactions).hasSize(3);
            assertThat(transactions).extracting(TaxTransaction::getTaxCode)
                    .containsExactly("01", "02", "03");
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("課税取引を更新できる")
        void canUpdateTaxTransaction() {
            // Arrange
            taxTransactionRepository.save(createTaxTransaction("01", "課税売上10%", new BigDecimal("0.10")));

            // Act
            TaxTransaction updated = TaxTransaction.builder()
                    .taxCode("01")
                    .taxName("標準税率課税売上")
                    .taxRate(new BigDecimal("0.10"))
                    .updatedBy("更新ユーザー")
                    .build();
            taxTransactionRepository.update(updated);

            // Assert
            Optional<TaxTransaction> found = taxTransactionRepository.findByCode("01");
            assertThat(found).isPresent();
            assertThat(found.get().getTaxName()).isEqualTo("標準税率課税売上");
            assertThat(found.get().getUpdatedBy()).isEqualTo("更新ユーザー");
        }
    }

    @Nested
    @DisplayName("税額計算")
    class TaxCalculation {

        @Test
        @DisplayName("税込金額を計算できる")
        void canCalculateTaxIncludedAmount() {
            // Arrange
            TaxTransaction taxTransaction = createTaxTransaction("01", "課税売上10%", new BigDecimal("0.10"));

            // Act
            BigDecimal taxIncluded = taxTransaction.calculateTaxIncludedAmount(new BigDecimal("1000"));

            // Assert
            assertThat(taxIncluded).isEqualByComparingTo(new BigDecimal("1100"));
        }

        @Test
        @DisplayName("税額を計算できる")
        void canCalculateTaxAmount() {
            // Arrange
            TaxTransaction taxTransaction = createTaxTransaction("01", "課税売上10%", new BigDecimal("0.10"));

            // Act
            BigDecimal taxAmount = taxTransaction.calculateTaxAmount(new BigDecimal("1000"));

            // Assert
            assertThat(taxAmount).isEqualByComparingTo(new BigDecimal("100"));
        }

        @Test
        @DisplayName("非課税の場合は税額が0になる")
        void zeroTaxForNonTaxable() {
            // Arrange
            TaxTransaction taxTransaction = createTaxTransaction("03", "非課税売上", BigDecimal.ZERO);

            // Act
            BigDecimal taxIncluded = taxTransaction.calculateTaxIncludedAmount(new BigDecimal("1000"));
            BigDecimal taxAmount = taxTransaction.calculateTaxAmount(new BigDecimal("1000"));

            // Assert
            assertThat(taxIncluded).isEqualByComparingTo(new BigDecimal("1000"));
            assertThat(taxAmount).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
