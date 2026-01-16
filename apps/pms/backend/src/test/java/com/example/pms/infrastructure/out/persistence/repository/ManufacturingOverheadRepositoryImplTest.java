package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ManufacturingOverheadRepository;
import com.example.pms.domain.model.cost.ManufacturingOverhead;
import com.example.pms.testsetup.BaseIntegrationTest;
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
 * 製造間接費マスタリポジトリテスト.
 */
@DisplayName("製造間接費マスタリポジトリ")
class ManufacturingOverheadRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ManufacturingOverheadRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private ManufacturingOverhead createOverhead(String period, String category, BigDecimal amount) {
        return ManufacturingOverhead.builder()
                .accountingPeriod(period)
                .costCategory(category)
                .costCategoryName("テスト費用区分")
                .amount(amount)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("製造間接費を登録できる")
        void canRegister() {
            ManufacturingOverhead overhead = createOverhead("2024-01",
                    "INDIRECT_MAT", new BigDecimal("100000.00"));
            repository.save(overhead);

            assertThat(overhead.getId()).isNotNull();
            List<ManufacturingOverhead> found = repository.findByAccountingPeriod("2024-01");
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getAmount())
                    .isEqualByComparingTo(new BigDecimal("100000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createOverhead("2024-01", "INDIRECT_MAT",
                    new BigDecimal("100000.00")));
            repository.save(createOverhead("2024-01", "INDIRECT_LABOR",
                    new BigDecimal("200000.00")));
            repository.save(createOverhead("2024-02", "INDIRECT_MAT",
                    new BigDecimal("150000.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<ManufacturingOverhead> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<ManufacturingOverhead> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("会計期間で検索できる")
        void canFindByAccountingPeriod() {
            List<ManufacturingOverhead> found = repository.findByAccountingPeriod("2024-01");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("会計期間と費用区分で検索できる")
        void canFindByAccountingPeriodAndCostCategory() {
            Optional<ManufacturingOverhead> found = repository
                    .findByAccountingPeriodAndCostCategory("2024-01", "INDIRECT_MAT");

            assertThat(found).isPresent();
            assertThat(found.get().getAmount())
                    .isEqualByComparingTo(new BigDecimal("100000.00"));
        }

        @Test
        @DisplayName("会計期間の合計を取得できる")
        void canSumByAccountingPeriod() {
            BigDecimal sum = repository.sumByAccountingPeriod("2024-01");
            assertThat(sum).isEqualByComparingTo(new BigDecimal("300000.00"));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<ManufacturingOverhead> all = repository.findAll();
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("製造間接費を更新できる")
        void canUpdate() {
            repository.save(createOverhead("2024-01", "INDIRECT_MAT",
                    new BigDecimal("100000.00")));
            List<ManufacturingOverhead> saved = repository.findByAccountingPeriod("2024-01");
            ManufacturingOverhead toUpdate = saved.get(0);
            toUpdate.setAmount(new BigDecimal("120000.00"));

            int result = repository.update(toUpdate);
            assertThat(result).isEqualTo(1);

            Optional<ManufacturingOverhead> updated = repository.findById(toUpdate.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getAmount())
                    .isEqualByComparingTo(new BigDecimal("120000.00"));
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {
        @Test
        @DisplayName("古いバージョンで更新すると失敗する")
        void updateFailsWithOldVersion() {
            repository.save(createOverhead("2024-01", "INDIRECT_MAT",
                    new BigDecimal("100000.00")));

            List<ManufacturingOverhead> all = repository.findByAccountingPeriod("2024-01");
            ManufacturingOverhead userA = all.get(0);
            ManufacturingOverhead userB = repository.findById(userA.getId()).orElseThrow();

            userA.setAmount(new BigDecimal("120000.00"));
            int resultA = repository.update(userA);
            assertThat(resultA).isEqualTo(1);

            userB.setAmount(new BigDecimal("130000.00"));
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
            repository.save(createOverhead("2024-01", "INDIRECT_MAT",
                    new BigDecimal("100000.00")));
            List<ManufacturingOverhead> saved = repository.findByAccountingPeriod("2024-01");
            Integer id = saved.get(0).getId();

            repository.deleteById(id);

            assertThat(repository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("会計期間で削除できる")
        void canDeleteByAccountingPeriod() {
            repository.save(createOverhead("2024-01", "INDIRECT_MAT",
                    new BigDecimal("100000.00")));
            repository.save(createOverhead("2024-01", "INDIRECT_LABOR",
                    new BigDecimal("200000.00")));

            repository.deleteByAccountingPeriod("2024-01");

            assertThat(repository.findByAccountingPeriod("2024-01")).isEmpty();
        }
    }
}
