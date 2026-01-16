package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StandardCostRepository;
import com.example.pms.domain.model.cost.StandardCost;
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
 * 標準原価マスタリポジトリテスト.
 */
@DisplayName("標準原価マスタリポジトリ")
class StandardCostRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private StandardCostRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目A', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM002', '2024-01-01', 'テスト品目B', '製品')
            ON CONFLICT DO NOTHING
            """);
    }

    private StandardCost createStandardCost(String itemCode,
                                            LocalDate startDate,
                                            LocalDate endDate) {
        return StandardCost.builder()
                .itemCode(itemCode)
                .effectiveStartDate(startDate)
                .effectiveEndDate(endDate)
                .standardMaterialCost(new BigDecimal("500.00"))
                .standardLaborCost(new BigDecimal("300.00"))
                .standardExpense(new BigDecimal("200.00"))
                .standardManufacturingCost(new BigDecimal("1000.00"))
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("標準原価を登録できる")
        void canRegister() {
            StandardCost cost = createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), null);
            repository.save(cost);

            assertThat(cost.getId()).isNotNull();
            List<StandardCost> found = repository.findByItemCode("ITEM001");
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getStandardManufacturingCost())
                    .isEqualByComparingTo(new BigDecimal("1000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30)));
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 7, 1), null));
            repository.save(createStandardCost("ITEM002",
                    LocalDate.of(2024, 1, 1), null));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<StandardCost> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<StandardCost> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItemCode() {
            List<StandardCost> found = repository.findByItemCode("ITEM001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("有効な標準原価を取得できる")
        void canFindValidStandardCost() {
            Optional<StandardCost> found = repository.findValidByItemCode(
                    "ITEM001", LocalDate.of(2024, 3, 15));

            assertThat(found).isPresent();
            assertThat(found.get().getEffectiveStartDate())
                    .isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        @DisplayName("新しい期間の標準原価を取得できる")
        void canFindNewerStandardCost() {
            Optional<StandardCost> found = repository.findValidByItemCode(
                    "ITEM001", LocalDate.of(2024, 8, 1));

            assertThat(found).isPresent();
            assertThat(found.get().getEffectiveStartDate())
                    .isEqualTo(LocalDate.of(2024, 7, 1));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<StandardCost> all = repository.findAll();
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("標準原価を更新できる")
        void canUpdate() {
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), null));
            List<StandardCost> saved = repository.findByItemCode("ITEM001");
            StandardCost toUpdate = saved.get(0);
            toUpdate.setStandardMaterialCost(new BigDecimal("550.00"));
            toUpdate.setStandardManufacturingCost(new BigDecimal("1050.00"));

            int result = repository.update(toUpdate);
            assertThat(result).isEqualTo(1);

            Optional<StandardCost> updated = repository.findById(toUpdate.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getStandardMaterialCost())
                    .isEqualByComparingTo(new BigDecimal("550.00"));
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {
        @Test
        @DisplayName("更新時にバージョンがインクリメントされる")
        void versionIncrementedOnUpdate() {
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), null));

            List<StandardCost> saved = repository.findByItemCode("ITEM001");
            assertThat(saved.get(0).getVersion()).isEqualTo(1);

            StandardCost toUpdate = saved.get(0);
            toUpdate.setStandardMaterialCost(new BigDecimal("550.00"));
            repository.update(toUpdate);

            Optional<StandardCost> updated = repository.findById(toUpdate.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("古いバージョンで更新すると失敗する")
        void updateFailsWithOldVersion() {
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), null));

            List<StandardCost> all = repository.findByItemCode("ITEM001");
            StandardCost userA = all.get(0);
            StandardCost userB = repository.findById(userA.getId()).orElseThrow();

            userA.setStandardMaterialCost(new BigDecimal("550.00"));
            int resultA = repository.update(userA);
            assertThat(resultA).isEqualTo(1);

            userB.setStandardMaterialCost(new BigDecimal("600.00"));
            int resultB = repository.update(userB);
            assertThat(resultB).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("IDで削除できる")
        void canDeleteById() {
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), null));
            List<StandardCost> saved = repository.findByItemCode("ITEM001");
            Integer id = saved.get(0).getId();

            repository.deleteById(id);

            assertThat(repository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("品目コードで削除できる")
        void canDeleteByItemCode() {
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30)));
            repository.save(createStandardCost("ITEM001",
                    LocalDate.of(2024, 7, 1), null));

            repository.deleteByItemCode("ITEM001");

            assertThat(repository.findByItemCode("ITEM001")).isEmpty();
        }
    }

    @Nested
    @DisplayName("ドメインロジック")
    class DomainLogic {
        @Test
        @DisplayName("有効期間内かチェックできる")
        void canCheckValidAt() {
            StandardCost cost = createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30));
            repository.save(cost);

            Optional<StandardCost> found = repository.findByItemCode("ITEM001").stream().findFirst();
            assertThat(found).isPresent();
            assertThat(found.get().isValidAt(LocalDate.of(2024, 3, 15))).isTrue();
            assertThat(found.get().isValidAt(LocalDate.of(2024, 7, 1))).isFalse();
        }

        @Test
        @DisplayName("標準製造原価を計算できる")
        void canCalculateStandardManufacturingCost() {
            StandardCost cost = createStandardCost("ITEM001",
                    LocalDate.of(2024, 1, 1), null);

            BigDecimal calculated = cost.calculateStandardManufacturingCost();
            assertThat(calculated).isEqualByComparingTo(new BigDecimal("1000.00"));
        }
    }
}
