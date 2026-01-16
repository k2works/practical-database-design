package com.example.pms;

import com.example.pms.application.port.out.AllocationRepository;
import com.example.pms.application.port.out.BomRepository;
import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.application.port.out.MpsRepository;
import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.application.port.out.ProcessRouteRepository;
import com.example.pms.application.port.out.RequirementRepository;
import com.example.pms.application.port.out.StaffRepository;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.application.port.out.UnitPriceRepository;
import com.example.pms.application.port.out.UnitRepository;
import com.example.pms.application.port.out.WorkCalendarRepository;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * アプリケーション起動スモークテスト.
 * Testcontainers を使用して PostgreSQL 環境でアプリケーションの起動と
 * 基本的な機能が正常に動作することを検証する。
 */
@DisplayName("アプリケーション起動スモークテスト")
@SuppressWarnings("PMD.CouplingBetweenObjects")
class ApplicationTest extends BaseIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    @Nested
    @DisplayName("コンテキスト起動")
    class ContextStartup {

        @Test
        @DisplayName("Spring ApplicationContext がロードされる")
        void applicationContextLoads() {
            assertThat(applicationContext).isNotNull();
        }

        @Test
        @DisplayName("DataSource が設定される")
        void dataSourceIsConfigured() {
            assertThat(dataSource).isNotNull();
        }

        @Test
        @DisplayName("Flyway が設定される")
        void flywayIsConfigured() {
            assertThat(flyway).isNotNull();
        }
    }

    @Nested
    @DisplayName("Flyway マイグレーション")
    class FlywayMigration {

        @Test
        @DisplayName("マイグレーションが正常に適用される")
        void migrationsAppliedSuccessfully() {
            var info = flyway.info();
            assertThat(info.applied()).isNotEmpty();
            assertThat(info.pending()).isEmpty();
        }

        @Test
        @DisplayName("V1 マイグレーションが適用されている")
        void v1MigrationIsApplied() {
            var info = flyway.info();
            var appliedMigrations = info.applied();

            assertThat(appliedMigrations)
                    .anyMatch(migration -> "1".equals(migration.getVersion().getVersion()));
        }

        @Test
        @DisplayName("V2 マイグレーションが適用されている")
        void v2MigrationIsApplied() {
            var info = flyway.info();
            var appliedMigrations = info.applied();

            assertThat(appliedMigrations)
                    .anyMatch(migration -> "2".equals(migration.getVersion().getVersion()));
        }
    }

    @Nested
    @DisplayName("データベーステーブル")
    class DatabaseTables {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Test
        @DisplayName("単位マスタテーブルが存在する")
        void unitTableExists() {
            assertTableExists("単位マスタ");
        }

        @Test
        @DisplayName("品目マスタテーブルが存在する")
        void itemTableExists() {
            assertTableExists("品目マスタ");
        }

        @Test
        @DisplayName("部品構成表テーブルが存在する")
        void bomTableExists() {
            assertTableExists("部品構成表");
        }

        @Test
        @DisplayName("カレンダマスタテーブルが存在する")
        void calendarTableExists() {
            assertTableExists("カレンダマスタ");
        }

        @Test
        @DisplayName("場所マスタテーブルが存在する")
        void locationTableExists() {
            assertTableExists("場所マスタ");
        }

        @Test
        @DisplayName("取引先マスタテーブルが存在する")
        void supplierTableExists() {
            assertTableExists("取引先マスタ");
        }

        @Test
        @DisplayName("部門マスタテーブルが存在する")
        void departmentTableExists() {
            assertTableExists("部門マスタ");
        }

        @Test
        @DisplayName("担当者マスタテーブルが存在する")
        void staffTableExists() {
            assertTableExists("担当者マスタ");
        }

        @Test
        @DisplayName("工程マスタテーブルが存在する")
        void processTableExists() {
            assertTableExists("工程マスタ");
        }

        @Test
        @DisplayName("工程表テーブルが存在する")
        void processRouteTableExists() {
            assertTableExists("工程表");
        }

        @Test
        @DisplayName("単価マスタテーブルが存在する")
        void unitPriceTableExists() {
            assertTableExists("単価マスタ");
        }

        @Test
        @DisplayName("欠点マスタテーブルが存在する")
        void defectTableExists() {
            assertTableExists("欠点マスタ");
        }

        @Test
        @DisplayName("基準生産計画テーブルが存在する")
        void mpsTableExists() {
            assertTableExists("基準生産計画");
        }

        @Test
        @DisplayName("オーダ情報テーブルが存在する")
        void orderTableExists() {
            assertTableExists("オーダ情報");
        }

        @Test
        @DisplayName("所要情報テーブルが存在する")
        void requirementTableExists() {
            assertTableExists("所要情報");
        }

        @Test
        @DisplayName("引当情報テーブルが存在する")
        void allocationTableExists() {
            assertTableExists("引当情報");
        }

        @Test
        @DisplayName("全16テーブルが存在する")
        void allTablesExist() {
            List<String> expectedTables = List.of(
                    "単位マスタ", "品目マスタ", "部品構成表", "カレンダマスタ",
                    "場所マスタ", "取引先マスタ", "部門マスタ", "担当者マスタ",
                    "工程マスタ", "工程表", "単価マスタ", "欠点マスタ",
                    "基準生産計画", "オーダ情報", "所要情報", "引当情報"
            );

            for (String tableName : expectedTables) {
                assertTableExists(tableName);
            }
        }

        private void assertTableExists(String tableName) {
            String sql = """
                SELECT EXISTS (
                    SELECT FROM information_schema.tables
                    WHERE table_schema = 'public'
                    AND table_name = ?
                )
                """;
            Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
            assertThat(exists)
                    .as("テーブル '%s' が存在すること", tableName)
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("リポジトリ Bean")
    class RepositoryBeans {

        @Autowired
        private UnitRepository unitRepository;

        @Autowired
        private ItemRepository itemRepository;

        @Autowired
        private BomRepository bomRepository;

        @Autowired
        private WorkCalendarRepository workCalendarRepository;

        @Autowired
        private LocationRepository locationRepository;

        @Autowired
        private SupplierRepository supplierRepository;

        @Autowired
        private DepartmentRepository departmentRepository;

        @Autowired
        private StaffRepository staffRepository;

        @Autowired
        private ProcessRepository processRepository;

        @Autowired
        private ProcessRouteRepository processRouteRepository;

        @Autowired
        private UnitPriceRepository unitPriceRepository;

        @Autowired
        private DefectRepository defectRepository;

        @Autowired
        private MpsRepository mpsRepository;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private RequirementRepository requirementRepository;

        @Autowired
        private AllocationRepository allocationRepository;

        @Test
        @DisplayName("UnitRepository が注入される")
        void unitRepositoryIsInjected() {
            assertThat(unitRepository).isNotNull();
        }

        @Test
        @DisplayName("ItemRepository が注入される")
        void itemRepositoryIsInjected() {
            assertThat(itemRepository).isNotNull();
        }

        @Test
        @DisplayName("BomRepository が注入される")
        void bomRepositoryIsInjected() {
            assertThat(bomRepository).isNotNull();
        }

        @Test
        @DisplayName("WorkCalendarRepository が注入される")
        void workCalendarRepositoryIsInjected() {
            assertThat(workCalendarRepository).isNotNull();
        }

        @Test
        @DisplayName("LocationRepository が注入される")
        void locationRepositoryIsInjected() {
            assertThat(locationRepository).isNotNull();
        }

        @Test
        @DisplayName("SupplierRepository が注入される")
        void supplierRepositoryIsInjected() {
            assertThat(supplierRepository).isNotNull();
        }

        @Test
        @DisplayName("DepartmentRepository が注入される")
        void departmentRepositoryIsInjected() {
            assertThat(departmentRepository).isNotNull();
        }

        @Test
        @DisplayName("StaffRepository が注入される")
        void staffRepositoryIsInjected() {
            assertThat(staffRepository).isNotNull();
        }

        @Test
        @DisplayName("ProcessRepository が注入される")
        void processRepositoryIsInjected() {
            assertThat(processRepository).isNotNull();
        }

        @Test
        @DisplayName("ProcessRouteRepository が注入される")
        void processRouteRepositoryIsInjected() {
            assertThat(processRouteRepository).isNotNull();
        }

        @Test
        @DisplayName("UnitPriceRepository が注入される")
        void unitPriceRepositoryIsInjected() {
            assertThat(unitPriceRepository).isNotNull();
        }

        @Test
        @DisplayName("DefectRepository が注入される")
        void defectRepositoryIsInjected() {
            assertThat(defectRepository).isNotNull();
        }

        @Test
        @DisplayName("MpsRepository が注入される")
        void mpsRepositoryIsInjected() {
            assertThat(mpsRepository).isNotNull();
        }

        @Test
        @DisplayName("OrderRepository が注入される")
        void orderRepositoryIsInjected() {
            assertThat(orderRepository).isNotNull();
        }

        @Test
        @DisplayName("RequirementRepository が注入される")
        void requirementRepositoryIsInjected() {
            assertThat(requirementRepository).isNotNull();
        }

        @Test
        @DisplayName("AllocationRepository が注入される")
        void allocationRepositoryIsInjected() {
            assertThat(allocationRepository).isNotNull();
        }

        @Test
        @DisplayName("全16リポジトリが注入される")
        void allRepositoriesAreInjected() {
            assertThat(unitRepository).isNotNull();
            assertThat(itemRepository).isNotNull();
            assertThat(bomRepository).isNotNull();
            assertThat(workCalendarRepository).isNotNull();
            assertThat(locationRepository).isNotNull();
            assertThat(supplierRepository).isNotNull();
            assertThat(departmentRepository).isNotNull();
            assertThat(staffRepository).isNotNull();
            assertThat(processRepository).isNotNull();
            assertThat(processRouteRepository).isNotNull();
            assertThat(unitPriceRepository).isNotNull();
            assertThat(defectRepository).isNotNull();
            assertThat(mpsRepository).isNotNull();
            assertThat(orderRepository).isNotNull();
            assertThat(requirementRepository).isNotNull();
            assertThat(allocationRepository).isNotNull();
        }
    }

    @Nested
    @DisplayName("ENUM 型")
    class EnumTypes {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Test
        @DisplayName("品目区分 ENUM が存在する")
        void itemCategoryEnumExists() {
            assertEnumExists("品目区分");
        }

        @Test
        @DisplayName("日付区分 ENUM が存在する")
        void dateTypeEnumExists() {
            assertEnumExists("日付区分");
        }

        @Test
        @DisplayName("場所区分 ENUM が存在する")
        void locationTypeEnumExists() {
            assertEnumExists("場所区分");
        }

        @Test
        @DisplayName("取引先区分 ENUM が存在する")
        void supplierTypeEnumExists() {
            assertEnumExists("取引先区分");
        }

        @Test
        @DisplayName("計画ステータス ENUM が存在する")
        void planStatusEnumExists() {
            assertEnumExists("計画ステータス");
        }

        @Test
        @DisplayName("オーダ種別 ENUM が存在する")
        void orderTypeEnumExists() {
            assertEnumExists("オーダ種別");
        }

        @Test
        @DisplayName("引当区分 ENUM が存在する")
        void allocationTypeEnumExists() {
            assertEnumExists("引当区分");
        }

        private void assertEnumExists(String enumName) {
            String sql = """
                SELECT EXISTS (
                    SELECT 1 FROM pg_type WHERE typname = ?
                )
                """;
            Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, enumName);
            assertThat(exists)
                    .as("ENUM '%s' が存在すること", enumName)
                    .isTrue();
        }
    }
}
