package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StockAdjustmentRepository;
import com.example.pms.application.port.out.StocktakingRepository;
import com.example.pms.domain.model.inventory.StockAdjustment;
import com.example.pms.domain.model.inventory.Stocktaking;
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
 * 在庫調整リポジトリテスト.
 */
@DisplayName("在庫調整リポジトリ")
class StockAdjustmentRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private StockAdjustmentRepository stockAdjustmentRepository;

    @Autowired
    private StocktakingRepository stocktakingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stockAdjustmentRepository.deleteAll();
        stocktakingRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC001', 'テスト場所1', '倉庫')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC002', 'テスト場所2', '製造')
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
                .status(StocktakingStatus.CONFIRMED)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build());
    }

    private StockAdjustment createStockAdjustment(String adjustmentNumber, String stocktakingNumber,
                                                    String itemCode, String locationCode,
                                                    BigDecimal adjustmentQuantity) {
        return StockAdjustment.builder()
                .adjustmentNumber(adjustmentNumber)
                .stocktakingNumber(stocktakingNumber)
                .itemCode(itemCode)
                .locationCode(locationCode)
                .adjustmentDate(LocalDate.of(2024, 1, 16))
                .adjusterCode("USER001")
                .adjustmentQuantity(adjustmentQuantity)
                .reasonCode("棚卸差異")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("在庫調整を登録できる")
        void canRegisterStockAdjustment() {
            // Arrange
            StockAdjustment adjustment = createStockAdjustment("ADJ-001", "ST-001", "ITEM001",
                    "LOC001", new BigDecimal("-2.00"));

            // Act
            stockAdjustmentRepository.save(adjustment);

            // Assert
            Optional<StockAdjustment> found = stockAdjustmentRepository.findByAdjustmentNumber("ADJ-001");
            assertThat(found).isPresent();
            assertThat(found.get().getStocktakingNumber()).isEqualTo("ST-001");
            assertThat(found.get().getAdjustmentQuantity()).isEqualByComparingTo(new BigDecimal("-2.00"));
        }

        @Test
        @DisplayName("棚卸番号なしで在庫調整を登録できる")
        void canRegisterStockAdjustmentWithoutStocktaking() {
            // Arrange
            StockAdjustment adjustment = StockAdjustment.builder()
                    .adjustmentNumber("ADJ-002")
                    .stocktakingNumber(null)
                    .itemCode("ITEM001")
                    .locationCode("LOC001")
                    .adjustmentDate(LocalDate.of(2024, 1, 16))
                    .adjusterCode("USER001")
                    .adjustmentQuantity(new BigDecimal("5.00"))
                    .reasonCode("入力訂正")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            // Act
            stockAdjustmentRepository.save(adjustment);

            // Assert
            Optional<StockAdjustment> found = stockAdjustmentRepository.findByAdjustmentNumber("ADJ-002");
            assertThat(found).isPresent();
            assertThat(found.get().getStocktakingNumber()).isNull();
            assertThat(found.get().getReasonCode()).isEqualTo("入力訂正");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            stockAdjustmentRepository.save(createStockAdjustment("ADJ-001", "ST-001", "ITEM001",
                    "LOC001", new BigDecimal("-2.00")));
            stockAdjustmentRepository.save(createStockAdjustment("ADJ-002", "ST-001", "ITEM002",
                    "LOC001", new BigDecimal("-5.00")));
            stockAdjustmentRepository.save(StockAdjustment.builder()
                    .adjustmentNumber("ADJ-003")
                    .stocktakingNumber(null)
                    .itemCode("ITEM001")
                    .locationCode("LOC002")
                    .adjustmentDate(LocalDate.of(2024, 1, 17))
                    .adjusterCode("USER002")
                    .adjustmentQuantity(new BigDecimal("10.00"))
                    .reasonCode("入力訂正")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build());
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<StockAdjustment> adjustment = stockAdjustmentRepository.findByAdjustmentNumber("ADJ-001");
            assertThat(adjustment).isPresent();
            Integer id = adjustment.get().getId();

            // Act
            Optional<StockAdjustment> found = stockAdjustmentRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getAdjustmentNumber()).isEqualTo("ADJ-001");
        }

        @Test
        @DisplayName("在庫調整番号で検索できる")
        void canFindByAdjustmentNumber() {
            // Act
            Optional<StockAdjustment> found = stockAdjustmentRepository.findByAdjustmentNumber("ADJ-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("棚卸番号で検索できる")
        void canFindByStocktakingNumber() {
            // Act
            List<StockAdjustment> found = stockAdjustmentRepository.findByStocktakingNumber("ST-001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(a -> "ST-001".equals(a.getStocktakingNumber()));
        }

        @Test
        @DisplayName("場所コードで検索できる")
        void canFindByLocationCode() {
            // Act
            List<StockAdjustment> found = stockAdjustmentRepository.findByLocationCode("LOC001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(a -> "LOC001".equals(a.getLocationCode()));
        }

        @Test
        @DisplayName("存在しない在庫調整番号で検索すると空を返す")
        void returnsEmptyForNonExistentAdjustmentNumber() {
            // Act
            Optional<StockAdjustment> found = stockAdjustmentRepository.findByAdjustmentNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<StockAdjustment> all = stockAdjustmentRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
