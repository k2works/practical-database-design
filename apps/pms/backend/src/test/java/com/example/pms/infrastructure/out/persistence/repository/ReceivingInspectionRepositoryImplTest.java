package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ReceivingInspectionRepository;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ReceivingInspection;
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
 * 受入検査データリポジトリテスト.
 */
@DisplayName("受入検査データリポジトリ")
class ReceivingInspectionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ReceivingInspectionRepository receivingInspectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
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
        jdbcTemplate.execute("""
            INSERT INTO "入荷受入データ" ("入荷番号", "発注番号", "発注行番号", "入荷日", "品目コード", "入荷数量")
            VALUES ('RCV-002', 'PO-001', 1, '2024-01-16', 'ITEM001', 100.00)
            ON CONFLICT DO NOTHING
            """);
    }

    private ReceivingInspection createReceivingInspection(String inspectionNumber, String receivingNumber) {
        return ReceivingInspection.builder()
                .inspectionNumber(inspectionNumber)
                .receivingNumber(receivingNumber)
                .purchaseOrderNumber("PO-001")
                .itemCode("ITEM001")
                .supplierCode("SUP001")
                .inspectionDate(LocalDate.of(2024, 1, 15))
                .inspectorCode("EMP001")
                .inspectionQuantity(new BigDecimal("100.00"))
                .passedQuantity(new BigDecimal("98.00"))
                .failedQuantity(new BigDecimal("2.00"))
                .judgment(InspectionJudgment.PASSED)
                .remarks("テスト受入検査")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("受入検査を登録できる")
        void canRegisterReceivingInspection() {
            ReceivingInspection inspection = createReceivingInspection("RI-001", "RCV-001");
            receivingInspectionRepository.save(inspection);

            Optional<ReceivingInspection> found = receivingInspectionRepository.findByInspectionNumber("RI-001");
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo("RCV-001");
            assertThat(found.get().getJudgment()).isEqualTo(InspectionJudgment.PASSED);
        }

        @Test
        @DisplayName("複数の受入検査を登録できる")
        void canRegisterMultipleReceivingInspections() {
            receivingInspectionRepository.save(createReceivingInspection("RI-001", "RCV-001"));
            receivingInspectionRepository.save(createReceivingInspection("RI-002", "RCV-001"));

            List<ReceivingInspection> found = receivingInspectionRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            receivingInspectionRepository.save(createReceivingInspection("RI-001", "RCV-001"));
            receivingInspectionRepository.save(createReceivingInspection("RI-002", "RCV-001"));
            receivingInspectionRepository.save(createReceivingInspection("RI-003", "RCV-002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            Optional<ReceivingInspection> result = receivingInspectionRepository.findByInspectionNumber("RI-001");
            assertThat(result).isPresent();
            Integer id = result.get().getId();

            Optional<ReceivingInspection> found = receivingInspectionRepository.findById(id);
            assertThat(found).isPresent();
            assertThat(found.get().getInspectionNumber()).isEqualTo("RI-001");
        }

        @Test
        @DisplayName("検査番号で検索できる")
        void canFindByInspectionNumber() {
            Optional<ReceivingInspection> found = receivingInspectionRepository.findByInspectionNumber("RI-002");
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo("RCV-001");
        }

        @Test
        @DisplayName("入庫番号で検索できる")
        void canFindByReceivingNumber() {
            List<ReceivingInspection> found = receivingInspectionRepository.findByReceivingNumber("RCV-001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("仕入先コードで検索できる")
        void canFindBySupplierCode() {
            List<ReceivingInspection> found = receivingInspectionRepository.findBySupplierCode("SUP001");
            assertThat(found).hasSize(3);
        }

        @Test
        @DisplayName("存在しない検査番号で検索すると空を返す")
        void returnsEmptyForNonExistent() {
            Optional<ReceivingInspection> found = receivingInspectionRepository.findByInspectionNumber("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<ReceivingInspection> found = receivingInspectionRepository.findAll();
            assertThat(found).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("受入検査を更新できる")
        void canUpdateReceivingInspection() {
            receivingInspectionRepository.save(createReceivingInspection("RI-001", "RCV-001"));

            Optional<ReceivingInspection> saved = receivingInspectionRepository.findByInspectionNumber("RI-001");
            assertThat(saved).isPresent();
            ReceivingInspection toUpdate = saved.get();
            toUpdate.setRemarks("更新後の備考");
            toUpdate.setPassedQuantity(new BigDecimal("95.00"));
            toUpdate.setFailedQuantity(new BigDecimal("5.00"));
            receivingInspectionRepository.update(toUpdate);

            Optional<ReceivingInspection> updated = receivingInspectionRepository.findByInspectionNumber("RI-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getRemarks()).isEqualTo("更新後の備考");
            assertThat(updated.get().getFailedQuantity()).isEqualByComparingTo(new BigDecimal("5.00"));
        }

        @Test
        @DisplayName("判定を更新できる")
        void canUpdateJudgment() {
            receivingInspectionRepository.save(createReceivingInspection("RI-001", "RCV-001"));

            Optional<ReceivingInspection> saved = receivingInspectionRepository.findByInspectionNumber("RI-001");
            assertThat(saved).isPresent();
            ReceivingInspection toUpdate = saved.get();
            toUpdate.setJudgment(InspectionJudgment.FAILED);
            receivingInspectionRepository.updateJudgment(toUpdate);

            Optional<ReceivingInspection> updated = receivingInspectionRepository.findByInspectionNumber("RI-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getJudgment()).isEqualTo(InspectionJudgment.FAILED);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("受入検査を削除できる")
        void canDeleteReceivingInspection() {
            receivingInspectionRepository.save(createReceivingInspection("RI-001", "RCV-001"));
            receivingInspectionRepository.save(createReceivingInspection("RI-002", "RCV-001"));

            receivingInspectionRepository.deleteByInspectionNumber("RI-001");

            assertThat(receivingInspectionRepository.findByInspectionNumber("RI-001")).isEmpty();
            assertThat(receivingInspectionRepository.findAll()).hasSize(1);
        }
    }
}
