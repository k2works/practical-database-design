package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LotCompositionRepository;
import com.example.pms.application.port.out.LotMasterRepository;
import com.example.pms.domain.model.quality.LotComposition;
import com.example.pms.domain.model.quality.LotMaster;
import com.example.pms.domain.model.quality.LotType;
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
 * ロット構成リポジトリテスト.
 */
@DisplayName("ロット構成リポジトリ")
class LotCompositionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private LotCompositionRepository lotCompositionRepository;

    @Autowired
    private LotMasterRepository lotMasterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String parentLotNumber;
    private String childLotNumber1;
    private String childLotNumber2;

    @BeforeEach
    void setUp() {
        lotCompositionRepository.deleteByParentLotNumber("LOT-PARENT");
        lotCompositionRepository.deleteByParentLotNumber("LOT-PARENT2");
        lotMasterRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト材料', '材料')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM002', '2024-01-01', 'テスト製品', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "倉庫マスタ" ("倉庫コード", "倉庫区分", "倉庫名")
            VALUES ('WH001', '製品倉庫', 'テスト倉庫')
            ON CONFLICT DO NOTHING
            """);

        // Create parent lot (production)
        LotMaster parentLot = LotMaster.builder()
                .lotNumber("LOT-PARENT")
                .itemCode("ITEM002")
                .lotType(LotType.MANUFACTURED)
                .manufactureDate(LocalDate.of(2024, 1, 20))
                .quantity(new BigDecimal("100.00"))
                .warehouseCode("WH001")
                .build();
        lotMasterRepository.save(parentLot);
        parentLotNumber = "LOT-PARENT";

        // Create child lots (receiving)
        LotMaster childLot1 = LotMaster.builder()
                .lotNumber("LOT-CHILD1")
                .itemCode("ITEM001")
                .lotType(LotType.PURCHASED)
                .manufactureDate(LocalDate.of(2024, 1, 10))
                .quantity(new BigDecimal("50.00"))
                .warehouseCode("WH001")
                .build();
        lotMasterRepository.save(childLot1);
        childLotNumber1 = "LOT-CHILD1";

        LotMaster childLot2 = LotMaster.builder()
                .lotNumber("LOT-CHILD2")
                .itemCode("ITEM001")
                .lotType(LotType.PURCHASED)
                .manufactureDate(LocalDate.of(2024, 1, 12))
                .quantity(new BigDecimal("50.00"))
                .warehouseCode("WH001")
                .build();
        lotMasterRepository.save(childLot2);
        childLotNumber2 = "LOT-CHILD2";
    }

    private LotComposition createLotComposition(String parentLotNum, String childLotNum, BigDecimal usedQuantity) {
        return LotComposition.builder()
                .parentLotNumber(parentLotNum)
                .childLotNumber(childLotNum)
                .usedQuantity(usedQuantity)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("ロット構成を登録できる")
        void canRegisterLotComposition() {
            LotComposition composition = createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00"));
            lotCompositionRepository.save(composition);

            List<LotComposition> found = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getChildLotNumber()).isEqualTo(childLotNumber1);
            assertThat(found.get(0).getUsedQuantity()).isEqualByComparingTo(new BigDecimal("30.00"));
        }

        @Test
        @DisplayName("複数のロット構成を登録できる")
        void canRegisterMultipleCompositions() {
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00")));
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber2, new BigDecimal("30.00")));

            List<LotComposition> found = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00")));
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber2, new BigDecimal("20.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            List<LotComposition> results = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(results).isNotEmpty();
            Integer id = results.get(0).getId();

            Optional<LotComposition> found = lotCompositionRepository.findById(id);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("親ロット番号で検索できる")
        void canFindByParentLotNumber() {
            List<LotComposition> found = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(c -> parentLotNumber.equals(c.getParentLotNumber()));
        }

        @Test
        @DisplayName("子ロット番号で検索できる")
        void canFindByChildLotNumber() {
            List<LotComposition> found = lotCompositionRepository.findByChildLotNumber(childLotNumber1);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getParentLotNumber()).isEqualTo(parentLotNumber);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<LotComposition> found = lotCompositionRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("ロット構成を更新できる")
        void canUpdateLotComposition() {
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00")));

            List<LotComposition> found = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(found).hasSize(1);
            LotComposition toUpdate = found.get(0);
            toUpdate.setUsedQuantity(new BigDecimal("40.00"));
            lotCompositionRepository.update(toUpdate);

            List<LotComposition> updated = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(updated).hasSize(1);
            assertThat(updated.get(0).getUsedQuantity()).isEqualByComparingTo(new BigDecimal("40.00"));
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("IDで削除できる")
        void canDeleteById() {
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00")));

            List<LotComposition> found = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(found).hasSize(1);
            Integer id = found.get(0).getId();

            lotCompositionRepository.deleteById(id);

            assertThat(lotCompositionRepository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("親ロット番号で削除できる")
        void canDeleteByParentLotNumber() {
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00")));
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber2, new BigDecimal("20.00")));

            lotCompositionRepository.deleteByParentLotNumber(parentLotNumber);

            List<LotComposition> found = lotCompositionRepository.findByParentLotNumber(parentLotNumber);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("子ロット番号で削除できる")
        void canDeleteByChildLotNumber() {
            lotCompositionRepository.save(createLotComposition(parentLotNumber, childLotNumber1, new BigDecimal("30.00")));

            lotCompositionRepository.deleteByChildLotNumber(childLotNumber1);

            List<LotComposition> found = lotCompositionRepository.findByChildLotNumber(childLotNumber1);
            assertThat(found).isEmpty();
        }
    }
}
