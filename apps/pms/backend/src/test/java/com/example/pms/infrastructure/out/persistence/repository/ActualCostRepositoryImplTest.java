package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ActualCostRepository;
import com.example.pms.application.port.out.LaborHoursRepository;
import com.example.pms.application.port.out.MaterialConsumptionRepository;
import com.example.pms.application.port.out.OverheadAllocationRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.cost.ActualCost;
import com.example.pms.domain.model.cost.MaterialConsumption;
import com.example.pms.domain.model.cost.OverheadAllocation;
import com.example.pms.domain.model.process.LaborHours;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
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
 * 実際原価データリポジトリテスト.
 */
@DisplayName("実際原価データリポジトリ")
class ActualCostRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ActualCostRepository repository;

    @Autowired
    private MaterialConsumptionRepository materialConsumptionRepository;

    @Autowired
    private LaborHoursRepository laborHoursRepository;

    @Autowired
    private OverheadAllocationRepository overheadAllocationRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String workOrderNumber;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        materialConsumptionRepository.deleteAll();
        laborHoursRepository.deleteAll();
        overheadAllocationRepository.deleteAll();
        workOrderRepository.deleteAll();

        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC001', 'テスト場所', '製造')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('MAT001', '2024-01-01', 'テスト材料', '材料')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード", "ステータス")
            VALUES ('ORD-001', '製造', 'ITEM001', '2024-01-15', '2024-01-25', 100, 'LOC001', '確定')
            ON CONFLICT DO NOTHING
            """);

        WorkOrder workOrder = WorkOrder.builder()
                .workOrderNumber("WO-001")
                .orderNumber("ORD-001")
                .workOrderDate(LocalDate.of(2024, 1, 15))
                .itemCode("ITEM001")
                .orderQuantity(new BigDecimal("100.00"))
                .locationCode("LOC001")
                .plannedStartDate(LocalDate.of(2024, 1, 16))
                .plannedEndDate(LocalDate.of(2024, 1, 20))
                .completedQuantity(BigDecimal.ZERO)
                .totalGoodQuantity(BigDecimal.ZERO)
                .totalDefectQuantity(BigDecimal.ZERO)
                .status(WorkOrderStatus.IN_PROGRESS)
                .completedFlag(false)
                .build();
        workOrderRepository.save(workOrder);
        workOrderNumber = "WO-001";
    }

    private ActualCost createActualCost() {
        return ActualCost.builder()
                .workOrderNumber(workOrderNumber)
                .itemCode("ITEM001")
                .completedQuantity(new BigDecimal("100.00"))
                .actualMaterialCost(new BigDecimal("50000.00"))
                .actualLaborCost(new BigDecimal("30000.00"))
                .actualExpense(new BigDecimal("20000.00"))
                .actualManufacturingCost(new BigDecimal("100000.00"))
                .unitCost(new BigDecimal("1000.00"))
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("実際原価を登録できる")
        void canRegister() {
            ActualCost actualCost = createActualCost();
            repository.save(actualCost);

            assertThat(actualCost.getId()).isNotNull();
            Optional<ActualCost> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).isPresent();
            assertThat(found.get().getActualManufacturingCost())
                    .isEqualByComparingTo(new BigDecimal("100000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createActualCost());
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<ActualCost> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<ActualCost> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            Optional<ActualCost> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).isPresent();
            assertThat(found.get().getUnitCost())
                    .isEqualByComparingTo(new BigDecimal("1000.00"));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<ActualCost> all = repository.findAll();
            assertThat(all).hasSize(1);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("実際原価を更新できる")
        void canUpdate() {
            repository.save(createActualCost());
            Optional<ActualCost> saved = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(saved).isPresent();

            ActualCost toUpdate = saved.get();
            toUpdate.setActualMaterialCost(new BigDecimal("55000.00"));
            toUpdate.setActualManufacturingCost(new BigDecimal("105000.00"));

            int result = repository.update(toUpdate);
            assertThat(result).isEqualTo(1);

            Optional<ActualCost> updated = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(updated).isPresent();
            assertThat(updated.get().getActualMaterialCost())
                    .isEqualByComparingTo(new BigDecimal("55000.00"));
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {
        @Test
        @DisplayName("更新時にバージョンがインクリメントされる")
        void versionIncrementedOnUpdate() {
            repository.save(createActualCost());

            Optional<ActualCost> saved = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(saved).isPresent();
            assertThat(saved.get().getVersion()).isEqualTo(1);

            ActualCost toUpdate = saved.get();
            toUpdate.setActualMaterialCost(new BigDecimal("55000.00"));
            repository.update(toUpdate);

            Optional<ActualCost> updated = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(updated).isPresent();
            assertThat(updated.get().getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("古いバージョンで更新すると失敗する")
        void updateFailsWithOldVersion() {
            repository.save(createActualCost());

            Optional<ActualCost> optA = repository.findByWorkOrderNumber(workOrderNumber);
            Optional<ActualCost> optB = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(optA).isPresent();
            assertThat(optB).isPresent();

            ActualCost userA = optA.get();
            ActualCost userB = optB.get();

            userA.setActualMaterialCost(new BigDecimal("55000.00"));
            int resultA = repository.update(userA);
            assertThat(resultA).isEqualTo(1);

            userB.setActualMaterialCost(new BigDecimal("60000.00"));
            int resultB = repository.update(userB);
            assertThat(resultB).isEqualTo(0);
        }

        @Test
        @DisplayName("バージョン番号を取得できる")
        void canFindVersionByWorkOrderNumber() {
            repository.save(createActualCost());

            Optional<Integer> version = repository.findVersionByWorkOrderNumber(workOrderNumber);
            assertThat(version).isPresent();
            assertThat(version.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("存在しない作業指示番号のバージョン取得は空を返す")
        void findVersionReturnsEmptyForNonExistent() {
            Optional<Integer> version = repository.findVersionByWorkOrderNumber("NOTEXIST");
            assertThat(version).isEmpty();
        }
    }

    @Nested
    @DisplayName("原価再計算")
    class Recalculate {
        @Test
        @DisplayName("原価を再計算できる")
        void canRecalculate() {
            repository.save(createActualCost());
            Optional<ActualCost> saved = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(saved).isPresent();

            int result = repository.recalculate(
                    workOrderNumber,
                    saved.get().getVersion(),
                    new BigDecimal("55000.00"),
                    new BigDecimal("35000.00"),
                    new BigDecimal("25000.00"));
            assertThat(result).isEqualTo(1);

            Optional<ActualCost> updated = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(updated).isPresent();
            assertThat(updated.get().getActualMaterialCost())
                    .isEqualByComparingTo(new BigDecimal("55000.00"));
            assertThat(updated.get().getActualLaborCost())
                    .isEqualByComparingTo(new BigDecimal("35000.00"));
            assertThat(updated.get().getActualExpense())
                    .isEqualByComparingTo(new BigDecimal("25000.00"));
            assertThat(updated.get().getActualManufacturingCost())
                    .isEqualByComparingTo(new BigDecimal("115000.00"));
            assertThat(updated.get().getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("古いバージョンで再計算すると失敗する")
        void recalculateFailsWithOldVersion() {
            repository.save(createActualCost());
            Optional<ActualCost> saved = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(saved).isPresent();

            // 先に更新して バージョンを2にする
            ActualCost toUpdate = saved.get();
            toUpdate.setActualMaterialCost(new BigDecimal("55000.00"));
            repository.update(toUpdate);

            // 古いバージョン(1)で再計算 → 失敗
            int result = repository.recalculate(
                    workOrderNumber,
                    1,
                    new BigDecimal("60000.00"),
                    new BigDecimal("40000.00"),
                    new BigDecimal("30000.00"));
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("リレーション")
    class Relation {
        @BeforeEach
        void setUpRelationData() {
            repository.save(createActualCost());

            // 工程マスタを追加
            jdbcTemplate.execute("""
                INSERT INTO "工程マスタ" ("工程コード", "工程名", "場所コード")
                VALUES ('PROC001', 'テスト工程', 'LOC001')
                ON CONFLICT DO NOTHING
                """);

            // 部門マスタを追加
            jdbcTemplate.execute("""
                INSERT INTO "部門マスタ" ("部門コード", "部門名", "有効開始日")
                VALUES ('DEPT001', 'テスト部門', '2024-01-01')
                ON CONFLICT DO NOTHING
                """);

            // 担当者マスタを追加
            jdbcTemplate.execute("""
                INSERT INTO "担当者マスタ" ("担当者コード", "適用開始日", "担当者名", "部門コード")
                VALUES ('EMP001', '2024-01-01', 'テスト担当', 'DEPT001')
                ON CONFLICT DO NOTHING
                """);

            MaterialConsumption consumption = MaterialConsumption.builder()
                    .workOrderNumber(workOrderNumber)
                    .materialCode("MAT001")
                    .consumptionDate(LocalDate.of(2024, 1, 17))
                    .consumptionQuantity(new BigDecimal("10.00"))
                    .unitPrice(new BigDecimal("100.00"))
                    .consumptionAmount(new BigDecimal("1000.00"))
                    .isDirect(true)
                    .build();
            materialConsumptionRepository.save(consumption);

            LaborHours laborHoursData = LaborHours.builder()
                    .laborHoursNumber("LH-001")
                    .workOrderNumber(workOrderNumber)
                    .itemCode("ITEM001")
                    .sequence(1)
                    .processCode("PROC001")
                    .departmentCode("DEPT001")
                    .employeeCode("EMP001")
                    .workDate(LocalDate.of(2024, 1, 17))
                    .hours(new BigDecimal("8.00"))
                    .build();
            laborHoursRepository.save(laborHoursData);

            OverheadAllocation allocation = OverheadAllocation.builder()
                    .workOrderNumber(workOrderNumber)
                    .accountingPeriod("2024-01")
                    .allocationBasis("DIRECT_LABOR")
                    .basisAmount(new BigDecimal("30000.00"))
                    .allocationRate(new BigDecimal("0.15"))
                    .allocatedAmount(new BigDecimal("4500.00"))
                    .build();
            overheadAllocationRepository.save(allocation);
        }

        @Test
        @DisplayName("リレーションを含めて取得できる")
        void canFindWithRelations() {
            Optional<ActualCost> found = repository
                    .findByWorkOrderNumberWithRelations(workOrderNumber);

            assertThat(found).isPresent();
            assertThat(found.get().getMaterialConsumptions()).hasSize(1);
            assertThat(found.get().getLaborHours()).hasSize(1);
            assertThat(found.get().getOverheadAllocations()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("作業指示番号で削除できる")
        void canDeleteByWorkOrderNumber() {
            repository.save(createActualCost());

            repository.deleteByWorkOrderNumber(workOrderNumber);

            assertThat(repository.findByWorkOrderNumber(workOrderNumber)).isEmpty();
        }
    }
}
