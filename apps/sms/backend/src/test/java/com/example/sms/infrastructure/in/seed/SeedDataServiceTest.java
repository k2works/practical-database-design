package com.example.sms.infrastructure.in.seed;

import com.example.sms.application.port.out.DepartmentRepository;
import com.example.sms.application.port.out.EmployeeRepository;
import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductClassificationRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.application.port.out.SalesRepository;
import com.example.sms.application.port.out.ShipmentRepository;
import com.example.sms.domain.model.department.Department;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seed データ投入サービス統合テスト.
 */
@DisplayName("Seed データ投入サービス")
class SeedDataServiceTest extends BaseIntegrationTest {

    @Autowired
    private SeedDataService seedDataService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ProductClassificationRepository productClassificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SalesRepository salesRepository;

    @BeforeEach
    void setUp() {
        // 各テスト前にデータを初期化
        seedDataService.seedAll();
    }

    @Nested
    @DisplayName("マスタデータの妥当性検証")
    class MasterDataValidation {

        @Test
        @DisplayName("部門マスタが21件投入される")
        void seedsDepartments() {
            List<Department> departments = departmentRepository.findAll();
            assertThat(departments).hasSize(21);
        }

        @Test
        @DisplayName("すべての部門が階層構造を持つ")
        void allDepartmentsHaveHierarchy() {
            List<Department> departments = departmentRepository.findAll();

            for (Department dept : departments) {
                assertThat(dept.getDepartmentPath()).isNotBlank();
                assertThat(dept.getHierarchyLevel()).isPositive();
            }
        }

        @Test
        @DisplayName("商品分類マスタが4件投入される")
        void seedsProductCategories() {
            var categories = productClassificationRepository.findAll();
            assertThat(categories).hasSize(4);
        }

        @Test
        @DisplayName("商品マスタが20件投入される")
        void seedsProducts() {
            List<Product> products = productRepository.findAll();
            assertThat(products).hasSize(20);
        }

        @Test
        @DisplayName("すべての商品が分類に所属している")
        void allProductsBelongToCategory() {
            List<Product> products = productRepository.findAll();

            for (Product product : products) {
                assertThat(product.getClassificationCode()).isNotBlank();
            }
        }

        @Test
        @DisplayName("取引先マスタが14件投入される（得意先10件、仕入先4件）")
        void seedsPartners() {
            List<Partner> partners = partnerRepository.findAll();
            assertThat(partners).hasSize(14);

            List<Partner> customers = partnerRepository.findCustomers();
            List<Partner> suppliers = partnerRepository.findSuppliers();

            assertThat(customers).hasSize(10);
            assertThat(suppliers).hasSize(4);
        }

        @Test
        @DisplayName("社員マスタが24件投入される")
        void seedsEmployees() {
            var employees = employeeRepository.findAll();
            assertThat(employees).hasSize(24);
        }
    }

    @Nested
    @DisplayName("在庫データの妥当性検証")
    class InventoryValidation {

        @Test
        @DisplayName("在庫データが20件投入される")
        void seedsInventories() {
            List<Inventory> inventories = inventoryRepository.findAll();
            assertThat(inventories).hasSize(20);
        }

        @Test
        @DisplayName("在庫数量が0以上である")
        void inventoryQuantityIsNonNegative() {
            List<Inventory> inventories = inventoryRepository.findAll();

            for (Inventory inventory : inventories) {
                assertThat(inventory.getCurrentQuantity())
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
                assertThat(inventory.getAllocatedQuantity())
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
            }
        }

        @Test
        @DisplayName("引当数量が在庫数量を超えていない")
        void allocatedQuantityDoesNotExceedInventory() {
            List<Inventory> inventories = inventoryRepository.findAll();

            for (Inventory inventory : inventories) {
                assertThat(inventory.getAllocatedQuantity())
                    .isLessThanOrEqualTo(inventory.getCurrentQuantity());
            }
        }
    }

    @Nested
    @DisplayName("受注データの妥当性検証")
    class OrderValidation {

        @Test
        @DisplayName("受注データが3件投入される")
        void seedsOrders() {
            List<SalesOrder> orders = salesOrderRepository.findAll();
            assertThat(orders).hasSize(3);
        }

        @Test
        @DisplayName("受注に対応する顧客が存在する")
        void orderHasValidCustomer() {
            List<SalesOrder> orders = salesOrderRepository.findAll();

            for (SalesOrder order : orders) {
                var customer = partnerRepository.findByCode(order.getCustomerCode());
                assertThat(customer).isPresent();
                assertThat(customer.get().isCustomer()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("出荷・売上データの妥当性検証")
    class ShipmentAndSalesValidation {

        @Test
        @DisplayName("出荷データは依存関係の複雑さにより初期シードでは投入しない")
        void shipmentsNotSeededInitially() {
            var shipments = shipmentRepository.findAll();
            assertThat(shipments).isEmpty();
        }

        @Test
        @DisplayName("売上データは依存関係の複雑さにより初期シードでは投入しない")
        void salesNotSeededInitially() {
            var sales = salesRepository.findAll();
            assertThat(sales).isEmpty();
        }
    }

    @Nested
    @DisplayName("再投入時の挙動")
    class ReseededBehavior {

        @Test
        @DisplayName("seedAll を複数回実行してもデータ件数が一定")
        void seedAllIsIdempotent() {
            // 2回目の投入
            seedDataService.seedAll();

            List<Department> departments = departmentRepository.findAll();
            List<Product> products = productRepository.findAll();
            List<Partner> partners = partnerRepository.findAll();

            assertThat(departments).hasSize(21);
            assertThat(products).hasSize(20);
            assertThat(partners).hasSize(14);
        }
    }
}
