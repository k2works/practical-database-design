package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.BomRepository;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.bom.BomExplosion;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BOMリポジトリテスト.
 */
@DisplayName("BOMリポジトリ")
@SuppressWarnings("PMD.BigIntegerInstantiation")
class BomRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        bomRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private void setupTestItems() {
        // 製品: X
        itemRepository.save(Item.builder()
                .itemCode("X")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .itemName("製品X")
                .itemCategory(ItemCategory.PRODUCT)
                .build());
        // 中間品: n
        itemRepository.save(Item.builder()
                .itemCode("n")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .itemName("中間品n")
                .itemCategory(ItemCategory.INTERMEDIATE)
                .build());
        // 中間品: m
        itemRepository.save(Item.builder()
                .itemCode("m")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .itemName("中間品m")
                .itemCategory(ItemCategory.INTERMEDIATE)
                .build());
        // 部品: a, b, c, d
        for (String code : List.of("a", "b", "c", "d")) {
            itemRepository.save(Item.builder()
                    .itemCode(code)
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .itemName("部品" + code)
                    .itemCategory(ItemCategory.PART)
                    .build());
        }
    }

    private Bom createBom(String parent, String child, BigDecimal qty, int seq) {
        return Bom.builder()
                .parentItemCode(parent)
                .childItemCode(child)
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .requiredQuantity(qty)
                .sequence(seq)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @BeforeEach
        void setUpData() {
            setupTestItems();
        }

        @Test
        @DisplayName("BOMを登録できる")
        void canRegisterBom() {
            // Arrange
            Bom bom = createBom("X", "n", BigDecimal.ONE, 1);

            // Act
            bomRepository.save(bom);

            // Assert
            List<Bom> found = bomRepository.findByParentItemCode("X");
            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getChildItemCode()).isEqualTo("n");
        }

        @Test
        @DisplayName("複数のBOMを登録できる")
        void canRegisterMultipleBoms() {
            // Arrange & Act
            bomRepository.save(createBom("X", "n", BigDecimal.ONE, 1));
            bomRepository.save(createBom("X", "d", BigDecimal.ONE, 2));

            // Assert
            List<Bom> found = bomRepository.findByParentItemCode("X");
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            setupTestItems();
            // X -> n, d
            bomRepository.save(createBom("X", "n", BigDecimal.ONE, 1));
            bomRepository.save(createBom("X", "d", BigDecimal.ONE, 2));
            // n -> m, b, c
            bomRepository.save(createBom("n", "m", BigDecimal.ONE, 1));
            bomRepository.save(createBom("n", "b", BigDecimal.ONE, 2));
            bomRepository.save(createBom("n", "c", new BigDecimal("2"), 3));
            // m -> a
            bomRepository.save(createBom("m", "a", BigDecimal.ONE, 1));
        }

        @Test
        @DisplayName("親品目コードで検索できる")
        void canFindByParentItemCode() {
            // Act
            List<Bom> found = bomRepository.findByParentItemCode("X");

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("子品目コードで逆展開検索できる")
        void canFindByChildItemCode() {
            // Act
            List<Bom> found = bomRepository.findByChildItemCode("n");

            // Assert
            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getParentItemCode()).isEqualTo("X");
        }

        @Test
        @DisplayName("日付を指定してBOMを検索できる")
        void canFindByParentItemCodeAndDate() {
            // Arrange
            bomRepository.deleteAll();
            bomRepository.save(Bom.builder()
                    .parentItemCode("X")
                    .childItemCode("n")
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(LocalDate.of(2024, 6, 1))
                    .requiredQuantity(BigDecimal.ONE)
                    .sequence(1)
                    .build());
            bomRepository.save(Bom.builder()
                    .parentItemCode("X")
                    .childItemCode("n")
                    .effectiveFrom(LocalDate.of(2024, 6, 1))
                    .requiredQuantity(new BigDecimal("2"))
                    .sequence(1)
                    .build());

            // Act
            List<Bom> foundBefore = bomRepository.findByParentItemCodeAndDate("X", LocalDate.of(2024, 3, 1));
            List<Bom> foundAfter = bomRepository.findByParentItemCodeAndDate("X", LocalDate.of(2024, 7, 1));

            // Assert
            assertThat(foundBefore).hasSize(1);
            assertThat(foundBefore.getFirst().getRequiredQuantity()).isEqualByComparingTo(BigDecimal.ONE);
            assertThat(foundAfter).hasSize(1);
            assertThat(foundAfter.getFirst().getRequiredQuantity()).isEqualByComparingTo(new BigDecimal("2"));
        }
    }

    @Nested
    @DisplayName("BOM展開")
    class Explosion {

        @BeforeEach
        void setUpData() {
            setupTestItems();
            // BOM構造: X -> n(1) -> m(1) -> a(1)
            //                   -> b(1)
            //                   -> c(2)
            //              -> d(1)
            bomRepository.save(createBom("X", "n", BigDecimal.ONE, 1));
            bomRepository.save(createBom("X", "d", BigDecimal.ONE, 2));
            bomRepository.save(createBom("n", "m", BigDecimal.ONE, 1));
            bomRepository.save(createBom("n", "b", BigDecimal.ONE, 2));
            bomRepository.save(createBom("n", "c", new BigDecimal("2"), 3));
            bomRepository.save(createBom("m", "a", BigDecimal.ONE, 1));
        }

        @Test
        @DisplayName("BOMを展開して全階層を取得できる")
        void canExplodeBom() {
            // Act
            List<BomExplosion> explosions = bomRepository.explode("X", new BigDecimal("10"));

            // Assert
            assertThat(explosions).isNotEmpty();

            // レベル1: n, d
            List<BomExplosion> level1 = explosions.stream()
                    .filter(e -> e.getLevel() == 1)
                    .toList();
            assertThat(level1).hasSize(2);

            // レベル2: m, b, c (nの子)
            List<BomExplosion> level2 = explosions.stream()
                    .filter(e -> e.getLevel() == 2)
                    .toList();
            assertThat(level2).hasSize(3);

            // レベル3: a (mの子)
            List<BomExplosion> level3 = explosions.stream()
                    .filter(e -> e.getLevel() == 3)
                    .toList();
            assertThat(level3).hasSize(1);
        }

        @Test
        @DisplayName("累計数量が正しく計算される")
        void calculatesTotalQuantityCorrectly() {
            // Act
            List<BomExplosion> explosions = bomRepository.explode("X", new BigDecimal("10"));

            // Assert: cの累計数量を確認 (10 * 1 * 2 = 20)
            BomExplosion cExplosion = explosions.stream()
                    .filter(e -> "c".equals(e.getChildItemCode()))
                    .findFirst()
                    .orElseThrow();
            assertThat(cExplosion.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("20"));

            // Assert: aの累計数量を確認 (10 * 1 * 1 * 1 = 10)
            BomExplosion aExplosion = explosions.stream()
                    .filter(e -> "a".equals(e.getChildItemCode()))
                    .findFirst()
                    .orElseThrow();
            assertThat(aExplosion.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("10"));
        }
    }
}
