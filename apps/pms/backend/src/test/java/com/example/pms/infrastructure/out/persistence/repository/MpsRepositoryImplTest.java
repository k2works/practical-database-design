package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.MpsRepository;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
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
 * 基準生産計画リポジトリテスト.
 */
@DisplayName("基準生産計画リポジトリ")
class MpsRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private MpsRepository mpsRepository;

    @BeforeEach
    void setUp() {
        mpsRepository.deleteAll();
    }

    private MasterProductionSchedule createMps(String mpsNumber, String itemCode, PlanStatus status) {
        return MasterProductionSchedule.builder()
                .mpsNumber(mpsNumber)
                .planDate(LocalDate.of(2024, 1, 1))
                .itemCode(itemCode)
                .planQuantity(new BigDecimal("100.00"))
                .dueDate(LocalDate.of(2024, 1, 15))
                .status(status)
                .locationCode("WH001")
                .remarks("テスト備考")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    class Registration {

        @Test
        @DisplayName("基準生産計画を登録できる")
        void canRegisterMps() {
            // Arrange
            MasterProductionSchedule mps = createMps("MPS001", "ITEM001", PlanStatus.DRAFT);

            // Act
            mpsRepository.save(mps);

            // Assert
            Optional<MasterProductionSchedule> found = mpsRepository.findByMpsNumber("MPS001");
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getStatus()).isEqualTo(PlanStatus.DRAFT);
            assertThat(found.get().getPlanQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("各計画ステータスを登録できる")
        void canRegisterAllPlanStatuses() {
            // Arrange & Act & Assert
            for (PlanStatus status : PlanStatus.values()) {
                MasterProductionSchedule mps = MasterProductionSchedule.builder()
                        .mpsNumber("MPS_" + status.name())
                        .planDate(LocalDate.of(2024, 1, 1))
                        .itemCode("ITEM001")
                        .planQuantity(new BigDecimal("100.00"))
                        .dueDate(LocalDate.of(2024, 1, 15))
                        .status(status)
                        .locationCode("WH001")
                        .build();
                mpsRepository.save(mps);

                Optional<MasterProductionSchedule> found = mpsRepository.findByMpsNumber("MPS_" + status.name());
                assertThat(found).isPresent();
                assertThat(found.get().getStatus()).isEqualTo(status);
                assertThat(found.get().getStatus().getDisplayName()).isEqualTo(status.getDisplayName());
            }
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            mpsRepository.save(createMps("MPS001", "PROD001", PlanStatus.DRAFT));
            mpsRepository.save(createMps("MPS002", "PROD002", PlanStatus.CONFIRMED));
            mpsRepository.save(createMps("MPS003", "PROD003", PlanStatus.DRAFT));
            mpsRepository.save(createMps("MPS004", "PROD004", PlanStatus.EXPANDED));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<MasterProductionSchedule> mps = mpsRepository.findByMpsNumber("MPS001");
            assertThat(mps).isPresent();
            Integer id = mps.get().getId();

            // Act
            Optional<MasterProductionSchedule> found = mpsRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getMpsNumber()).isEqualTo("MPS001");
        }

        @Test
        @DisplayName("MPS番号で検索できる")
        void canFindByMpsNumber() {
            // Act
            Optional<MasterProductionSchedule> found = mpsRepository.findByMpsNumber("MPS002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("PROD002");
            assertThat(found.get().getStatus()).isEqualTo(PlanStatus.CONFIRMED);
        }

        @Test
        @DisplayName("存在しないMPS番号で検索すると空を返す")
        void returnsEmptyForNonExistentMpsNumber() {
            // Act
            Optional<MasterProductionSchedule> found = mpsRepository.findByMpsNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            // Act
            List<MasterProductionSchedule> drafts = mpsRepository.findByStatus(PlanStatus.DRAFT);

            // Assert
            assertThat(drafts).hasSize(2);
            assertThat(drafts).allMatch(mps -> mps.getStatus() == PlanStatus.DRAFT);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<MasterProductionSchedule> all = mpsRepository.findAll();

            // Assert
            assertThat(all).hasSize(4);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("ステータスを更新できる")
        void canUpdateStatus() {
            // Arrange
            MasterProductionSchedule mps = createMps("MPS001", "PROD001", PlanStatus.DRAFT);
            mpsRepository.save(mps);
            Optional<MasterProductionSchedule> saved = mpsRepository.findByMpsNumber("MPS001");
            assertThat(saved).isPresent();

            // Act
            mpsRepository.updateStatus(saved.get().getId(), PlanStatus.CONFIRMED);

            // Assert
            Optional<MasterProductionSchedule> updated = mpsRepository.findByMpsNumber("MPS001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getStatus()).isEqualTo(PlanStatus.CONFIRMED);
        }
    }
}
