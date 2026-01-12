package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WorkOrderDetailRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
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
 * 作業指示データリポジトリテスト.
 */
@DisplayName("作業指示データリポジトリ")
class WorkOrderRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderDetailRepository workOrderDetailRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        workOrderDetailRepository.deleteAll();
        workOrderRepository.deleteAll();

        // Create required master data
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
    }

    private WorkOrder createWorkOrder(String workOrderNumber, WorkOrderStatus status) {
        return WorkOrder.builder()
                .workOrderNumber(workOrderNumber)
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
                .status(status)
                .completedFlag(false)
                .remarks("テスト作業指示")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("未着手の作業指示を登録できる")
        void canRegisterNotStartedWorkOrder() {
            // Arrange
            WorkOrder workOrder = createWorkOrder("WO-001", WorkOrderStatus.NOT_STARTED);

            // Act
            workOrderRepository.save(workOrder);

            // Assert
            Optional<WorkOrder> found = workOrderRepository.findByWorkOrderNumber("WO-001");
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(WorkOrderStatus.NOT_STARTED);
            assertThat(found.get().getOrderNumber()).isEqualTo("ORD-001");
        }

        @Test
        @DisplayName("作業中の作業指示を登録できる")
        void canRegisterInProgressWorkOrder() {
            // Arrange
            WorkOrder workOrder = createWorkOrder("WO-002", WorkOrderStatus.IN_PROGRESS);

            // Act
            workOrderRepository.save(workOrder);

            // Assert
            Optional<WorkOrder> found = workOrderRepository.findByWorkOrderNumber("WO-002");
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(WorkOrderStatus.IN_PROGRESS);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            workOrderRepository.save(createWorkOrder("WO-001", WorkOrderStatus.NOT_STARTED));
            workOrderRepository.save(createWorkOrder("WO-002", WorkOrderStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<WorkOrder> workOrder = workOrderRepository.findByWorkOrderNumber("WO-001");
            assertThat(workOrder).isPresent();
            Integer id = workOrder.get().getId();

            // Act
            Optional<WorkOrder> found = workOrderRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo("WO-001");
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            // Act
            Optional<WorkOrder> found = workOrderRepository.findByWorkOrderNumber("WO-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(WorkOrderStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("オーダ番号で検索できる")
        void canFindByOrderNumber() {
            // Act
            List<WorkOrder> found = workOrderRepository.findByOrderNumber("ORD-001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(wo -> "ORD-001".equals(wo.getOrderNumber()));
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            // Act
            List<WorkOrder> found = workOrderRepository.findByStatus(WorkOrderStatus.NOT_STARTED);

            // Assert
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getWorkOrderNumber()).isEqualTo("WO-001");
        }

        @Test
        @DisplayName("存在しない作業指示番号で検索すると空を返す")
        void returnsEmptyForNonExistentWorkOrderNumber() {
            // Act
            Optional<WorkOrder> found = workOrderRepository.findByWorkOrderNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<WorkOrder> all = workOrderRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
