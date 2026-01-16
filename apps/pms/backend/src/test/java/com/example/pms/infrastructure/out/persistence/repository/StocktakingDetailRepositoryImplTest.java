package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StocktakingDetailRepository;
import com.example.pms.application.port.out.StocktakingRepository;
import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingDetail;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 棚卸明細リポジトリテスト.
 */
@DisplayName("棚卸明細リポジトリ")
class StocktakingDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private StocktakingDetailRepository stocktakingDetailRepository;

    @Autowired
    private StocktakingRepository stocktakingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stocktakingDetailRepository.deleteAll();
        stocktakingRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC001', 'テスト場所1', '倉庫')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目1', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM002', '2024-01-01', 'テスト品目2', '部品')
            ON CONFLICT DO NOTHING
            """);

        // Create parent stocktaking
        stocktakingRepository.save(Stocktaking.builder()
                .stocktakingNumber("ST-001")
                .locationCode("LOC001")
                .stocktakingDate(LocalDate.of(2024, 1, 15))
                .status(StocktakingStatus.ISSUED)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build());
    }

    private StocktakingDetail createStocktakingDetail(String stocktakingNumber, Integer lineNumber,
                                                        String itemCode, BigDecimal bookQuantity,
                                                        BigDecimal actualQuantity) {
        BigDecimal differenceQuantity = actualQuantity != null
                ? actualQuantity.subtract(bookQuantity)
                : null;
        return StocktakingDetail.builder()
                .stocktakingNumber(stocktakingNumber)
                .lineNumber(lineNumber)
                .itemCode(itemCode)
                .bookQuantity(bookQuantity)
                .actualQuantity(actualQuantity)
                .differenceQuantity(differenceQuantity)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("棚卸明細を登録できる")
        void canRegisterStocktakingDetail() {
            // Arrange
            StocktakingDetail detail = createStocktakingDetail("ST-001", 1, "ITEM001",
                    new BigDecimal("100.00"), new BigDecimal("98.00"));

            // Act
            stocktakingDetailRepository.save(detail);

            // Assert
            Optional<StocktakingDetail> found =
                    stocktakingDetailRepository.findByStocktakingNumberAndLineNumber("ST-001", 1);
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getBookQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(found.get().getActualQuantity()).isEqualByComparingTo(new BigDecimal("98.00"));
            assertThat(found.get().getDifferenceQuantity()).isEqualByComparingTo(new BigDecimal("-2.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            stocktakingDetailRepository.save(createStocktakingDetail("ST-001", 1, "ITEM001",
                    new BigDecimal("100.00"), new BigDecimal("98.00")));
            stocktakingDetailRepository.save(createStocktakingDetail("ST-001", 2, "ITEM002",
                    new BigDecimal("50.00"), null));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<StocktakingDetail> detail =
                    stocktakingDetailRepository.findByStocktakingNumberAndLineNumber("ST-001", 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<StocktakingDetail> found = stocktakingDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("棚卸番号と行番号で検索できる")
        void canFindByStocktakingNumberAndLineNumber() {
            // Act
            Optional<StocktakingDetail> found =
                    stocktakingDetailRepository.findByStocktakingNumberAndLineNumber("ST-001", 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
            assertThat(found.get().getActualQuantity()).isNull();
        }

        @Test
        @DisplayName("棚卸番号で検索できる")
        void canFindByStocktakingNumber() {
            // Act
            List<StocktakingDetail> found =
                    stocktakingDetailRepository.findByStocktakingNumber("ST-001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(d -> "ST-001".equals(d.getStocktakingNumber()));
        }

        @Test
        @DisplayName("存在しない棚卸番号で検索すると空を返す")
        void returnsEmptyForNonExistentStocktakingNumber() {
            // Act
            Optional<StocktakingDetail> found =
                    stocktakingDetailRepository.findByStocktakingNumberAndLineNumber("NOTEXIST", 1);

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<StocktakingDetail> all = stocktakingDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
