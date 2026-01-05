package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ProductHistoryRepository;
import com.example.sms.domain.model.common.ProductHistory;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商品マスタ履歴リポジトリテスト.
 */
@DisplayName("商品マスタ履歴リポジトリ")
class ProductHistoryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"商品マスタ履歴\"");
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("商品マスタ履歴を登録できる")
        void canRegisterProductHistory() {
            var history = ProductHistory.builder()
                    .productCode("PRD001")
                    .validFromDate(LocalDate.of(2025, 1, 1))
                    .validToDate(null)
                    .productName("テスト商品")
                    .productCategory(ProductCategory.PRODUCT)
                    .unitPrice(new BigDecimal("1000"))
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .createdBy("test-user")
                    .build();

            productHistoryRepository.save(history);

            var result = productHistoryRepository.findById(history.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getProductCode()).isEqualTo("PRD001");
            assertThat(result.get().getProductName()).isEqualTo("テスト商品");
        }

        @Test
        @DisplayName("有効終了日付きで登録できる")
        void canRegisterWithValidToDate() {
            var history = ProductHistory.builder()
                    .productCode("PRD002")
                    .validFromDate(LocalDate.of(2025, 1, 1))
                    .validToDate(LocalDate.of(2025, 6, 30))
                    .productName("期間限定商品")
                    .productCategory(ProductCategory.PRODUCT)
                    .unitPrice(new BigDecimal("500"))
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .createdBy("test-user")
                    .build();

            productHistoryRepository.save(history);

            var result = productHistoryRepository.findById(history.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getValidToDate()).isEqualTo(LocalDate.of(2025, 6, 30));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("商品コードで検索できる")
        void canFindByProductCode() {
            var hist1 = createHistory("PRD001", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            var hist2 = createHistory("PRD001", LocalDate.of(2025, 1, 1), null);
            var hist3 = createHistory("PRD002", LocalDate.of(2025, 1, 1), null);
            productHistoryRepository.save(hist1);
            productHistoryRepository.save(hist2);
            productHistoryRepository.save(hist3);

            var result = productHistoryRepository.findByProductCode("PRD001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("特定日時点で有効な履歴を検索できる")
        void canFindByProductCodeAndValidDate() {
            var hist1 = createHistory("PRD001", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30));
            hist1.setProductName("旧商品名");
            hist1.setUnitPrice(new BigDecimal("900"));
            var hist2 = createHistory("PRD001", LocalDate.of(2024, 7, 1), LocalDate.of(2024, 12, 31));
            hist2.setProductName("中間商品名");
            hist2.setUnitPrice(new BigDecimal("1000"));
            var hist3 = createHistory("PRD001", LocalDate.of(2025, 1, 1), null);
            hist3.setProductName("新商品名");
            hist3.setUnitPrice(new BigDecimal("1100"));
            productHistoryRepository.save(hist1);
            productHistoryRepository.save(hist2);
            productHistoryRepository.save(hist3);

            // 2024年4月時点
            var result1 = productHistoryRepository.findByProductCodeAndValidDate("PRD001", LocalDate.of(2024, 4, 1));
            assertThat(result1).isPresent();
            assertThat(result1.get().getProductName()).isEqualTo("旧商品名");
            assertThat(result1.get().getUnitPrice()).isEqualByComparingTo(new BigDecimal("900"));

            // 2024年10月時点
            var result2 = productHistoryRepository.findByProductCodeAndValidDate("PRD001", LocalDate.of(2024, 10, 1));
            assertThat(result2).isPresent();
            assertThat(result2.get().getProductName()).isEqualTo("中間商品名");

            // 2025年2月時点
            var result3 = productHistoryRepository.findByProductCodeAndValidDate("PRD001", LocalDate.of(2025, 2, 1));
            assertThat(result3).isPresent();
            assertThat(result3.get().getProductName()).isEqualTo("新商品名");
        }

        @Test
        @DisplayName("有効期間外の日付では取得できない")
        void returnsEmptyWhenOutsideValidPeriod() {
            var hist = createHistory("PRD001", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 30));
            productHistoryRepository.save(hist);

            // 有効期間開始前
            var result1 = productHistoryRepository.findByProductCodeAndValidDate("PRD001", LocalDate.of(2024, 12, 31));
            assertThat(result1).isEmpty();

            // 有効期間終了後
            var result2 = productHistoryRepository.findByProductCodeAndValidDate("PRD001", LocalDate.of(2025, 7, 1));
            assertThat(result2).isEmpty();
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("有効終了日を更新できる")
        void canUpdateValidToDate() {
            var history = createHistory("PRD001", LocalDate.of(2025, 1, 1), null);
            productHistoryRepository.save(history);

            var fetched = productHistoryRepository.findById(history.getId()).get();
            fetched.setValidToDate(LocalDate.of(2025, 12, 31));
            productHistoryRepository.update(fetched);

            var updated = productHistoryRepository.findById(history.getId()).get();
            assertThat(updated.getValidToDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        }
    }

    @Nested
    @DisplayName("全件取得")
    class FindAll {

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            var hist1 = createHistory("PRD001", LocalDate.of(2025, 1, 1), null);
            var hist2 = createHistory("PRD002", LocalDate.of(2025, 1, 1), null);
            productHistoryRepository.save(hist1);
            productHistoryRepository.save(hist2);

            var result = productHistoryRepository.findAll();
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("ドメインロジック")
    class DomainLogic {

        @Test
        @DisplayName("isValidOnで有効期間を判定できる")
        void canCheckValidPeriod() {
            var history = ProductHistory.builder()
                    .productCode("PRD001")
                    .validFromDate(LocalDate.of(2025, 1, 1))
                    .validToDate(LocalDate.of(2025, 6, 30))
                    .productName("テスト商品")
                    .productCategory(ProductCategory.PRODUCT)
                    .unitPrice(new BigDecimal("1000"))
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .build();

            // 有効期間内
            assertThat(history.isValidOn(LocalDate.of(2025, 3, 15))).isTrue();
            assertThat(history.isValidOn(LocalDate.of(2025, 1, 1))).isTrue();
            assertThat(history.isValidOn(LocalDate.of(2025, 6, 30))).isTrue();

            // 有効期間外
            assertThat(history.isValidOn(LocalDate.of(2024, 12, 31))).isFalse();
            assertThat(history.isValidOn(LocalDate.of(2025, 7, 1))).isFalse();
        }

        @Test
        @DisplayName("有効終了日なしは無期限有効")
        void noEndDateMeansIndefinite() {
            var history = ProductHistory.builder()
                    .productCode("PRD001")
                    .validFromDate(LocalDate.of(2025, 1, 1))
                    .validToDate(null)
                    .productName("テスト商品")
                    .productCategory(ProductCategory.PRODUCT)
                    .unitPrice(new BigDecimal("1000"))
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .build();

            assertThat(history.isValidOn(LocalDate.of(2025, 12, 31))).isTrue();
            assertThat(history.isValidOn(LocalDate.of(2030, 1, 1))).isTrue();
        }
    }

    private ProductHistory createHistory(String productCode, LocalDate validFrom, LocalDate validTo) {
        return ProductHistory.builder()
                .productCode(productCode)
                .validFromDate(validFrom)
                .validToDate(validTo)
                .productName("テスト商品")
                .productCategory(ProductCategory.PRODUCT)
                .unitPrice(new BigDecimal("1000"))
                .taxCategory(TaxCategory.EXCLUSIVE)
                .createdBy("test-user")
                .build();
    }
}
