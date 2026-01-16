package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ProcessInspectionRepository;
import com.example.pms.application.port.out.ProcessInspectionResultRepository;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ProcessInspection;
import com.example.pms.domain.model.quality.ProcessInspectionResult;
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
 * 工程検査結果データリポジトリテスト.
 */
@DisplayName("工程検査結果データリポジトリ")
class ProcessInspectionResultRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ProcessInspectionResultRepository processInspectionResultRepository;

    @Autowired
    private ProcessInspectionRepository processInspectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String inspectionNumber;

    @BeforeEach
    void setUp() {
        processInspectionResultRepository.deleteByInspectionNumber("PI-001");
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
        jdbcTemplate.execute("""
            INSERT INTO "欠点マスタ" ("欠点コード", "欠点名", "欠点分類")
            VALUES ('DEF001', 'キズ', '外観')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "欠点マスタ" ("欠点コード", "欠点名", "欠点分類")
            VALUES ('DEF002', '寸法不良', '寸法')
            ON CONFLICT DO NOTHING
            """);

        // Create process inspection
        ProcessInspection inspection = ProcessInspection.builder()
                .inspectionNumber("PI-001")
                .workOrderNumber("WO-001")
                .processCode("PROC001")
                .itemCode("ITEM001")
                .inspectionDate(LocalDate.of(2024, 1, 18))
                .inspectorCode("EMP001")
                .inspectionQuantity(new BigDecimal("50.00"))
                .passedQuantity(new BigDecimal("48.00"))
                .failedQuantity(new BigDecimal("2.00"))
                .judgment(InspectionJudgment.PASSED)
                .build();
        processInspectionRepository.save(inspection);
        inspectionNumber = "PI-001";
    }

    private ProcessInspectionResult createProcessInspectionResult(String defectCode, BigDecimal quantity) {
        return ProcessInspectionResult.builder()
                .inspectionNumber(inspectionNumber)
                .defectCode(defectCode)
                .quantity(quantity)
                .remarks("テスト検査結果")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("工程検査結果を登録できる")
        void canRegisterProcessInspectionResult() {
            ProcessInspectionResult result = createProcessInspectionResult("DEF001", new BigDecimal("1.00"));
            processInspectionResultRepository.save(result);

            List<ProcessInspectionResult> found = processInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getDefectCode()).isEqualTo("DEF001");
        }

        @Test
        @DisplayName("複数の工程検査結果を登録できる")
        void canRegisterMultipleResults() {
            processInspectionResultRepository.save(createProcessInspectionResult("DEF001", new BigDecimal("1.00")));
            processInspectionResultRepository.save(createProcessInspectionResult("DEF002", new BigDecimal("1.00")));

            List<ProcessInspectionResult> found = processInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            processInspectionResultRepository.save(createProcessInspectionResult("DEF001", new BigDecimal("1.00")));
            processInspectionResultRepository.save(createProcessInspectionResult("DEF002", new BigDecimal("1.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<ProcessInspectionResult> results = processInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(results).isNotEmpty();
            Integer id = results.get(0).getId();

            Optional<ProcessInspectionResult> found = processInspectionResultRepository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("検査番号で検索できる")
        void canFindByInspectionNumber() {
            List<ProcessInspectionResult> found = processInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(r -> inspectionNumber.equals(r.getInspectionNumber()));
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("検査番号で削除できる")
        void canDeleteByInspectionNumber() {
            processInspectionResultRepository.save(createProcessInspectionResult("DEF001", new BigDecimal("1.00")));
            processInspectionResultRepository.save(createProcessInspectionResult("DEF002", new BigDecimal("1.00")));

            processInspectionResultRepository.deleteByInspectionNumber(inspectionNumber);

            List<ProcessInspectionResult> found = processInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("IDで削除できる")
        void canDeleteById() {
            processInspectionResultRepository.save(createProcessInspectionResult("DEF001", new BigDecimal("1.00")));

            List<ProcessInspectionResult> results = processInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(results).hasSize(1);
            Integer id = results.get(0).getId();

            processInspectionResultRepository.deleteById(id);

            assertThat(processInspectionResultRepository.findById(id)).isEmpty();
        }
    }
}
