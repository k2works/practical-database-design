package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ShipmentInspectionRepository;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ShipmentInspection;
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
 * 出荷検査データリポジトリテスト.
 */
@DisplayName("出荷検査データリポジトリ")
class ShipmentInspectionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ShipmentInspectionRepository shipmentInspectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        shipmentInspectionRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目', '製品')
            ON CONFLICT DO NOTHING
            """);
    }

    private ShipmentInspection createShipmentInspection(String inspectionNumber, String shipmentNumber) {
        return ShipmentInspection.builder()
                .inspectionNumber(inspectionNumber)
                .shipmentNumber(shipmentNumber)
                .itemCode("ITEM001")
                .inspectionDate(LocalDate.of(2024, 1, 20))
                .inspectorCode("EMP001")
                .inspectionQuantity(new BigDecimal("100.00"))
                .passedQuantity(new BigDecimal("99.00"))
                .failedQuantity(new BigDecimal("1.00"))
                .judgment(InspectionJudgment.PASSED)
                .remarks("テスト出荷検査")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("出荷検査を登録できる")
        void canRegisterShipmentInspection() {
            ShipmentInspection inspection = createShipmentInspection("SI-001", "SHP-001");
            shipmentInspectionRepository.save(inspection);

            Optional<ShipmentInspection> found = shipmentInspectionRepository.findByInspectionNumber("SI-001");
            assertThat(found).isPresent();
            assertThat(found.get().getShipmentNumber()).isEqualTo("SHP-001");
            assertThat(found.get().getJudgment()).isEqualTo(InspectionJudgment.PASSED);
        }

        @Test
        @DisplayName("複数の出荷検査を登録できる")
        void canRegisterMultipleShipmentInspections() {
            shipmentInspectionRepository.save(createShipmentInspection("SI-001", "SHP-001"));
            shipmentInspectionRepository.save(createShipmentInspection("SI-002", "SHP-001"));

            List<ShipmentInspection> found = shipmentInspectionRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            shipmentInspectionRepository.save(createShipmentInspection("SI-001", "SHP-001"));
            shipmentInspectionRepository.save(createShipmentInspection("SI-002", "SHP-001"));
            shipmentInspectionRepository.save(createShipmentInspection("SI-003", "SHP-002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            Optional<ShipmentInspection> result = shipmentInspectionRepository.findByInspectionNumber("SI-001");
            assertThat(result).isPresent();
            Integer id = result.get().getId();

            Optional<ShipmentInspection> found = shipmentInspectionRepository.findById(id);
            assertThat(found).isPresent();
            assertThat(found.get().getInspectionNumber()).isEqualTo("SI-001");
        }

        @Test
        @DisplayName("検査番号で検索できる")
        void canFindByInspectionNumber() {
            Optional<ShipmentInspection> found = shipmentInspectionRepository.findByInspectionNumber("SI-002");
            assertThat(found).isPresent();
            assertThat(found.get().getShipmentNumber()).isEqualTo("SHP-001");
        }

        @Test
        @DisplayName("出荷番号で検索できる")
        void canFindByShipmentNumber() {
            List<ShipmentInspection> found = shipmentInspectionRepository.findByShipmentNumber("SHP-001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("存在しない検査番号で検索すると空を返す")
        void returnsEmptyForNonExistent() {
            Optional<ShipmentInspection> found = shipmentInspectionRepository.findByInspectionNumber("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<ShipmentInspection> found = shipmentInspectionRepository.findAll();
            assertThat(found).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("出荷検査を更新できる")
        void canUpdateShipmentInspection() {
            shipmentInspectionRepository.save(createShipmentInspection("SI-001", "SHP-001"));

            Optional<ShipmentInspection> saved = shipmentInspectionRepository.findByInspectionNumber("SI-001");
            assertThat(saved).isPresent();
            ShipmentInspection toUpdate = saved.get();
            toUpdate.setRemarks("更新後の備考");
            toUpdate.setJudgment(InspectionJudgment.HOLD);
            shipmentInspectionRepository.update(toUpdate);

            Optional<ShipmentInspection> updated = shipmentInspectionRepository.findByInspectionNumber("SI-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getRemarks()).isEqualTo("更新後の備考");
            assertThat(updated.get().getJudgment()).isEqualTo(InspectionJudgment.HOLD);
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("出荷検査を削除できる")
        void canDeleteShipmentInspection() {
            shipmentInspectionRepository.save(createShipmentInspection("SI-001", "SHP-001"));
            shipmentInspectionRepository.save(createShipmentInspection("SI-002", "SHP-001"));

            shipmentInspectionRepository.deleteByInspectionNumber("SI-001");

            assertThat(shipmentInspectionRepository.findByInspectionNumber("SI-001")).isEmpty();
            assertThat(shipmentInspectionRepository.findAll()).hasSize(1);
        }
    }
}
