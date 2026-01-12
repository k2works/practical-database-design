package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StockRepository;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 在庫情報リポジトリテスト.
 */
@DisplayName("在庫情報リポジトリ")
class StockRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();

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
            VALUES ('ITEM002', '2024-01-01', 'テスト品目2', '材料')
            ON CONFLICT DO NOTHING
            """);
    }

    private Stock createStock(String locationCode, String itemCode, BigDecimal quantity) {
        return Stock.builder()
                .locationCode(locationCode)
                .itemCode(itemCode)
                .stockQuantity(quantity)
                .passedQuantity(quantity)
                .defectiveQuantity(BigDecimal.ZERO)
                .uninspectedQuantity(BigDecimal.ZERO)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("在庫情報を登録できる")
        void canRegisterStock() {
            // Arrange
            Stock stock = createStock("LOC001", "ITEM001", new BigDecimal("100.00"));

            // Act
            stockRepository.save(stock);

            // Assert
            Optional<Stock> found = stockRepository.findByLocationAndItem("LOC001", "ITEM001");
            assertThat(found).isPresent();
            assertThat(found.get().getStockQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(found.get().getPassedQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            stockRepository.save(createStock("LOC001", "ITEM001", new BigDecimal("100.00")));
            stockRepository.save(createStock("LOC001", "ITEM002", new BigDecimal("50.00")));
            stockRepository.save(createStock("LOC002", "ITEM001", new BigDecimal("30.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Stock> stock = stockRepository.findByLocationAndItem("LOC001", "ITEM001");
            assertThat(stock).isPresent();
            Integer id = stock.get().getId();

            // Act
            Optional<Stock> found = stockRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getLocationCode()).isEqualTo("LOC001");
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("場所コードと品目コードで検索できる")
        void canFindByLocationAndItem() {
            // Act
            Optional<Stock> found = stockRepository.findByLocationAndItem("LOC001", "ITEM002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getStockQuantity()).isEqualByComparingTo(new BigDecimal("50.00"));
        }

        @Test
        @DisplayName("場所コードで検索できる")
        void canFindByLocation() {
            // Act
            List<Stock> found = stockRepository.findByLocation("LOC001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(s -> "LOC001".equals(s.getLocationCode()));
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItem() {
            // Act
            List<Stock> found = stockRepository.findByItem("ITEM001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(s -> "ITEM001".equals(s.getItemCode()));
        }

        @Test
        @DisplayName("存在しない場所コードと品目コードで検索すると空を返す")
        void returnsEmptyForNonExistentLocationAndItem() {
            // Act
            Optional<Stock> found = stockRepository.findByLocationAndItem("NOTEXIST", "ITEM001");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Stock> all = stockRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
