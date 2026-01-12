package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.CostVarianceRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.cost.CostVariance;
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
 * 原価差異データリポジトリテスト.
 */
@DisplayName("原価差異データリポジトリ")
class CostVarianceRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private CostVarianceRepository repository;

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
            VALUES ('ITEM001', '2024-01-01', 'テスト品目A', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM002', '2024-01-01', 'テスト品目B', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード", "ステータス")
            VALUES ('ORD-001', '製造', 'ITEM001', '2024-01-15', '2024-01-25', 100, 'LOC001', '確定')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード", "ステータス")
            VALUES ('ORD-002', '製造', 'ITEM001', '2024-02-01', '2024-02-10', 50, 'LOC001', '確定')
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

        WorkOrder workOrder2 = WorkOrder.builder()
                .workOrderNumber("WO-002")
                .orderNumber("ORD-002")
                .workOrderDate(LocalDate.of(2024, 2, 1))
                .itemCode("ITEM001")
                .orderQuantity(new BigDecimal("50.00"))
                .locationCode("LOC001")
                .plannedStartDate(LocalDate.of(2024, 2, 2))
                .plannedEndDate(LocalDate.of(2024, 2, 5))
                .completedQuantity(BigDecimal.ZERO)
                .totalGoodQuantity(BigDecimal.ZERO)
                .totalDefectQuantity(BigDecimal.ZERO)
                .status(WorkOrderStatus.IN_PROGRESS)
                .completedFlag(false)
                .build();
        workOrderRepository.save(workOrder2);
    }

    private CostVariance createVariance(String woNumber, BigDecimal totalVariance) {
        return CostVariance.builder()
                .workOrderNumber(woNumber)
                .itemCode("ITEM001")
                .materialCostVariance(new BigDecimal("5000.00"))
                .laborCostVariance(new BigDecimal("3000.00"))
                .expenseVariance(new BigDecimal("2000.00"))
                .totalVariance(totalVariance)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("原価差異を登録できる")
        void canRegister() {
            CostVariance variance = createVariance(workOrderNumber, new BigDecimal("10000.00"));
            repository.save(variance);

            assertThat(variance.getId()).isNotNull();
            Optional<CostVariance> found = repository.findByWorkOrderNumber(workOrderNumber);
            assertThat(found).isPresent();
            assertThat(found.get().getTotalVariance())
                    .isEqualByComparingTo(new BigDecimal("10000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            repository.save(createVariance("WO-001", new BigDecimal("10000.00")));
            repository.save(createVariance("WO-002", new BigDecimal("-5000.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<CostVariance> all = repository.findAll();
            Integer id = all.get(0).getId();

            Optional<CostVariance> found = repository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            Optional<CostVariance> found = repository.findByWorkOrderNumber("WO-001");
            assertThat(found).isPresent();
            assertThat(found.get().getTotalVariance())
                    .isEqualByComparingTo(new BigDecimal("10000.00"));
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItemCode() {
            List<CostVariance> found = repository.findByItemCode("ITEM001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("作業指示番号の存在確認ができる")
        void canCheckExistence() {
            boolean exists = repository.existsByWorkOrderNumber("WO-001");
            assertThat(exists).isTrue();

            boolean notExists = repository.existsByWorkOrderNumber("WO-999");
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<CostVariance> all = repository.findAll();
            assertThat(all).hasSize(2);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("作業指示番号で削除できる")
        void canDeleteByWorkOrderNumber() {
            repository.save(createVariance("WO-001", new BigDecimal("10000.00")));

            repository.deleteByWorkOrderNumber("WO-001");

            assertThat(repository.findByWorkOrderNumber("WO-001")).isEmpty();
        }
    }

    @Nested
    @DisplayName("ドメインロジック")
    class DomainLogic {
        @Test
        @DisplayName("有利差異を判定できる")
        void canCheckFavorable() {
            CostVariance favorable = createVariance("WO-001", new BigDecimal("-5000.00"));
            assertThat(favorable.isFavorable()).isTrue();
            assertThat(favorable.isUnfavorable()).isFalse();
        }

        @Test
        @DisplayName("不利差異を判定できる")
        void canCheckUnfavorable() {
            CostVariance unfavorable = createVariance("WO-001", new BigDecimal("5000.00"));
            assertThat(unfavorable.isFavorable()).isFalse();
            assertThat(unfavorable.isUnfavorable()).isTrue();
        }

        @Test
        @DisplayName("差異ゼロを判定できる")
        void canCheckZeroVariance() {
            CostVariance zero = createVariance("WO-001", BigDecimal.ZERO);
            assertThat(zero.isFavorable()).isFalse();
            assertThat(zero.isUnfavorable()).isFalse();
        }
    }
}
