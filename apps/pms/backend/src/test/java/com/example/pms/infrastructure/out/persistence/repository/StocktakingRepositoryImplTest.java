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
 * 棚卸リポジトリテスト.
 */
@DisplayName("棚卸リポジトリ")
class StocktakingRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private StocktakingRepository stocktakingRepository;

    @Autowired
    private StocktakingDetailRepository stocktakingDetailRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stocktakingDetailRepository.deleteAll();
        stocktakingRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC001', 'テスト場所', '倉庫')
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

    private Stocktaking createStocktaking(String stocktakingNumber, StocktakingStatus status) {
        return Stocktaking.builder()
                .stocktakingNumber(stocktakingNumber)
                .locationCode("LOC001")
                .stocktakingDate(LocalDate.of(2024, 1, 15))
                .status(status)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("発行済の棚卸を登録できる")
        void canRegisterIssuedStocktaking() {
            // Arrange
            Stocktaking stocktaking = createStocktaking("ST-001", StocktakingStatus.ISSUED);

            // Act
            stocktakingRepository.save(stocktaking);

            // Assert
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumber("ST-001");
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(StocktakingStatus.ISSUED);
            assertThat(found.get().getLocationCode()).isEqualTo("LOC001");
        }

        @Test
        @DisplayName("確定済の棚卸を登録できる")
        void canRegisterConfirmedStocktaking() {
            // Arrange
            Stocktaking stocktaking = createStocktaking("ST-002", StocktakingStatus.CONFIRMED);

            // Act
            stocktakingRepository.save(stocktaking);

            // Assert
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumber("ST-002");
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(StocktakingStatus.CONFIRMED);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            stocktakingRepository.save(createStocktaking("ST-001", StocktakingStatus.ISSUED));
            stocktakingRepository.save(createStocktaking("ST-002", StocktakingStatus.ENTERED));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Stocktaking> stocktaking = stocktakingRepository.findByStocktakingNumber("ST-001");
            assertThat(stocktaking).isPresent();
            Integer id = stocktaking.get().getId();

            // Act
            Optional<Stocktaking> found = stocktakingRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getStocktakingNumber()).isEqualTo("ST-001");
        }

        @Test
        @DisplayName("棚卸番号で検索できる")
        void canFindByStocktakingNumber() {
            // Act
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumber("ST-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(StocktakingStatus.ENTERED);
        }

        @Test
        @DisplayName("場所コードで検索できる")
        void canFindByLocationCode() {
            // Act
            List<Stocktaking> found = stocktakingRepository.findByLocationCode("LOC001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(st -> "LOC001".equals(st.getLocationCode()));
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            // Act
            List<Stocktaking> found = stocktakingRepository.findByStatus(StocktakingStatus.ISSUED);

            // Assert
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getStocktakingNumber()).isEqualTo("ST-001");
        }

        @Test
        @DisplayName("存在しない棚卸番号で検索すると空を返す")
        void returnsEmptyForNonExistentStocktakingNumber() {
            // Act
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Stocktaking> all = stocktakingRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }

    @Nested
    @DisplayName("リレーション")
    class Relation {

        private StocktakingDetail createDetail(String stocktakingNumber, int lineNumber, String itemCode) {
            return StocktakingDetail.builder()
                    .stocktakingNumber(stocktakingNumber)
                    .lineNumber(lineNumber)
                    .itemCode(itemCode)
                    .bookQuantity(new BigDecimal("100.00"))
                    .actualQuantity(new BigDecimal("95.00"))
                    .differenceQuantity(new BigDecimal("-5.00"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();
        }

        @Test
        @DisplayName("明細を含めて取得できる")
        void canFindWithDetails() {
            // Arrange
            Stocktaking stocktaking = createStocktaking("ST-REL-001", StocktakingStatus.ENTERED);
            stocktakingRepository.save(stocktaking);

            StocktakingDetail detail1 = createDetail("ST-REL-001", 1, "ITEM001");
            StocktakingDetail detail2 = createDetail("ST-REL-001", 2, "ITEM002");
            stocktakingDetailRepository.save(detail1);
            stocktakingDetailRepository.save(detail2);

            // Act
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumberWithDetails("ST-REL-001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getDetails()).hasSize(2);
            assertThat(found.get().getDetails().get(0).getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getDetails().get(1).getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("明細がない場合は空のリストを返す")
        void returnsEmptyListWhenNoDetails() {
            // Arrange
            Stocktaking stocktaking = createStocktaking("ST-REL-002", StocktakingStatus.ISSUED);
            stocktakingRepository.save(stocktaking);

            // Act
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumberWithDetails("ST-REL-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getDetails()).isEmpty();
        }

        @Test
        @DisplayName("バージョンが取得できる")
        void canGetVersion() {
            // Arrange
            Stocktaking stocktaking = createStocktaking("ST-VER-001", StocktakingStatus.ISSUED);
            stocktakingRepository.save(stocktaking);

            // Act
            Optional<Stocktaking> found = stocktakingRepository.findByStocktakingNumber("ST-VER-001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getVersion()).isEqualTo(1);
        }
    }
}
