package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WorkOrderDetailRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderDetail;
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
 * 作業指示明細データリポジトリテスト.
 */
@DisplayName("作業指示明細データリポジトリ")
class WorkOrderDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private WorkOrderDetailRepository workOrderDetailRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String workOrderNumber;

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
        jdbcTemplate.execute("""
            INSERT INTO "工程マスタ" ("工程コード", "工程名")
            VALUES ('PROC001', 'プレス加工')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "工程マスタ" ("工程コード", "工程名")
            VALUES ('PROC002', '組立')
            ON CONFLICT DO NOTHING
            """);

        // Create work order
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
                .status(WorkOrderStatus.NOT_STARTED)
                .completedFlag(false)
                .build();
        workOrderRepository.save(workOrder);
        workOrderNumber = "WO-001";
    }

    private WorkOrderDetail createWorkOrderDetail(Integer sequence, String processCode) {
        return WorkOrderDetail.builder()
                .workOrderNumber(workOrderNumber)
                .sequence(sequence)
                .processCode(processCode)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("作業指示明細を登録できる")
        void canRegisterWorkOrderDetail() {
            // Arrange
            WorkOrderDetail detail = createWorkOrderDetail(1, "PROC001");

            // Act
            workOrderDetailRepository.save(detail);

            // Assert
            Optional<WorkOrderDetail> found = workOrderDetailRepository
                    .findByWorkOrderNumberAndSequence(workOrderNumber, 1);
            assertThat(found).isPresent();
            assertThat(found.get().getProcessCode()).isEqualTo("PROC001");
        }

        @Test
        @DisplayName("複数明細を登録できる")
        void canRegisterMultipleDetails() {
            // Arrange
            WorkOrderDetail detail1 = createWorkOrderDetail(1, "PROC001");
            WorkOrderDetail detail2 = createWorkOrderDetail(2, "PROC002");

            // Act
            workOrderDetailRepository.save(detail1);
            workOrderDetailRepository.save(detail2);

            // Assert
            List<WorkOrderDetail> details = workOrderDetailRepository
                    .findByWorkOrderNumber(workOrderNumber);
            assertThat(details).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            workOrderDetailRepository.save(createWorkOrderDetail(1, "PROC001"));
            workOrderDetailRepository.save(createWorkOrderDetail(2, "PROC002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<WorkOrderDetail> detail = workOrderDetailRepository
                    .findByWorkOrderNumberAndSequence(workOrderNumber, 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<WorkOrderDetail> found = workOrderDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getProcessCode()).isEqualTo("PROC001");
        }

        @Test
        @DisplayName("作業指示番号と工順で検索できる")
        void canFindByWorkOrderNumberAndSequence() {
            // Act
            Optional<WorkOrderDetail> found = workOrderDetailRepository
                    .findByWorkOrderNumberAndSequence(workOrderNumber, 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getProcessCode()).isEqualTo("PROC002");
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            // Act
            List<WorkOrderDetail> found = workOrderDetailRepository
                    .findByWorkOrderNumber(workOrderNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(d -> d.getWorkOrderNumber().equals(workOrderNumber));
        }

        @Test
        @DisplayName("存在しない作業指示番号で検索すると空リストを返す")
        void returnsEmptyListForNonExistentWorkOrderNumber() {
            // Act
            List<WorkOrderDetail> found = workOrderDetailRepository
                    .findByWorkOrderNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<WorkOrderDetail> all = workOrderDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
