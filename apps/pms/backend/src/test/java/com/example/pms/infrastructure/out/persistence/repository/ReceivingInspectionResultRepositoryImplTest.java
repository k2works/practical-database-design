package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ReceivingInspectionRepository;
import com.example.pms.application.port.out.ReceivingInspectionResultRepository;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ReceivingInspection;
import com.example.pms.domain.model.quality.ReceivingInspectionResult;
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
 * 受入検査結果データリポジトリテスト.
 */
@DisplayName("受入検査結果データリポジトリ")
class ReceivingInspectionResultRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ReceivingInspectionResultRepository receivingInspectionResultRepository;

    @Autowired
    private ReceivingInspectionRepository receivingInspectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String inspectionNumber;

    @BeforeEach
    void setUp() {
        receivingInspectionResultRepository.deleteByInspectionNumber("RI-001");
        receivingInspectionRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目', '材料')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "取引先マスタ" ("取引先コード", "適用開始日", "取引先名", "取引先区分")
            VALUES ('SUP001', '2024-01-01', 'テスト仕入先', '仕入先')
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
        // Create purchase order chain for foreign key constraints
        jdbcTemplate.execute("""
            INSERT INTO "発注データ" ("発注番号", "取引先コード", "発注日", "ステータス")
            VALUES ('PO-001', 'SUP001', '2024-01-10', '発注済')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "発注明細データ" ("発注番号", "発注行番号", "品目コード", "受入予定日", "発注数量", "発注単価", "発注金額")
            VALUES ('PO-001', 1, 'ITEM001', '2024-01-20', 100.00, 1000.00, 100000.00)
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "入荷受入データ" ("入荷番号", "発注番号", "発注行番号", "入荷日", "品目コード", "入荷数量")
            VALUES ('RCV-001', 'PO-001', 1, '2024-01-15', 'ITEM001', 100.00)
            ON CONFLICT DO NOTHING
            """);

        // Create receiving inspection
        ReceivingInspection inspection = ReceivingInspection.builder()
                .inspectionNumber("RI-001")
                .receivingNumber("RCV-001")
                .purchaseOrderNumber("PO-001")
                .itemCode("ITEM001")
                .supplierCode("SUP001")
                .inspectionDate(LocalDate.of(2024, 1, 15))
                .inspectorCode("EMP001")
                .inspectionQuantity(new BigDecimal("100.00"))
                .passedQuantity(new BigDecimal("98.00"))
                .failedQuantity(new BigDecimal("2.00"))
                .judgment(InspectionJudgment.PASSED)
                .build();
        receivingInspectionRepository.save(inspection);
        inspectionNumber = "RI-001";
    }

    private ReceivingInspectionResult createReceivingInspectionResult(String defectCode, BigDecimal quantity) {
        return ReceivingInspectionResult.builder()
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
        @DisplayName("受入検査結果を登録できる")
        void canRegisterReceivingInspectionResult() {
            ReceivingInspectionResult result = createReceivingInspectionResult("DEF001", new BigDecimal("1.00"));
            receivingInspectionResultRepository.save(result);

            List<ReceivingInspectionResult> found = receivingInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getDefectCode()).isEqualTo("DEF001");
        }

        @Test
        @DisplayName("複数の受入検査結果を登録できる")
        void canRegisterMultipleResults() {
            receivingInspectionResultRepository.save(createReceivingInspectionResult("DEF001", new BigDecimal("1.00")));
            receivingInspectionResultRepository.save(createReceivingInspectionResult("DEF002", new BigDecimal("1.00")));

            List<ReceivingInspectionResult> found = receivingInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            receivingInspectionResultRepository.save(createReceivingInspectionResult("DEF001", new BigDecimal("1.00")));
            receivingInspectionResultRepository.save(createReceivingInspectionResult("DEF002", new BigDecimal("1.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<ReceivingInspectionResult> results = receivingInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(results).isNotEmpty();
            Integer id = results.get(0).getId();

            Optional<ReceivingInspectionResult> found = receivingInspectionResultRepository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("検査番号で検索できる")
        void canFindByInspectionNumber() {
            List<ReceivingInspectionResult> found = receivingInspectionResultRepository
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
            receivingInspectionResultRepository.save(createReceivingInspectionResult("DEF001", new BigDecimal("1.00")));
            receivingInspectionResultRepository.save(createReceivingInspectionResult("DEF002", new BigDecimal("1.00")));

            receivingInspectionResultRepository.deleteByInspectionNumber(inspectionNumber);

            List<ReceivingInspectionResult> found = receivingInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).isEmpty();
        }
    }
}
