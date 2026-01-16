package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.UnitRepository;
import com.example.pms.domain.model.unit.Unit;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 単位リポジトリテスト.
 */
@DisplayName("単位リポジトリ")
class UnitRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        unitRepository.deleteAll();
    }

    private Unit createUnit(String code, String symbol, String name) {
        return Unit.builder()
                .unitCode(code)
                .unitSymbol(symbol)
                .unitName(name)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("単位を登録できる")
        void canRegisterUnit() {
            // Arrange
            Unit unit = createUnit("PCS", "個", "個数");

            // Act
            unitRepository.save(unit);

            // Assert
            Optional<Unit> found = unitRepository.findByUnitCode("PCS");
            assertThat(found).isPresent();
            assertThat(found.get().getUnitSymbol()).isEqualTo("個");
            assertThat(found.get().getUnitName()).isEqualTo("個数");
        }

        @Test
        @DisplayName("複数の単位を登録できる")
        void canRegisterMultipleUnits() {
            // Arrange & Act
            unitRepository.save(createUnit("PCS", "個", "個数"));
            unitRepository.save(createUnit("KG", "kg", "キログラム"));
            unitRepository.save(createUnit("M", "m", "メートル"));

            // Assert
            List<Unit> units = unitRepository.findAll();
            assertThat(units).hasSize(3);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            unitRepository.save(createUnit("PCS", "個", "個数"));
            unitRepository.save(createUnit("KG", "kg", "キログラム"));
            unitRepository.save(createUnit("M", "m", "メートル"));
            unitRepository.save(createUnit("L", "L", "リットル"));
        }

        @Test
        @DisplayName("単位コードで検索できる")
        void canFindByUnitCode() {
            // Act
            Optional<Unit> found = unitRepository.findByUnitCode("KG");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getUnitName()).isEqualTo("キログラム");
        }

        @Test
        @DisplayName("存在しないコードで検索すると空を返す")
        void returnsEmptyForNonExistentCode() {
            // Act
            Optional<Unit> found = unitRepository.findByUnitCode("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Unit> units = unitRepository.findAll();

            // Assert
            assertThat(units).hasSize(4);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("単位を更新できる")
        void canUpdateUnit() {
            // Arrange
            Unit unit = createUnit("PCS", "個", "個数");
            unitRepository.save(unit);

            // Act
            Optional<Unit> saved = unitRepository.findByUnitCode("PCS");
            assertThat(saved).isPresent();
            Unit toUpdate = saved.get();
            toUpdate.setUnitSymbol("pcs");
            toUpdate.setUnitName("ピース");
            unitRepository.update(toUpdate);

            // Assert
            Optional<Unit> updated = unitRepository.findByUnitCode("PCS");
            assertThat(updated).isPresent();
            assertThat(updated.get().getUnitSymbol()).isEqualTo("pcs");
            assertThat(updated.get().getUnitName()).isEqualTo("ピース");
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {

        @Test
        @DisplayName("単位を削除できる")
        void canDeleteUnit() {
            // Arrange
            unitRepository.save(createUnit("PCS", "個", "個数"));
            unitRepository.save(createUnit("KG", "kg", "キログラム"));

            // Act
            unitRepository.deleteByUnitCode("PCS");

            // Assert
            assertThat(unitRepository.findByUnitCode("PCS")).isEmpty();
            assertThat(unitRepository.findAll()).hasSize(1);
        }
    }
}
