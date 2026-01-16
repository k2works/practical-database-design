package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WageRateRepository;
import com.example.pms.domain.model.cost.WageRate;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 賃率マスタリポジトリテスト.
 */
@DisplayName("賃率マスタリポジトリ")
class WageRateRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private WageRateRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private WageRate createWageRate(String categoryCode, LocalDate startDate, LocalDate endDate) {
        return WageRate.builder()
                .workerCategoryCode(categoryCode)
                .workerCategoryName("テスト作業者区分")
                .effectiveStartDate(startDate)
                .effectiveEndDate(endDate)
                .hourlyRate(new BigDecimal("2500.00"))
                .isDirect(true)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("賃率を登録できる")
        void canRegister() {
            WageRate wageRate = createWageRate("WC001",
                    LocalDate.of(2024, 1, 1), null);
            repository.save(wageRate);

            assertThat(wageRate.getId()).isNotNull();
            List<WageRate> found = repository.findByWorkerCategoryCode("WC001");
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getHourlyRate())
                    .isEqualByComparingTo(new BigDecimal("2500.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createWageRate("WC001",
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30)));
            repository.save(createWageRate("WC001",
                    LocalDate.of(2024, 7, 1), null));
            repository.save(createWageRate("WC002",
                    LocalDate.of(2024, 1, 1), null));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<WageRate> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<WageRate> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("作業者区分コードで検索できる")
        void canFindByWorkerCategoryCode() {
            List<WageRate> found = repository.findByWorkerCategoryCode("WC001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("有効な賃率を取得できる")
        void canFindValidWageRate() {
            Optional<WageRate> found = repository.findValidByWorkerCategoryCode(
                    "WC001", LocalDate.of(2024, 3, 15));

            assertThat(found).isPresent();
            assertThat(found.get().getEffectiveStartDate())
                    .isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        @DisplayName("新しい期間の賃率を取得できる")
        void canFindNewerWageRate() {
            Optional<WageRate> found = repository.findValidByWorkerCategoryCode(
                    "WC001", LocalDate.of(2024, 8, 1));

            assertThat(found).isPresent();
            assertThat(found.get().getEffectiveStartDate())
                    .isEqualTo(LocalDate.of(2024, 7, 1));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<WageRate> all = repository.findAll();
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("賃率を更新できる")
        void canUpdate() {
            repository.save(createWageRate("WC001",
                    LocalDate.of(2024, 1, 1), null));
            List<WageRate> saved = repository.findByWorkerCategoryCode("WC001");
            WageRate toUpdate = saved.get(0);
            toUpdate.setHourlyRate(new BigDecimal("3000.00"));

            int result = repository.update(toUpdate);
            assertThat(result).isEqualTo(1);

            Optional<WageRate> updated = repository.findById(toUpdate.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getHourlyRate())
                    .isEqualByComparingTo(new BigDecimal("3000.00"));
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {
        @Test
        @DisplayName("更新時にバージョンがインクリメントされる")
        void versionIncrementedOnUpdate() {
            repository.save(createWageRate("WC001",
                    LocalDate.of(2024, 1, 1), null));

            List<WageRate> saved = repository.findByWorkerCategoryCode("WC001");
            assertThat(saved.get(0).getVersion()).isEqualTo(1);

            WageRate toUpdate = saved.get(0);
            toUpdate.setHourlyRate(new BigDecimal("3000.00"));
            repository.update(toUpdate);

            Optional<WageRate> updated = repository.findById(toUpdate.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("古いバージョンで更新すると失敗する")
        void updateFailsWithOldVersion() {
            repository.save(createWageRate("WC001",
                    LocalDate.of(2024, 1, 1), null));

            List<WageRate> all = repository.findByWorkerCategoryCode("WC001");
            WageRate userA = all.get(0);
            WageRate userB = repository.findById(userA.getId()).orElseThrow();

            userA.setHourlyRate(new BigDecimal("3000.00"));
            int resultA = repository.update(userA);
            assertThat(resultA).isEqualTo(1);

            userB.setHourlyRate(new BigDecimal("3500.00"));
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
            repository.save(createWageRate("WC001",
                    LocalDate.of(2024, 1, 1), null));
            List<WageRate> saved = repository.findByWorkerCategoryCode("WC001");
            Integer id = saved.get(0).getId();

            repository.deleteById(id);

            assertThat(repository.findById(id)).isEmpty();
        }
    }
}
