package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ShipmentInspectionRepository;
import com.example.pms.application.port.out.ShipmentInspectionResultRepository;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ShipmentInspection;
import com.example.pms.domain.model.quality.ShipmentInspectionResult;
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
 * 出荷検査結果データリポジトリテスト.
 */
@DisplayName("出荷検査結果データリポジトリ")
class ShipmentInspectionResultRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ShipmentInspectionResultRepository shipmentInspectionResultRepository;

    @Autowired
    private ShipmentInspectionRepository shipmentInspectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String inspectionNumber;

    @BeforeEach
    void setUp() {
        shipmentInspectionResultRepository.deleteByInspectionNumber("SI-001");
        shipmentInspectionRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目', '製品')
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

        // Create shipment inspection
        ShipmentInspection inspection = ShipmentInspection.builder()
                .inspectionNumber("SI-001")
                .shipmentNumber("SHP-001")
                .itemCode("ITEM001")
                .inspectionDate(LocalDate.of(2024, 1, 20))
                .inspectorCode("EMP001")
                .inspectionQuantity(new BigDecimal("100.00"))
                .passedQuantity(new BigDecimal("99.00"))
                .failedQuantity(new BigDecimal("1.00"))
                .judgment(InspectionJudgment.PASSED)
                .build();
        shipmentInspectionRepository.save(inspection);
        inspectionNumber = "SI-001";
    }

    private ShipmentInspectionResult createShipmentInspectionResult(String defectCode, BigDecimal quantity) {
        return ShipmentInspectionResult.builder()
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
        @DisplayName("出荷検査結果を登録できる")
        void canRegisterShipmentInspectionResult() {
            ShipmentInspectionResult result = createShipmentInspectionResult("DEF001", new BigDecimal("1.00"));
            shipmentInspectionResultRepository.save(result);

            List<ShipmentInspectionResult> found = shipmentInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getDefectCode()).isEqualTo("DEF001");
        }

        @Test
        @DisplayName("複数の出荷検査結果を登録できる")
        void canRegisterMultipleResults() {
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF001", new BigDecimal("1.00")));
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF002", new BigDecimal("1.00")));

            List<ShipmentInspectionResult> found = shipmentInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF001", new BigDecimal("1.00")));
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF002", new BigDecimal("1.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<ShipmentInspectionResult> results = shipmentInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(results).isNotEmpty();
            Integer id = results.get(0).getId();

            Optional<ShipmentInspectionResult> found = shipmentInspectionResultRepository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("検査番号で検索できる")
        void canFindByInspectionNumber() {
            List<ShipmentInspectionResult> found = shipmentInspectionResultRepository
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
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF001", new BigDecimal("1.00")));
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF002", new BigDecimal("1.00")));

            shipmentInspectionResultRepository.deleteByInspectionNumber(inspectionNumber);

            List<ShipmentInspectionResult> found = shipmentInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("IDで削除できる")
        void canDeleteById() {
            shipmentInspectionResultRepository.save(createShipmentInspectionResult("DEF001", new BigDecimal("1.00")));

            List<ShipmentInspectionResult> results = shipmentInspectionResultRepository
                    .findByInspectionNumber(inspectionNumber);
            assertThat(results).hasSize(1);
            Integer id = results.get(0).getId();

            shipmentInspectionResultRepository.deleteById(id);

            assertThat(shipmentInspectionResultRepository.findById(id)).isEmpty();
        }
    }
}
