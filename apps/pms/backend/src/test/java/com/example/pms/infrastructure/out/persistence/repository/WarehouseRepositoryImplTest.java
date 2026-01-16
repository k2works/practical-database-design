package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WarehouseRepository;
import com.example.pms.domain.model.inventory.Warehouse;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 倉庫マスタリポジトリテスト.
 */
@DisplayName("倉庫マスタリポジトリ")
class WarehouseRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        warehouseRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "部門マスタ" ("部門コード", "部門名", "有効開始日")
            VALUES ('DEPT001', 'テスト部門1', '2024-01-01')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "部門マスタ" ("部門コード", "部門名", "有効開始日")
            VALUES ('DEPT002', 'テスト部門2', '2024-01-01')
            ON CONFLICT DO NOTHING
            """);
    }

    private Warehouse createWarehouse(String warehouseCode, String warehouseType,
                                       String warehouseName, String departmentCode) {
        return Warehouse.builder()
                .warehouseCode(warehouseCode)
                .warehouseType(warehouseType)
                .warehouseName(warehouseName)
                .departmentCode(departmentCode)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("倉庫を登録できる")
        void canRegisterWarehouse() {
            // Arrange
            Warehouse warehouse = createWarehouse("WH001", "製品倉庫", "製品倉庫1", "DEPT001");

            // Act
            warehouseRepository.save(warehouse);

            // Assert
            Optional<Warehouse> found = warehouseRepository.findByWarehouseCode("WH001");
            assertThat(found).isPresent();
            assertThat(found.get().getWarehouseName()).isEqualTo("製品倉庫1");
            assertThat(found.get().getWarehouseType()).isEqualTo("製品倉庫");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            warehouseRepository.save(createWarehouse("WH001", "製品倉庫", "製品倉庫1", "DEPT001"));
            warehouseRepository.save(createWarehouse("WH002", "材料倉庫", "材料倉庫1", "DEPT001"));
            warehouseRepository.save(createWarehouse("WH003", "出荷倉庫", "出荷倉庫1", "DEPT002"));
        }

        @Test
        @DisplayName("倉庫コードで検索できる")
        void canFindByWarehouseCode() {
            // Act
            Optional<Warehouse> found = warehouseRepository.findByWarehouseCode("WH002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getWarehouseName()).isEqualTo("材料倉庫1");
        }

        @Test
        @DisplayName("部門コードで検索できる")
        void canFindByDepartmentCode() {
            // Act
            List<Warehouse> found = warehouseRepository.findByDepartmentCode("DEPT001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(w -> "DEPT001".equals(w.getDepartmentCode()));
        }

        @Test
        @DisplayName("存在しない倉庫コードで検索すると空を返す")
        void returnsEmptyForNonExistentWarehouseCode() {
            // Act
            Optional<Warehouse> found = warehouseRepository.findByWarehouseCode("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Warehouse> all = warehouseRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
