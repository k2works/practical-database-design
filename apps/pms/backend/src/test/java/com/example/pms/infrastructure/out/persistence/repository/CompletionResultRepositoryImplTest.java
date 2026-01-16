package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.CompletionResultRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.process.CompletionResult;
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
 * 完成実績データリポジトリテスト.
 */
@DisplayName("完成実績データリポジトリ")
class CompletionResultRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private CompletionResultRepository completionResultRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String workOrderNumber;

    @BeforeEach
    void setUp() {
        completionResultRepository.deleteAll();
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
    }

    private CompletionResult createCompletionResult(String completionResultNumber) {
        return CompletionResult.builder()
                .completionResultNumber(completionResultNumber)
                .workOrderNumber(workOrderNumber)
                .itemCode("ITEM001")
                .completionDate(LocalDate.of(2024, 1, 18))
                .completedQuantity(new BigDecimal("50.00"))
                .goodQuantity(new BigDecimal("48.00"))
                .defectQuantity(new BigDecimal("2.00"))
                .remarks("テスト完成実績")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("完成実績を登録できる")
        void canRegisterCompletionResult() {
            // Arrange
            CompletionResult result = createCompletionResult("CR-001");

            // Act
            completionResultRepository.save(result);

            // Assert
            Optional<CompletionResult> found = completionResultRepository
                    .findByCompletionResultNumber("CR-001");
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo(workOrderNumber);
            assertThat(found.get().getCompletedQuantity()).isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            completionResultRepository.save(createCompletionResult("CR-001"));
            completionResultRepository.save(createCompletionResult("CR-002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<CompletionResult> result = completionResultRepository
                    .findByCompletionResultNumber("CR-001");
            assertThat(result).isPresent();
            Integer id = result.get().getId();

            // Act
            Optional<CompletionResult> found = completionResultRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getCompletionResultNumber()).isEqualTo("CR-001");
        }

        @Test
        @DisplayName("完成実績番号で検索できる")
        void canFindByCompletionResultNumber() {
            // Act
            Optional<CompletionResult> found = completionResultRepository
                    .findByCompletionResultNumber("CR-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo(workOrderNumber);
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            // Act
            List<CompletionResult> found = completionResultRepository
                    .findByWorkOrderNumber(workOrderNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(cr -> cr.getWorkOrderNumber().equals(workOrderNumber));
        }

        @Test
        @DisplayName("存在しない完成実績番号で検索すると空を返す")
        void returnsEmptyForNonExistentCompletionResultNumber() {
            // Act
            Optional<CompletionResult> found = completionResultRepository
                    .findByCompletionResultNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<CompletionResult> all = completionResultRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
