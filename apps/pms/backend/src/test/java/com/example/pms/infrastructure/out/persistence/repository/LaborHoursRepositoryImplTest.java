package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LaborHoursRepository;
import com.example.pms.application.port.out.WorkOrderDetailRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.process.LaborHours;
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
 * 工数実績データリポジトリテスト.
 */
@DisplayName("工数実績データリポジトリ")
class LaborHoursRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private LaborHoursRepository laborHoursRepository;

    @Autowired
    private WorkOrderDetailRepository workOrderDetailRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String workOrderNumber;

    @BeforeEach
    void setUp() {
        laborHoursRepository.deleteAll();
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
            INSERT INTO "部門マスタ" ("部門コード", "部門名", "有効開始日")
            VALUES ('DEPT001', '製造部', '2024-01-01')
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
                .status(WorkOrderStatus.IN_PROGRESS)
                .completedFlag(false)
                .build();
        workOrderRepository.save(workOrder);
        workOrderNumber = "WO-001";

        // Create work order detail
        WorkOrderDetail detail = WorkOrderDetail.builder()
                .workOrderNumber(workOrderNumber)
                .sequence(1)
                .processCode("PROC001")
                .build();
        workOrderDetailRepository.save(detail);
    }

    private LaborHours createLaborHours(String laborHoursNumber, Integer sequence) {
        return LaborHours.builder()
                .laborHoursNumber(laborHoursNumber)
                .workOrderNumber(workOrderNumber)
                .itemCode("ITEM001")
                .sequence(sequence)
                .processCode("PROC001")
                .departmentCode("DEPT001")
                .employeeCode("EMP001")
                .workDate(LocalDate.of(2024, 1, 17))
                .hours(new BigDecimal("8.00"))
                .remarks("テスト工数実績")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("工数実績を登録できる")
        void canRegisterLaborHours() {
            // Arrange
            LaborHours laborHours = createLaborHours("LH-001", 1);

            // Act
            laborHoursRepository.save(laborHours);

            // Assert
            Optional<LaborHours> found = laborHoursRepository.findByLaborHoursNumber("LH-001");
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo(workOrderNumber);
            assertThat(found.get().getHours()).isEqualByComparingTo(new BigDecimal("8.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            laborHoursRepository.save(createLaborHours("LH-001", 1));
            laborHoursRepository.save(createLaborHours("LH-002", 1));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<LaborHours> laborHours = laborHoursRepository.findByLaborHoursNumber("LH-001");
            assertThat(laborHours).isPresent();
            Integer id = laborHours.get().getId();

            // Act
            Optional<LaborHours> found = laborHoursRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getLaborHoursNumber()).isEqualTo("LH-001");
        }

        @Test
        @DisplayName("工数実績番号で検索できる")
        void canFindByLaborHoursNumber() {
            // Act
            Optional<LaborHours> found = laborHoursRepository.findByLaborHoursNumber("LH-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo(workOrderNumber);
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            // Act
            List<LaborHours> found = laborHoursRepository.findByWorkOrderNumber(workOrderNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(lh -> lh.getWorkOrderNumber().equals(workOrderNumber));
        }

        @Test
        @DisplayName("作業指示番号と工順で検索できる")
        void canFindByWorkOrderNumberAndSequence() {
            // Act
            List<LaborHours> found = laborHoursRepository
                    .findByWorkOrderNumberAndSequence(workOrderNumber, 1);

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("存在しない工数実績番号で検索すると空を返す")
        void returnsEmptyForNonExistentLaborHoursNumber() {
            // Act
            Optional<LaborHours> found = laborHoursRepository.findByLaborHoursNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<LaborHours> all = laborHoursRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
