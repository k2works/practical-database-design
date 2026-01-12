package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ProcessInspectionRepository;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ProcessInspection;
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
 * 工程検査データリポジトリテスト.
 */
@DisplayName("工程検査データリポジトリ")
class ProcessInspectionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ProcessInspectionRepository processInspectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        processInspectionRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC001', 'テスト場所', '製造')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード", "ステータス")
            VALUES ('ORD-001', '製造', 'ITEM001', '2024-01-15', '2024-01-25', 100, 'LOC001', '確定')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "作業指示データ" ("作業指示番号", "オーダ番号", "作業指示日", "品目コード", "作業指示数", "場所コード", "開始予定日", "完成予定日", "完成済数", "総良品数", "総不良品数", "ステータス", "完了フラグ")
            VALUES ('WO-001', 'ORD-001', '2024-01-15', 'ITEM001', 100.00, 'LOC001', '2024-01-16', '2024-01-20', 0.00, 0.00, 0.00, '作業中', false)
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "工程マスタ" ("工程コード", "工程名")
            VALUES ('PROC001', 'テスト工程')
            ON CONFLICT DO NOTHING
            """);
    }

    private ProcessInspection createProcessInspection(String inspectionNumber, String workOrderNumber) {
        return ProcessInspection.builder()
                .inspectionNumber(inspectionNumber)
                .workOrderNumber(workOrderNumber)
                .processCode("PROC001")
                .itemCode("ITEM001")
                .inspectionDate(LocalDate.of(2024, 1, 18))
                .inspectorCode("EMP001")
                .inspectionQuantity(new BigDecimal("50.00"))
                .passedQuantity(new BigDecimal("48.00"))
                .failedQuantity(new BigDecimal("2.00"))
                .judgment(InspectionJudgment.PASSED)
                .remarks("テスト工程検査")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("工程検査を登録できる")
        void canRegisterProcessInspection() {
            ProcessInspection inspection = createProcessInspection("PI-001", "WO-001");
            processInspectionRepository.save(inspection);

            Optional<ProcessInspection> found = processInspectionRepository.findByInspectionNumber("PI-001");
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo("WO-001");
            assertThat(found.get().getJudgment()).isEqualTo(InspectionJudgment.PASSED);
        }

        @Test
        @DisplayName("複数の工程検査を登録できる")
        void canRegisterMultipleProcessInspections() {
            processInspectionRepository.save(createProcessInspection("PI-001", "WO-001"));
            processInspectionRepository.save(createProcessInspection("PI-002", "WO-001"));

            List<ProcessInspection> found = processInspectionRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            processInspectionRepository.save(createProcessInspection("PI-001", "WO-001"));
            processInspectionRepository.save(createProcessInspection("PI-002", "WO-001"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            Optional<ProcessInspection> result = processInspectionRepository.findByInspectionNumber("PI-001");
            assertThat(result).isPresent();
            Integer id = result.get().getId();

            Optional<ProcessInspection> found = processInspectionRepository.findById(id);
            assertThat(found).isPresent();
            assertThat(found.get().getInspectionNumber()).isEqualTo("PI-001");
        }

        @Test
        @DisplayName("検査番号で検索できる")
        void canFindByInspectionNumber() {
            Optional<ProcessInspection> found = processInspectionRepository.findByInspectionNumber("PI-002");
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo("WO-001");
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            List<ProcessInspection> found = processInspectionRepository.findByWorkOrderNumber("WO-001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("工程コードで検索できる")
        void canFindByProcessCode() {
            List<ProcessInspection> found = processInspectionRepository.findByProcessCode("PROC001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("存在しない検査番号で検索すると空を返す")
        void returnsEmptyForNonExistent() {
            Optional<ProcessInspection> found = processInspectionRepository.findByInspectionNumber("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<ProcessInspection> found = processInspectionRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("工程検査を更新できる")
        void canUpdateProcessInspection() {
            processInspectionRepository.save(createProcessInspection("PI-001", "WO-001"));

            Optional<ProcessInspection> saved = processInspectionRepository.findByInspectionNumber("PI-001");
            assertThat(saved).isPresent();
            ProcessInspection toUpdate = saved.get();
            toUpdate.setRemarks("更新後の備考");
            toUpdate.setJudgment(InspectionJudgment.FAILED);
            processInspectionRepository.update(toUpdate);

            Optional<ProcessInspection> updated = processInspectionRepository.findByInspectionNumber("PI-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getRemarks()).isEqualTo("更新後の備考");
            assertThat(updated.get().getJudgment()).isEqualTo(InspectionJudgment.FAILED);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("工程検査を削除できる")
        void canDeleteProcessInspection() {
            processInspectionRepository.save(createProcessInspection("PI-001", "WO-001"));
            processInspectionRepository.save(createProcessInspection("PI-002", "WO-001"));

            processInspectionRepository.deleteByInspectionNumber("PI-001");

            assertThat(processInspectionRepository.findByInspectionNumber("PI-001")).isEmpty();
            assertThat(processInspectionRepository.findAll()).hasSize(1);
        }
    }
}
