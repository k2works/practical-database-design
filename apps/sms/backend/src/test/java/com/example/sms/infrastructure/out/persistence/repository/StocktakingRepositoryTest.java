package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.StocktakingRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingDetail;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import com.example.sms.domain.model.product.Product;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 棚卸リポジトリテスト.
 */
@DisplayName("棚卸リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class StocktakingRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private StocktakingRepository stocktakingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stocktakingRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM \"倉庫マスタ\"");
        productRepository.deleteAll();

        // 倉庫マスタに登録
        jdbcTemplate.update(
                "INSERT INTO \"倉庫マスタ\" (\"倉庫コード\", \"倉庫名\", \"倉庫区分\") VALUES (?, ?, ?::倉庫区分)",
                "WH001", "メイン倉庫", "自社");

        // 商品を登録
        var product = Product.builder()
                .productCode("P001")
                .productName("テスト商品")
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(TaxCategory.EXCLUSIVE)
                .build();
        productRepository.save(product);
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("棚卸を登録できる")
        void canRegisterStocktaking() {
            var stocktaking = Stocktaking.builder()
                    .stocktakingNumber("ST-202501-0001")
                    .warehouseCode("WH001")
                    .stocktakingDate(LocalDate.of(2025, 1, 20))
                    .status(StocktakingStatus.DRAFT)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            stocktakingRepository.save(stocktaking);

            var result = stocktakingRepository.findByStocktakingNumber("ST-202501-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getStocktakingNumber()).isEqualTo("ST-202501-0001");
            assertThat(result.get().getStatus()).isEqualTo(StocktakingStatus.DRAFT);
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("棚卸明細付きで登録できる")
        void canRegisterStocktakingWithDetails() {
            var stocktaking = Stocktaking.builder()
                    .stocktakingNumber("ST-202501-0002")
                    .warehouseCode("WH001")
                    .stocktakingDate(LocalDate.of(2025, 1, 20))
                    .status(StocktakingStatus.DRAFT)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            StocktakingDetail.builder()
                                    .productCode("P001")
                                    .bookQuantity(new BigDecimal("100"))
                                    .adjustedFlag(false)
                                    .build()
                    ))
                    .build();

            stocktakingRepository.save(stocktaking);

            var result = stocktakingRepository.findWithDetailsByStocktakingNumber("ST-202501-0002");
            assertThat(result).isPresent();
            assertThat(result.get().getDetails()).hasSize(1);
            assertThat(result.get().getDetails().get(0).getProductCode()).isEqualTo("P001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("倉庫コードで検索できる")
        void canFindByWarehouseCode() {
            var st1 = createStocktaking("ST-202501-0001", LocalDate.of(2025, 1, 20));
            var st2 = createStocktaking("ST-202501-0002", LocalDate.of(2025, 1, 25));
            stocktakingRepository.save(st1);
            stocktakingRepository.save(st2);

            var result = stocktakingRepository.findByWarehouseCode("WH001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            var st1 = createStocktaking("ST-202501-0001", LocalDate.of(2025, 1, 20));
            st1.setStatus(StocktakingStatus.IN_PROGRESS);
            var st2 = createStocktaking("ST-202501-0002", LocalDate.of(2025, 1, 25));
            st2.setStatus(StocktakingStatus.DRAFT);
            stocktakingRepository.save(st1);
            stocktakingRepository.save(st2);

            var result = stocktakingRepository.findByStatus(StocktakingStatus.IN_PROGRESS);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStocktakingNumber()).isEqualTo("ST-202501-0001");
        }

        @Test
        @DisplayName("棚卸日範囲で検索できる")
        void canFindByStocktakingDateBetween() {
            var st1 = createStocktaking("ST-202501-0001", LocalDate.of(2025, 1, 10));
            var st2 = createStocktaking("ST-202501-0002", LocalDate.of(2025, 1, 20));
            var st3 = createStocktaking("ST-202502-0001", LocalDate.of(2025, 2, 1));
            stocktakingRepository.save(st1);
            stocktakingRepository.save(st2);
            stocktakingRepository.save(st3);

            var result = stocktakingRepository.findByStocktakingDateBetween(
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var stocktaking = createStocktaking("ST-202501-0001", LocalDate.of(2025, 1, 20));
            stocktakingRepository.save(stocktaking);

            var fetched = stocktakingRepository.findByStocktakingNumber("ST-202501-0001").get();
            fetched.setStatus(StocktakingStatus.IN_PROGRESS);
            stocktakingRepository.update(fetched);

            var updated = stocktakingRepository.findByStocktakingNumber("ST-202501-0001").get();
            assertThat(updated.getStatus()).isEqualTo(StocktakingStatus.IN_PROGRESS);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var stocktaking = createStocktaking("ST-202501-0002", LocalDate.of(2025, 1, 20));
            stocktakingRepository.save(stocktaking);

            var stA = stocktakingRepository.findByStocktakingNumber("ST-202501-0002").get();
            var stB = stocktakingRepository.findByStocktakingNumber("ST-202501-0002").get();

            stA.setStatus(StocktakingStatus.IN_PROGRESS);
            stocktakingRepository.update(stA);

            stB.setStatus(StocktakingStatus.CONFIRMED);
            assertThatThrownBy(() -> stocktakingRepository.update(stB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var stocktaking = createStocktaking("ST-202501-0003", LocalDate.of(2025, 1, 20));
            stocktakingRepository.save(stocktaking);

            var fetched = stocktakingRepository.findByStocktakingNumber("ST-202501-0003").get();
            stocktakingRepository.deleteById(fetched.getId());

            fetched.setStatus(StocktakingStatus.IN_PROGRESS);
            assertThatThrownBy(() -> stocktakingRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で棚卸と棚卸明細を取得できる")
        void canFetchStocktakingWithDetailsUsingJoin() {
            var stocktaking = Stocktaking.builder()
                    .stocktakingNumber("ST-202501-0010")
                    .warehouseCode("WH001")
                    .stocktakingDate(LocalDate.of(2025, 1, 20))
                    .status(StocktakingStatus.DRAFT)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            StocktakingDetail.builder()
                                    .productCode("P001")
                                    .bookQuantity(new BigDecimal("100"))
                                    .actualQuantity(new BigDecimal("98"))
                                    .adjustedFlag(false)
                                    .build(),
                            StocktakingDetail.builder()
                                    .productCode("P001")
                                    .lotNumber("LOT-001")
                                    .bookQuantity(new BigDecimal("50"))
                                    .actualQuantity(new BigDecimal("50"))
                                    .adjustedFlag(false)
                                    .build()
                    ))
                    .build();
            stocktakingRepository.save(stocktaking);

            var result = stocktakingRepository.findWithDetailsByStocktakingNumber("ST-202501-0010");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getStocktakingNumber()).isEqualTo("ST-202501-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("明細がない棚卸も正しく取得できる")
        void canFetchStocktakingWithoutDetails() {
            var stocktaking = createStocktaking("ST-202501-0012", LocalDate.of(2025, 1, 20));
            stocktakingRepository.save(stocktaking);

            var result = stocktakingRepository.findWithDetailsByStocktakingNumber("ST-202501-0012");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getStocktakingNumber()).isEqualTo("ST-202501-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }

    private Stocktaking createStocktaking(String stocktakingNumber, LocalDate stocktakingDate) {
        return Stocktaking.builder()
                .stocktakingNumber(stocktakingNumber)
                .warehouseCode("WH001")
                .stocktakingDate(stocktakingDate)
                .status(StocktakingStatus.DRAFT)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
