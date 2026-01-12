package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.OverheadAllocationRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.cost.OverheadAllocation;
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
 * 製造間接費配賦データリポジトリテスト.
 */
@DisplayName("製造間接費配賦データリポジトリ")
class OverheadAllocationRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private OverheadAllocationRepository repository;

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

    private OverheadAllocation createAllocation(String period, BigDecimal amount) {
        return OverheadAllocation.builder()
                .workOrderNumber(workOrderNumber)
                .accountingPeriod(period)
                .allocationBasis("DIRECT_LABOR")
                .basisAmount(new BigDecimal("100000.00"))
                .allocationRate(new BigDecimal("0.15"))
                .allocatedAmount(amount)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("配賦データを登録できる")
        void canRegister() {
            OverheadAllocation allocation = createAllocation("2024-01",
                    new BigDecimal("15000.00"));
            repository.save(allocation);

            assertThat(allocation.getId()).isNotNull();
            List<OverheadAllocation> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getAllocatedAmount())
                    .isEqualByComparingTo(new BigDecimal("15000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createAllocation("2024-01", new BigDecimal("15000.00")));
            repository.save(createAllocation("2024-02", new BigDecimal("18000.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<OverheadAllocation> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<OverheadAllocation> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            List<OverheadAllocation> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("作業指示番号と会計期間で検索できる")
        void canFindByWorkOrderNumberAndPeriod() {
            Optional<OverheadAllocation> found = repository
                    .findByWorkOrderNumberAndPeriod(workOrderNumber, "2024-01");

            assertThat(found).isPresent();
            assertThat(found.get().getAllocatedAmount())
                    .isEqualByComparingTo(new BigDecimal("15000.00"));
        }

        @Test
        @DisplayName("作業指示番号の配賦合計を取得できる")
        void canSumByWorkOrderNumber() {
            BigDecimal sum = repository.sumByWorkOrderNumber(workOrderNumber);
            assertThat(sum).isEqualByComparingTo(new BigDecimal("33000.00"));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<OverheadAllocation> all = repository.findAll();
            assertThat(all).hasSize(2);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("IDで削除できる")
        void canDeleteById() {
            repository.save(createAllocation("2024-01", new BigDecimal("15000.00")));
            List<OverheadAllocation> saved = repository.findByWorkOrderNumber(workOrderNumber);
            Integer id = saved.get(0).getId();

            repository.deleteById(id);

            assertThat(repository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("作業指示番号で削除できる")
        void canDeleteByWorkOrderNumber() {
            repository.save(createAllocation("2024-01", new BigDecimal("15000.00")));
            repository.save(createAllocation("2024-02", new BigDecimal("18000.00")));

            repository.deleteByWorkOrderNumber(workOrderNumber);

            assertThat(repository.findByWorkOrderNumber(workOrderNumber)).isEmpty();
        }
    }
}
