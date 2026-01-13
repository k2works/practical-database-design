package com.example.pms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.pms.application.port.out.BomRepository;
import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.application.port.out.ProcessRouteRepository;
import com.example.pms.application.port.out.StaffRepository;
import com.example.pms.application.port.out.StockRepository;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.application.port.out.UnitPriceRepository;
import com.example.pms.application.port.out.UnitRepository;
import com.example.pms.application.port.out.WarehouseRepository;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.process.ProcessRoute;
import com.example.pms.infrastructure.in.seed.SeedDataService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Seed データ整合性チェック.
 * chapter31.md 準拠の E 社データが正しく投入され、整合性を保っているかを検証する。
 */
@DisplayName("Seed データ整合性チェック")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.TooManyFields", "PMD.CouplingBetweenObjects"})
class SeedDataIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SeedDataService seedDataService;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private ProcessRouteRepository processRouteRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UnitPriceRepository unitPriceRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    void seedData() {
        // Seed データを投入（一度だけ実行）
        seedDataService.seedAll();
    }

    @Nested
    @DisplayName("マスタデータの妥当性検証")
    class MasterDataValidation {

        @Test
        @DisplayName("単位マスタが投入されている")
        void unitsShouldBeSeeded() {
            assertThat(unitRepository.findByUnitCode("PCS")).isPresent();
            assertThat(unitRepository.findByUnitCode("KG")).isPresent();
            assertThat(unitRepository.findByUnitCode("M")).isPresent();
            assertThat(unitRepository.findByUnitCode("SET")).isPresent();
        }

        @Test
        @DisplayName("部門マスタが投入されている")
        void departmentsShouldBeSeeded() {
            assertThat(departmentRepository.findByDepartmentCode("SALES")).isPresent();
            assertThat(departmentRepository.findByDepartmentCode("MFG")).isPresent();
            assertThat(departmentRepository.findByDepartmentCode("QUALITY")).isPresent();
            assertThat(departmentRepository.findByDepartmentCode("PURCHASE")).isPresent();
            assertThat(departmentRepository.findByDepartmentCode("WAREHOUSE")).isPresent();
        }

        @Test
        @DisplayName("取引先マスタが投入されている（仕入先・外注先・得意先）")
        void suppliersShouldBeSeeded() {
            // 仕入先
            assertThat(supplierRepository.findBySupplierCode("SUP-001")).isPresent();
            assertThat(supplierRepository.findBySupplierCode("SUP-002")).isPresent();

            // 外注先
            assertThat(supplierRepository.findBySupplierCode("OUT-001")).isPresent();
            assertThat(supplierRepository.findBySupplierCode("OUT-002")).isPresent();

            // 得意先
            assertThat(supplierRepository.findBySupplierCode("CUS-001")).isPresent();
        }

        @Test
        @DisplayName("倉庫マスタが投入されている")
        void warehousesShouldBeSeeded() {
            assertThat(warehouseRepository.findByWarehouseCode("WH-MAT")).isPresent();
            assertThat(warehouseRepository.findByWarehouseCode("WH-PART")).isPresent();
            assertThat(warehouseRepository.findByWarehouseCode("WH-PROD")).isPresent();
        }

        @Test
        @DisplayName("工程マスタが投入されている")
        void processesShouldBeSeeded() {
            assertThat(processRepository.findByProcessCode("LATHE")).isPresent();
            assertThat(processRepository.findByProcessCode("MILL")).isPresent();
            assertThat(processRepository.findByProcessCode("HOB")).isPresent();
            assertThat(processRepository.findByProcessCode("ASM")).isPresent();
            assertThat(processRepository.findByProcessCode("INS-SHIP")).isPresent();
            assertThat(processRepository.findByProcessCode("OUT-MEKI")).isPresent();
            assertThat(processRepository.findByProcessCode("OUT-HEAT")).isPresent();
        }

        @Test
        @DisplayName("すべての品目が単位を持つ")
        void allItemsHaveUnit() {
            List<Item> items = itemRepository.findAll();

            assertThat(items).isNotEmpty();
            for (Item item : items) {
                assertThat(item.getUnitCode())
                        .as("品目 %s の単位", item.getItemCode())
                        .isNotBlank();
            }
        }

        @Test
        @DisplayName("品目区分ごとに適切な品目が登録されている")
        void itemsHaveCorrectCategory() {
            // 製品
            var prodA001 = itemRepository.findByItemCode("PROD-A001");
            assertThat(prodA001).isPresent();
            assertThat(prodA001.get().getItemCategory()).isEqualTo(ItemCategory.PRODUCT);

            // 半製品
            var semiA001 = itemRepository.findByItemCode("SEMI-A001");
            assertThat(semiA001).isPresent();
            assertThat(semiA001.get().getItemCategory()).isEqualTo(ItemCategory.SEMI_PRODUCT);

            // 部品
            var part001 = itemRepository.findByItemCode("PART-001");
            assertThat(part001).isPresent();
            assertThat(part001.get().getItemCategory()).isEqualTo(ItemCategory.PART);

            // 材料
            var mat001 = itemRepository.findByItemCode("MAT-001");
            assertThat(mat001).isPresent();
            assertThat(mat001.get().getItemCategory()).isEqualTo(ItemCategory.MATERIAL);
        }

        @Test
        @DisplayName("担当者マスタが投入されている")
        void staffShouldBeSeeded() {
            assertThat(staffRepository.findByStaffCode("EMP-001")).isPresent();
            assertThat(staffRepository.findByStaffCode("EMP-006")).isPresent();
            assertThat(staffRepository.findByStaffCode("EMP-008")).isPresent();
        }
    }

    @Nested
    @DisplayName("BOM 展開の整合性確認")
    class BomIntegrity {

        @Test
        @DisplayName("製品 PROD-A001 の BOM が正しく展開できる")
        void productBomCanBeExpanded() {
            List<Bom> boms = bomRepository.findByParentItemCode("PROD-A001");

            assertThat(boms).isNotEmpty();
            assertThat(boms).anyMatch(bom -> "SEMI-A001".equals(bom.getChildItemCode()));
            assertThat(boms).anyMatch(bom -> "PART-001".equals(bom.getChildItemCode()));
            assertThat(boms).anyMatch(bom -> "PART-002".equals(bom.getChildItemCode()));
        }

        @Test
        @DisplayName("製品 PROD-B001 (ギアボックス) の BOM が正しく展開できる")
        void gearboxBomCanBeExpanded() {
            List<Bom> boms = bomRepository.findByParentItemCode("PROD-B001");

            assertThat(boms).isNotEmpty();
            assertThat(boms).anyMatch(bom -> "SEMI-B001".equals(bom.getChildItemCode()));
            assertThat(boms).anyMatch(bom -> "SEMI-B002".equals(bom.getChildItemCode()));
            assertThat(boms).anyMatch(bom -> "SEMI-B003".equals(bom.getChildItemCode()));
        }

        @Test
        @DisplayName("BOM に循環参照がない")
        void noCyclicReferenceInBom() {
            // 主要な親品目の BOM をチェック
            List<String> parentItems = List.of(
                    "PROD-A001", "PROD-B001", "PROD-C001",
                    "SEMI-A001", "SEMI-B001", "SEMI-B002", "SEMI-B003", "SEMI-C001"
            );

            for (String parentItemCode : parentItems) {
                List<Bom> boms = bomRepository.findByParentItemCode(parentItemCode);
                for (Bom bom : boms) {
                    // 簡易的な循環参照チェック（親=子は禁止）
                    assertThat(bom.getParentItemCode())
                            .as("BOM: %s -> %s", bom.getParentItemCode(), bom.getChildItemCode())
                            .isNotEqualTo(bom.getChildItemCode());
                }
            }
        }

        @Test
        @DisplayName("BOM の必要量が正の値である")
        void bomRequiredQuantityIsPositive() {
            // 主要な親品目の BOM をチェック
            List<String> parentItems = List.of(
                    "PROD-A001", "PROD-B001", "PROD-C001",
                    "SEMI-A001", "SEMI-B001", "SEMI-B002", "SEMI-B003", "SEMI-C001"
            );

            int totalBoms = 0;
            for (String parentItemCode : parentItems) {
                List<Bom> boms = bomRepository.findByParentItemCode(parentItemCode);
                totalBoms += boms.size();
                for (Bom bom : boms) {
                    assertThat(bom.getRequiredQuantity())
                            .as("BOM %s -> %s の必要量", bom.getParentItemCode(), bom.getChildItemCode())
                            .isGreaterThan(BigDecimal.ZERO);
                }
            }
            assertThat(totalBoms).as("BOM 件数").isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("工程表の整合性確認")
    class ProcessRouteIntegrity {

        @Test
        @DisplayName("製品 PROD-A001 の工程表が定義されている")
        void productRouteExists() {
            List<ProcessRoute> routes = processRouteRepository.findByItemCode("PROD-A001");

            assertThat(routes).isNotEmpty();
            assertThat(routes).anyMatch(r -> "ASM".equals(r.getProcessCode()));
            assertThat(routes).anyMatch(r -> "INS-SHIP".equals(r.getProcessCode()));
        }

        @Test
        @DisplayName("半製品 SEMI-A001 の工程表が外注工程を含む")
        void semiProductRouteIncludesOutsourcing() {
            List<ProcessRoute> routes = processRouteRepository.findByItemCode("SEMI-A001");

            assertThat(routes).isNotEmpty();
            assertThat(routes).anyMatch(r -> "LATHE".equals(r.getProcessCode()));
            assertThat(routes).anyMatch(r -> "GRIND".equals(r.getProcessCode()));
            assertThat(routes).anyMatch(r -> "OUT-MEKI".equals(r.getProcessCode())); // メッキ外注
        }

        @Test
        @DisplayName("工程表の順序が正しい")
        void processRouteSequenceIsCorrect() {
            List<ProcessRoute> routes = processRouteRepository.findByItemCode("SEMI-B002");

            assertThat(routes).isNotEmpty();

            // 順序でソートして確認
            List<ProcessRoute> sorted = routes.stream()
                    .sorted((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
                    .toList();

            for (int i = 0; i < sorted.size(); i++) {
                assertThat(sorted.get(i).getSequence())
                        .as("工程順序が連番になっている")
                        .isEqualTo(i + 1);
            }
        }
    }

    @Nested
    @DisplayName("在庫数量の正確性検証")
    class StockValidation {

        @Test
        @DisplayName("在庫数量が0以上である")
        void stockQuantityIsNonNegative() {
            List<Stock> stocks = stockRepository.findAll();

            assertThat(stocks).isNotEmpty();
            for (Stock stock : stocks) {
                assertThat(stock.getStockQuantity())
                        .as("在庫 %s/%s の数量", stock.getLocationCode(), stock.getItemCode())
                        .isGreaterThanOrEqualTo(BigDecimal.ZERO);
            }
        }

        @Test
        @DisplayName("材料倉庫に材料在庫がある")
        void materialWarehouseHasMaterialStock() {
            var matStock = stockRepository.findByLocationAndItem("WH-MAT", "MAT-001");

            assertThat(matStock).isPresent();
            assertThat(matStock.get().getStockQuantity()).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("製品倉庫に製品在庫がある")
        void productWarehouseHasProductStock() {
            var prodStock = stockRepository.findByLocationAndItem("WH-PROD", "PROD-A001");

            assertThat(prodStock).isPresent();
            assertThat(prodStock.get().getStockQuantity()).isGreaterThan(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("単価マスタの整合性確認")
    class UnitPriceValidation {

        @Test
        @DisplayName("材料の仕入単価が設定されている")
        void materialPurchasePriceExists() {
            var prices = unitPriceRepository.findByItemCode("MAT-001");

            assertThat(prices).isNotEmpty();
            assertThat(prices.get(0).getPrice()).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("外注加工単価が設定されている")
        void outsourcingPriceExists() {
            var prices = unitPriceRepository.findByItemCode("SEMI-A001");

            assertThat(prices).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("オーダ情報の検証")
    class OrderValidation {

        @Test
        @DisplayName("製造オーダが投入されている")
        void manufacturingOrdersExist() {
            var order = orderRepository.findByOrderNumber("MO-2025-001");

            assertThat(order).isPresent();
            assertThat(order.get().getItemCode()).isEqualTo("PROD-A001");
            assertThat(order.get().getPlanQuantity()).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("オーダの納期が着手予定日より後である")
        void dueDateIsAfterStartDate() {
            List<Order> orders = orderRepository.findAll();

            for (Order order : orders) {
                if (order.getStartDate() != null && order.getDueDate() != null) {
                    assertThat(order.getDueDate())
                            .as("オーダ %s の納期", order.getOrderNumber())
                            .isAfterOrEqualTo(order.getStartDate());
                }
            }
        }
    }

    @Nested
    @DisplayName("MRP シナリオの検証")
    class MrpScenarioValidation {

        @Test
        @DisplayName("製造オーダから所要量が正しく計算される")
        void calculateRequirementsFromManufacturingOrder() {
            var orderOpt = orderRepository.findByOrderNumber("MO-2025-001");
            assertThat(orderOpt).isPresent();

            Order order = orderOpt.get();
            assertThat(order.getPlanQuantity()).isEqualByComparingTo(new BigDecimal("100"));

            // BOM 展開で必要量を計算
            List<Bom> boms = bomRepository.findByParentItemCode(order.getItemCode());

            assertThat(boms).isNotEmpty();
            for (Bom bom : boms) {
                BigDecimal requiredQty = order.getPlanQuantity()
                        .multiply(bom.getRequiredQuantity());
                assertThat(requiredQty)
                        .as("品目 %s の所要量", bom.getChildItemCode())
                        .isGreaterThan(BigDecimal.ZERO);
            }
        }
    }
}
