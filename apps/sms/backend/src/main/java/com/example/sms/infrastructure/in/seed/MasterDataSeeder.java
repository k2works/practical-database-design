package com.example.sms.infrastructure.in.seed;

import com.example.sms.application.port.out.DepartmentRepository;
import com.example.sms.application.port.out.EmployeeRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductClassificationRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.WarehouseRepository;
import com.example.sms.domain.model.department.Department;
import com.example.sms.domain.model.employee.Employee;
import com.example.sms.domain.model.inventory.Warehouse;
import com.example.sms.domain.model.inventory.WarehouseType;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.ProductClassification;
import com.example.sms.domain.model.product.TaxCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * マスタデータ Seeder.
 * B社事例に基づくマスタデータを投入する。
 */
@Component
public class MasterDataSeeder {

    private static final Logger LOG = LoggerFactory.getLogger(MasterDataSeeder.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PartnerRepository partnerRepository;
    private final ProductClassificationRepository productClassificationRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public MasterDataSeeder(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            PartnerRepository partnerRepository,
            ProductClassificationRepository productClassificationRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.partnerRepository = partnerRepository;
        this.productClassificationRepository = productClassificationRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * すべてのマスタデータを投入.
     */
    public void seedAll(LocalDate effectiveDate) {
        seedDepartments(effectiveDate);
        seedWarehouses();
        seedProductClassifications();
        seedProducts();
        seedPartners();
        seedEmployees(effectiveDate);
    }

    /**
     * すべてのマスタデータを削除.
     */
    public void cleanAll() {
        employeeRepository.deleteAll();
        productRepository.deleteAll();
        productClassificationRepository.deleteAll();
        partnerRepository.deleteAll();
        warehouseRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    private void seedDepartments(LocalDate effectiveDate) {
        LOG.info("部門マスタを投入中...");

        List<Department> departments = List.of(
            // 本社
            Department.builder()
                .departmentCode("000000").startDate(effectiveDate)
                .departmentName("本社").departmentPath("/000000").hierarchyLevel(1).isLeaf(false).build(),

            // 食肉製造・販売事業
            Department.builder()
                .departmentCode("100000").startDate(effectiveDate)
                .departmentName("食肉製造・販売事業").departmentPath("/000000/100000").hierarchyLevel(2).isLeaf(false).build(),
            Department.builder()
                .departmentCode("110000").startDate(effectiveDate)
                .departmentName("食肉加工部門").departmentPath("/000000/100000/110000").hierarchyLevel(3).isLeaf(false).build(),
            Department.builder()
                .departmentCode("111000").startDate(effectiveDate)
                .departmentName("牛肉・豚肉・鶏肉課").departmentPath("/000000/100000/110000/111000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("112000").startDate(effectiveDate)
                .departmentName("食肉加工品課").departmentPath("/000000/100000/110000/112000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("120000").startDate(effectiveDate)
                .departmentName("小売販売部門").departmentPath("/000000/100000/120000").hierarchyLevel(3).isLeaf(false).build(),
            Department.builder()
                .departmentCode("121000").startDate(effectiveDate)
                .departmentName("直営小売店課").departmentPath("/000000/100000/120000/121000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("122000").startDate(effectiveDate)
                .departmentName("百貨店・スーパー向け販売課").departmentPath("/000000/100000/120000/122000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("130000").startDate(effectiveDate)
                .departmentName("新規取引先開拓部門").departmentPath("/000000/100000/130000").hierarchyLevel(3).isLeaf(false).build(),
            Department.builder()
                .departmentCode("131000").startDate(effectiveDate)
                .departmentName("ホテル・旅館向け課").departmentPath("/000000/100000/130000/131000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("132000").startDate(effectiveDate)
                .departmentName("飲食店向け課").departmentPath("/000000/100000/130000/132000").hierarchyLevel(4).isLeaf(true).build(),

            // 食肉加工品事業
            Department.builder()
                .departmentCode("200000").startDate(effectiveDate)
                .departmentName("食肉加工品事業").departmentPath("/000000/200000").hierarchyLevel(2).isLeaf(false).build(),
            Department.builder()
                .departmentCode("210000").startDate(effectiveDate)
                .departmentName("自社ブランド部門").departmentPath("/000000/200000/210000").hierarchyLevel(3).isLeaf(false).build(),
            Department.builder()
                .departmentCode("211000").startDate(effectiveDate)
                .departmentName("贈答用製品製造課").departmentPath("/000000/200000/210000/211000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("212000").startDate(effectiveDate)
                .departmentName("道の駅・土産物製品販売課").departmentPath("/000000/200000/210000/212000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("220000").startDate(effectiveDate)
                .departmentName("相手先ブランド製造(OEM)部門").departmentPath("/000000/200000/220000").hierarchyLevel(3).isLeaf(false).build(),
            Department.builder()
                .departmentCode("221000").startDate(effectiveDate)
                .departmentName("客先要望対応課").departmentPath("/000000/200000/220000/221000").hierarchyLevel(4).isLeaf(true).build(),

            // コンサルティング事業
            Department.builder()
                .departmentCode("300000").startDate(effectiveDate)
                .departmentName("コンサルティング事業").departmentPath("/000000/300000").hierarchyLevel(2).isLeaf(false).build(),
            Department.builder()
                .departmentCode("310000").startDate(effectiveDate)
                .departmentName("顧客対応部門").departmentPath("/000000/300000/310000").hierarchyLevel(3).isLeaf(false).build(),
            Department.builder()
                .departmentCode("311000").startDate(effectiveDate)
                .departmentName("メニュー提案課").departmentPath("/000000/300000/310000/311000").hierarchyLevel(4).isLeaf(true).build(),
            Department.builder()
                .departmentCode("312000").startDate(effectiveDate)
                .departmentName("半加工商品提供課").departmentPath("/000000/300000/310000/312000").hierarchyLevel(4).isLeaf(true).build()
        );

        departments.forEach(departmentRepository::save);
        if (LOG.isInfoEnabled()) {
            LOG.info("部門マスタ {}件 投入完了", departments.size());
        }
    }

    private void seedWarehouses() {
        LOG.info("倉庫マスタを投入中...");

        List<Warehouse> warehouses = List.of(
            Warehouse.builder()
                .warehouseCode("WH-HQ")
                .warehouseName("本社倉庫")
                .warehouseNameKana("ホンシャソウコ")
                .warehouseType(WarehouseType.OWN)
                .postalCode("100-0001")
                .address("東京都千代田区1-1-1")
                .phoneNumber("03-1234-5678")
                .activeFlag(true)
                .build(),
            Warehouse.builder()
                .warehouseCode("WH-FAC")
                .warehouseName("工場倉庫")
                .warehouseNameKana("コウジョウソウコ")
                .warehouseType(WarehouseType.OWN)
                .postalCode("200-0001")
                .address("埼玉県さいたま市2-2-2")
                .phoneNumber("048-1234-5678")
                .activeFlag(true)
                .build(),
            Warehouse.builder()
                .warehouseCode("WH-EXT")
                .warehouseName("外部委託倉庫")
                .warehouseNameKana("ガイブイタクソウコ")
                .warehouseType(WarehouseType.EXTERNAL)
                .postalCode("300-0001")
                .address("神奈川県横浜市3-3-3")
                .phoneNumber("045-1234-5678")
                .activeFlag(true)
                .build()
        );

        warehouses.forEach(warehouseRepository::save);
        if (LOG.isInfoEnabled()) {
            LOG.info("倉庫マスタ {}件 投入完了", warehouses.size());
        }
    }

    private void seedProductClassifications() {
        LOG.info("商品分類マスタを投入中...");

        List<ProductClassification> categories = List.of(
            ProductClassification.builder()
                .classificationCode("CAT-BEEF").classificationName("牛肉")
                .classificationPath("/CAT-BEEF").hierarchyLevel(1).isLeaf(true).build(),
            ProductClassification.builder()
                .classificationCode("CAT-PORK").classificationName("豚肉")
                .classificationPath("/CAT-PORK").hierarchyLevel(1).isLeaf(true).build(),
            ProductClassification.builder()
                .classificationCode("CAT-CHKN").classificationName("鶏肉")
                .classificationPath("/CAT-CHKN").hierarchyLevel(1).isLeaf(true).build(),
            ProductClassification.builder()
                .classificationCode("CAT-PROC").classificationName("加工品")
                .classificationPath("/CAT-PROC").hierarchyLevel(1).isLeaf(true).build()
        );

        categories.forEach(productClassificationRepository::save);
        if (LOG.isInfoEnabled()) {
            LOG.info("商品分類マスタ {}件 投入完了", categories.size());
        }
    }

    private void seedProducts() {
        LOG.info("商品マスタを投入中...");

        List<Product> products = List.of(
            // 牛肉
            createProduct("BEEF-001", "黒毛和牛サーロイン", "CAT-BEEF", 8000, 5000),
            createProduct("BEEF-002", "黒毛和牛ロース", "CAT-BEEF", 6000, 3800),
            createProduct("BEEF-003", "黒毛和牛カルビ", "CAT-BEEF", 5500, 3500),
            createProduct("BEEF-004", "黒毛和牛ヒレ", "CAT-BEEF", 10_000, 6500),
            createProduct("BEEF-005", "黒毛和牛切り落とし", "CAT-BEEF", 2500, 1500),

            // 豚肉
            createProduct("PORK-001", "豚ロース", "CAT-PORK", 1200, 750),
            createProduct("PORK-002", "豚バラ", "CAT-PORK", 980, 600),
            createProduct("PORK-003", "豚ヒレ", "CAT-PORK", 1500, 950),
            createProduct("PORK-004", "豚コマ", "CAT-PORK", 680, 400),
            createProduct("PORK-005", "豚肩ロース", "CAT-PORK", 1100, 700),

            // 鶏肉
            createProduct("CHKN-001", "鶏もも", "CAT-CHKN", 480, 280),
            createProduct("CHKN-002", "鶏むね", "CAT-CHKN", 380, 220),
            createProduct("CHKN-003", "手羽先", "CAT-CHKN", 350, 200),
            createProduct("CHKN-004", "手羽元", "CAT-CHKN", 320, 180),
            createProduct("CHKN-005", "鶏ささみ", "CAT-CHKN", 520, 320),

            // 加工品
            createProduct("PROC-001", "ローストビーフ", "CAT-PROC", 3500, 2000),
            createProduct("PROC-002", "ロースハム", "CAT-PROC", 1800, 1000),
            createProduct("PROC-003", "あらびきソーセージ", "CAT-PROC", 680, 400),
            createProduct("PROC-004", "ベーコン", "CAT-PROC", 980, 580),
            createProduct("PROC-005", "手作りコロッケ", "CAT-PROC", 250, 120)
        );

        products.forEach(productRepository::save);
        if (LOG.isInfoEnabled()) {
            LOG.info("商品マスタ {}件 投入完了", products.size());
        }
    }

    private Product createProduct(String code, String name, String classificationCode,
                                   int sellingPrice, int purchasePrice) {
        return Product.builder()
            .productCode(code)
            .productFullName(name)
            .productName(name)
            .productCategory(ProductCategory.PRODUCT)
            .taxCategory(TaxCategory.EXCLUSIVE)
            .classificationCode(classificationCode)
            .sellingPrice(new BigDecimal(sellingPrice))
            .purchasePrice(new BigDecimal(purchasePrice))
            .isInventoryManaged(true)
            .isInventoryAllocated(true)
            .build();
    }

    private void seedPartners() {
        LOG.info("取引先マスタを投入中...");

        List<Partner> partners = List.of(
            // 得意先（百貨店）
            createCustomer("CUS-001", "地域百貨店"),
            createCustomer("CUS-002", "X県有名百貨店"),
            // 得意先（スーパー）
            createCustomer("CUS-003", "地域スーパーチェーン"),
            createCustomer("CUS-004", "広域スーパーチェーン"),
            // 得意先（ホテル・旅館）
            createCustomer("CUS-005", "シティホテル"),
            createCustomer("CUS-006", "温泉旅館"),
            // 得意先（飲食店）
            createCustomer("CUS-007", "焼肉レストラン"),
            createCustomer("CUS-008", "イタリアンレストラン"),
            // 得意先（観光施設）
            createCustomer("CUS-009", "道の駅"),
            createCustomer("CUS-010", "観光センター"),
            // 仕入先（食肉卸）
            createSupplier("SUP-001", "地域食肉卸A社"),
            createSupplier("SUP-002", "地域食肉卸B社"),
            // 仕入先（畜産業者）
            createSupplier("SUP-003", "地域畜産農家"),
            createSupplier("SUP-004", "県内畜産組合")
        );

        partners.forEach(partnerRepository::save);
        if (LOG.isInfoEnabled()) {
            LOG.info("取引先マスタ {}件 投入完了", partners.size());
        }
    }

    private Partner createCustomer(String code, String name) {
        return Partner.builder()
            .partnerCode(code)
            .partnerName(name)
            .isCustomer(true)
            .isSupplier(false)
            .creditLimit(new BigDecimal("10000000"))
            .build();
    }

    private Partner createSupplier(String code, String name) {
        return Partner.builder()
            .partnerCode(code)
            .partnerName(name)
            .isCustomer(false)
            .isSupplier(true)
            .build();
    }

    private void seedEmployees(LocalDate effectiveDate) {
        LOG.info("社員マスタを投入中...");

        List<Employee> employees = List.of(
            // 経営層
            createEmployee("EMP-001", "山田 太郎", "000000", effectiveDate),
            createEmployee("EMP-002", "佐藤 次郎", "000000", effectiveDate),

            // 食肉製造・販売事業（正社員8名）
            createEmployee("EMP-003", "鈴木 三郎", "111000", effectiveDate),
            createEmployee("EMP-004", "高橋 四郎", "111000", effectiveDate),
            createEmployee("EMP-005", "田中 五郎", "112000", effectiveDate),
            createEmployee("EMP-006", "伊藤 六郎", "112000", effectiveDate),
            createEmployee("EMP-007", "渡辺 七郎", "121000", effectiveDate),
            createEmployee("EMP-008", "山本 八郎", "121000", effectiveDate),
            createEmployee("EMP-009", "中村 九郎", "122000", effectiveDate),
            createEmployee("EMP-010", "小林 十郎", "122000", effectiveDate),

            // 食肉加工品事業（正社員6名）
            createEmployee("EMP-011", "加藤 一男", "211000", effectiveDate),
            createEmployee("EMP-012", "吉田 二男", "211000", effectiveDate),
            createEmployee("EMP-013", "山口 三男", "212000", effectiveDate),
            createEmployee("EMP-014", "松本 四男", "212000", effectiveDate),
            createEmployee("EMP-015", "井上 五男", "221000", effectiveDate),
            createEmployee("EMP-016", "木村 六男", "221000", effectiveDate),

            // コンサルティング事業（正社員6名）
            createEmployee("EMP-017", "林 一子", "311000", effectiveDate),
            createEmployee("EMP-018", "斎藤 二子", "311000", effectiveDate),
            createEmployee("EMP-019", "清水 三子", "311000", effectiveDate),
            createEmployee("EMP-020", "森 四子", "312000", effectiveDate),
            createEmployee("EMP-021", "池田 五子", "312000", effectiveDate),
            createEmployee("EMP-022", "橋本 六子", "312000", effectiveDate),

            // 経理・総務（正社員2名）
            createEmployee("EMP-023", "阿部 一郎", "000000", effectiveDate),
            createEmployee("EMP-024", "石川 二郎", "000000", effectiveDate)
        );

        employees.forEach(employeeRepository::save);
        if (LOG.isInfoEnabled()) {
            LOG.info("社員マスタ {}件 投入完了", employees.size());
        }
    }

    private Employee createEmployee(String code, String name, String departmentCode,
                                     LocalDate departmentStartDate) {
        return Employee.builder()
            .employeeCode(code)
            .employeeName(name)
            .departmentCode(departmentCode)
            .departmentStartDate(departmentStartDate)
            .build();
    }
}
