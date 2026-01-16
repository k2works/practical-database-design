package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.MaterialConsumptionRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.cost.MaterialConsumption;
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
 * 材料消費データリポジトリテスト.
 */
@DisplayName("材料消費データリポジトリ")
class MaterialConsumptionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private MaterialConsumptionRepository repository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String workOrderNumber;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
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

    private MaterialConsumption createConsumption(boolean isDirect, BigDecimal amount) {
        return MaterialConsumption.builder()
                .workOrderNumber(workOrderNumber)
                .materialCode("MAT001")
                .consumptionDate(LocalDate.of(2024, 1, 17))
                .consumptionQuantity(new BigDecimal("10.00"))
                .unitPrice(new BigDecimal("100.00"))
                .consumptionAmount(amount)
                .isDirect(isDirect)
                .remarks("テスト材料消費")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("材料消費データを登録できる")
        void canRegister() {
            MaterialConsumption consumption = createConsumption(true, new BigDecimal("1000.00"));
            repository.save(consumption);

            assertThat(consumption.getId()).isNotNull();
            List<MaterialConsumption> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getConsumptionAmount())
                    .isEqualByComparingTo(new BigDecimal("1000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createConsumption(true, new BigDecimal("1000.00")));
            repository.save(createConsumption(true, new BigDecimal("2000.00")));
            repository.save(createConsumption(false, new BigDecimal("500.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<MaterialConsumption> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<MaterialConsumption> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            List<MaterialConsumption> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).hasSize(3);
        }

        @Test
        @DisplayName("直接材料費合計を取得できる")
        void canSumDirectMaterialCost() {
            BigDecimal sum = repository.sumDirectMaterialCostByWorkOrderNumber(workOrderNumber);
            assertThat(sum).isEqualByComparingTo(new BigDecimal("3000.00"));
        }

        @Test
        @DisplayName("間接材料費合計を取得できる")
        void canSumIndirectMaterialCost() {
            BigDecimal sum = repository.sumIndirectMaterialCostByPeriod(
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
            assertThat(sum).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<MaterialConsumption> all = repository.findAll();
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("材料消費データを更新できる")
        void canUpdate() {
            repository.save(createConsumption(true, new BigDecimal("1000.00")));
            List<MaterialConsumption> saved = repository.findByWorkOrderNumber(workOrderNumber);
            MaterialConsumption toUpdate = saved.get(0);
            toUpdate.setRemarks("更新後の備考");
            toUpdate.setConsumptionAmount(new BigDecimal("1500.00"));

            int result = repository.update(toUpdate);
            assertThat(result).isEqualTo(1);

            Optional<MaterialConsumption> updated = repository.findById(toUpdate.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getRemarks()).isEqualTo("更新後の備考");
            assertThat(updated.get().getConsumptionAmount())
                    .isEqualByComparingTo(new BigDecimal("1500.00"));
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {
        @Test
        @DisplayName("古いバージョンで更新すると失敗する")
        void updateFailsWithOldVersion() {
            repository.save(createConsumption(true, new BigDecimal("1000.00")));

            List<MaterialConsumption> all = repository.findByWorkOrderNumber(workOrderNumber);
            MaterialConsumption userA = all.get(0);
            MaterialConsumption userB = repository.findById(userA.getId()).orElseThrow();

            userA.setRemarks("ユーザーAの更新");
            int resultA = repository.update(userA);
            assertThat(resultA).isEqualTo(1);

            userB.setRemarks("ユーザーBの更新");
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
            repository.save(createConsumption(true, new BigDecimal("1000.00")));
            List<MaterialConsumption> saved = repository.findByWorkOrderNumber(workOrderNumber);
            Integer id = saved.get(0).getId();

            repository.deleteById(id);

            assertThat(repository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("作業指示番号で削除できる")
        void canDeleteByWorkOrderNumber() {
            repository.save(createConsumption(true, new BigDecimal("1000.00")));
            repository.save(createConsumption(true, new BigDecimal("2000.00")));

            repository.deleteByWorkOrderNumber(workOrderNumber);

            assertThat(repository.findByWorkOrderNumber(workOrderNumber)).isEmpty();
        }
    }
}
