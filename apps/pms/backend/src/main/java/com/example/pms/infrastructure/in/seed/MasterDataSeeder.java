package com.example.pms.infrastructure.in.seed;

import com.example.pms.application.port.out.BomRepository;
import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.LocationRepository;
import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.application.port.out.ProcessRouteRepository;
import com.example.pms.application.port.out.StaffRepository;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.application.port.out.UnitPriceRepository;
import com.example.pms.application.port.out.UnitRepository;
import com.example.pms.application.port.out.WarehouseRepository;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.defect.Defect;
import com.example.pms.domain.model.department.Department;
import com.example.pms.domain.model.inventory.Warehouse;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import com.example.pms.domain.model.process.Process;
import com.example.pms.domain.model.process.ProcessRoute;
import com.example.pms.domain.model.staff.Staff;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.domain.model.supplier.SupplierType;
import com.example.pms.domain.model.unit.Unit;
import com.example.pms.domain.model.unitprice.UnitPrice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * マスタデータ Seeder.
 * chapter31.md 準拠の E 社マスタデータを投入する。
 */
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.ExcessiveImports",
    "PMD.CouplingBetweenObjects",
    "PMD.GodClass",
    "PMD.BigIntegerInstantiation",
    "PMD.GuardLogStatement"
})
public class MasterDataSeeder {

    private static final LocalDate EFFECTIVE_DATE = LocalDate.of(2025, 1, 1);

    private final UnitRepository unitRepository;
    private final DepartmentRepository departmentRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProcessRepository processRepository;
    private final ItemRepository itemRepository;
    private final BomRepository bomRepository;
    private final ProcessRouteRepository processRouteRepository;
    private final StaffRepository staffRepository;
    private final UnitPriceRepository unitPriceRepository;
    private final DefectRepository defectRepository;

    /**
     * すべてのマスタデータを投入.
     */
    public void seedAll() {
        seedUnits();
        seedDepartments();
        seedSuppliers();
        seedLocations();
        seedWarehouses();
        seedProcesses();
        seedItems();
        seedBoms();
        seedProcessRoutes();
        seedStaff();
        seedUnitPrices();
        seedDefects();
    }

    /**
     * すべてのマスタデータを削除.
     */
    public void cleanAll() {
        unitPriceRepository.deleteAll();
        staffRepository.deleteAll();
        processRouteRepository.deleteAll();
        bomRepository.deleteAll();
        itemRepository.deleteAll();
        processRepository.deleteAll();
        warehouseRepository.deleteAll();
        locationRepository.deleteAll();
        supplierRepository.deleteAll();
        departmentRepository.deleteAll();
        defectRepository.deleteAll();
        unitRepository.deleteAll();
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedUnits() {
        log.info("単位マスタを投入中...");

        List<Unit> units = List.of(
            Unit.builder().unitCode("PCS").unitSymbol("個").unitName("個数").build(),
            Unit.builder().unitCode("KG").unitSymbol("kg").unitName("キログラム").build(),
            Unit.builder().unitCode("M").unitSymbol("m").unitName("メートル").build(),
            Unit.builder().unitCode("SET").unitSymbol("set").unitName("セット").build(),
            Unit.builder().unitCode("L").unitSymbol("L").unitName("リットル").build()
        );

        int count = 0;
        for (Unit unit : units) {
            if (unitRepository.findByUnitCode(unit.getUnitCode()).isEmpty()) {
                unitRepository.save(unit);
                count++;
            }
        }
        log.info("単位マスタ {}件 投入完了", count);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedDepartments() {
        log.info("部門マスタを投入中...");

        List<Department> departments = List.of(
            Department.builder().departmentCode("SALES").departmentName("営業部")
                .departmentPath("E精密工業/営業部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build(),
            Department.builder().departmentCode("PROD-PLAN").departmentName("生産管理部")
                .departmentPath("E精密工業/生産管理部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build(),
            Department.builder().departmentCode("MFG").departmentName("製造部")
                .departmentPath("E精密工業/製造部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build(),
            Department.builder().departmentCode("QUALITY").departmentName("品質管理部")
                .departmentPath("E精密工業/品質管理部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build(),
            Department.builder().departmentCode("PURCHASE").departmentName("購買部")
                .departmentPath("E精密工業/購買部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build(),
            Department.builder().departmentCode("WAREHOUSE").departmentName("倉庫部")
                .departmentPath("E精密工業/倉庫部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build(),
            Department.builder().departmentCode("OUTSOURCE").departmentName("外注管理部")
                .departmentPath("E精密工業/外注管理部").lowestLevel(true).validFrom(EFFECTIVE_DATE).build()
        );

        int count = 0;
        for (Department dept : departments) {
            if (departmentRepository.findByDepartmentCode(dept.getDepartmentCode()).isEmpty()) {
                departmentRepository.save(dept);
                count++;
            }
        }
        log.info("部門マスタ {}件 投入完了", count);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedSuppliers() {
        log.info("取引先マスタを投入中...");

        List<Supplier> suppliers = List.of(
            // 仕入先
            Supplier.builder().supplierCode("SUP-001").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("東京スチール株式会社").supplierNameKana("トウキョウスチール")
                .supplierType(SupplierType.VENDOR).build(),
            Supplier.builder().supplierCode("SUP-002").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("大阪金属工業").supplierNameKana("オオサカキンゾクコウギョウ")
                .supplierType(SupplierType.VENDOR).build(),
            Supplier.builder().supplierCode("SUP-003").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("名古屋ベアリング").supplierNameKana("ナゴヤベアリング")
                .supplierType(SupplierType.VENDOR).build(),
            Supplier.builder().supplierCode("SUP-004").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("横浜部品センター").supplierNameKana("ヨコハマブヒンセンター")
                .supplierType(SupplierType.VENDOR).build(),
            Supplier.builder().supplierCode("SUP-005").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("神戸包装資材").supplierNameKana("コウベホウソウシザイ")
                .supplierType(SupplierType.VENDOR).build(),
            // 外注先
            Supplier.builder().supplierCode("OUT-001").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("メッキ工業所").supplierNameKana("メッキコウギョウショ")
                .supplierType(SupplierType.SUBCONTRACTOR).build(),
            Supplier.builder().supplierCode("OUT-002").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("熱処理センター").supplierNameKana("ネツショリセンター")
                .supplierType(SupplierType.SUBCONTRACTOR).build(),
            // 得意先
            Supplier.builder().supplierCode("CUS-001").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("機械メーカーA社").supplierNameKana("キカイメーカーエーシャ")
                .supplierType(SupplierType.CUSTOMER).build(),
            Supplier.builder().supplierCode("CUS-002").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("産業機器B社").supplierNameKana("サンギョウキキビーシャ")
                .supplierType(SupplierType.CUSTOMER).build(),
            Supplier.builder().supplierCode("CUS-003").effectiveFrom(EFFECTIVE_DATE)
                .supplierName("精密機械C社").supplierNameKana("セイミツキカイシーシャ")
                .supplierType(SupplierType.CUSTOMER).build()
        );

        int count = 0;
        for (Supplier supplier : suppliers) {
            if (supplierRepository.findBySupplierCode(supplier.getSupplierCode()).isEmpty()) {
                supplierRepository.save(supplier);
                count++;
            }
        }
        log.info("取引先マスタ {}件 投入完了", count);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedLocations() {
        log.info("場所マスタを投入中...");

        List<Location> locations = List.of(
            Location.builder().locationCode("FACTORY").locationName("本社工場")
                .locationType(LocationType.MANUFACTURING).build(),
            Location.builder().locationCode("LINE1").locationName("製造ライン1")
                .locationType(LocationType.MANUFACTURING).parentLocationCode("FACTORY").build(),
            Location.builder().locationCode("LINE2").locationName("製造ライン2")
                .locationType(LocationType.MANUFACTURING).parentLocationCode("FACTORY").build(),
            Location.builder().locationCode("WH-MAT").locationName("原材料倉庫")
                .locationType(LocationType.WAREHOUSE).build(),
            Location.builder().locationCode("WH-PART").locationName("部品倉庫")
                .locationType(LocationType.WAREHOUSE).build(),
            Location.builder().locationCode("WH-PROD").locationName("製品倉庫")
                .locationType(LocationType.WAREHOUSE).build(),
            Location.builder().locationCode("INSPECT").locationName("検査エリア")
                .locationType(LocationType.INSPECTION).parentLocationCode("FACTORY").build(),
            Location.builder().locationCode("SHIP").locationName("出荷エリア")
                .locationType(LocationType.SHIPPING).build()
        );

        int count = 0;
        for (Location location : locations) {
            if (locationRepository.findByLocationCode(location.getLocationCode()).isEmpty()) {
                locationRepository.save(location);
                count++;
            }
        }
        log.info("場所マスタ {}件 投入完了", count);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedWarehouses() {
        log.info("倉庫マスタを投入中...");

        List<Warehouse> warehouses = List.of(
            Warehouse.builder().warehouseCode("WH-MAT").warehouseName("原材料倉庫")
                .warehouseType("材料").departmentCode("WAREHOUSE").build(),
            Warehouse.builder().warehouseCode("WH-PART").warehouseName("部品倉庫")
                .warehouseType("部品").departmentCode("WAREHOUSE").build(),
            Warehouse.builder().warehouseCode("WH-PROD").warehouseName("製品倉庫")
                .warehouseType("製品").departmentCode("WAREHOUSE").build()
        );

        int count = 0;
        for (Warehouse warehouse : warehouses) {
            if (warehouseRepository.findByWarehouseCode(warehouse.getWarehouseCode()).isEmpty()) {
                warehouseRepository.save(warehouse);
                count++;
            }
        }
        log.info("倉庫マスタ {}件 投入完了", count);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedProcesses() {
        log.info("工程マスタを投入中...");

        List<Process> processes = List.of(
            // 切削工程
            Process.builder().processCode("LATHE").processName("旋盤加工")
                .processType("加工").locationCode("LINE1").build(),
            Process.builder().processCode("MILL").processName("フライス加工")
                .processType("加工").locationCode("LINE1").build(),
            Process.builder().processCode("GRIND").processName("研削加工")
                .processType("加工").locationCode("LINE1").build(),
            Process.builder().processCode("HOB").processName("ホブ切り")
                .processType("加工").locationCode("LINE1").build(),
            Process.builder().processCode("DRILL").processName("穴あけ加工")
                .processType("加工").locationCode("LINE1").build(),
            // 組立工程
            Process.builder().processCode("ASM").processName("組立")
                .processType("組立").locationCode("LINE1").build(),
            Process.builder().processCode("FINAL-ASM").processName("最終組立")
                .processType("組立").locationCode("LINE1").build(),
            // 検査工程
            Process.builder().processCode("INS-PROC").processName("工程検査")
                .processType("検査").locationCode("INSPECT").build(),
            Process.builder().processCode("INS-SHIP").processName("出荷検査")
                .processType("検査").locationCode("INSPECT").build(),
            Process.builder().processCode("INS-RCV").processName("受入検査")
                .processType("検査").locationCode("INSPECT").build(),
            // 外注工程
            Process.builder().processCode("OUT-MEKI").processName("メッキ処理")
                .processType("外注").locationCode("FACTORY").build(),
            Process.builder().processCode("OUT-HEAT").processName("熱処理")
                .processType("外注").locationCode("FACTORY").build()
        );

        int count = 0;
        for (Process process : processes) {
            if (processRepository.findByProcessCode(process.getProcessCode()).isEmpty()) {
                processRepository.save(process);
                count++;
            }
        }
        log.info("工程マスタ {}件 投入完了", count);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedItems() {
        log.info("品目マスタを投入中...");

        List<Item> items = List.of(
            // 製品
            Item.builder().itemCode("PROD-A001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("精密シャフトA").itemCategory(ItemCategory.PRODUCT)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("100")).build(),
            Item.builder().itemCode("PROD-B001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("ギアボックスアセンブリ").itemCategory(ItemCategory.PRODUCT)
                .unitCode("PCS").leadTime(14).safetyStock(new BigDecimal("50")).build(),
            Item.builder().itemCode("PROD-C001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("精密プレート").itemCategory(ItemCategory.PRODUCT)
                .unitCode("PCS").leadTime(5).safetyStock(new BigDecimal("80")).build(),

            // 半製品
            Item.builder().itemCode("SEMI-A001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("加工済みシャフト").itemCategory(ItemCategory.SEMI_PRODUCT)
                .unitCode("PCS").leadTime(5).safetyStock(new BigDecimal("120")).build(),
            Item.builder().itemCode("SEMI-B001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("ギアボックス本体").itemCategory(ItemCategory.SEMI_PRODUCT)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("60")).build(),
            Item.builder().itemCode("SEMI-B002").effectiveFrom(EFFECTIVE_DATE)
                .itemName("駆動ギア").itemCategory(ItemCategory.SEMI_PRODUCT)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("80")).build(),
            Item.builder().itemCode("SEMI-B003").effectiveFrom(EFFECTIVE_DATE)
                .itemName("従動ギア").itemCategory(ItemCategory.SEMI_PRODUCT)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("80")).build(),
            Item.builder().itemCode("SEMI-C001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("加工済みプレート").itemCategory(ItemCategory.SEMI_PRODUCT)
                .unitCode("PCS").leadTime(3).safetyStock(new BigDecimal("100")).build(),

            // 部品
            Item.builder().itemCode("PART-001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("ベアリング 6205").itemCategory(ItemCategory.PART)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("100")).build(),
            Item.builder().itemCode("PART-002").effectiveFrom(EFFECTIVE_DATE)
                .itemName("オイルシール φ20").itemCategory(ItemCategory.PART)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("100")).build(),
            Item.builder().itemCode("PART-003").effectiveFrom(EFFECTIVE_DATE)
                .itemName("標準シャフト φ10").itemCategory(ItemCategory.PART)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("50")).build(),
            Item.builder().itemCode("PART-004").effectiveFrom(EFFECTIVE_DATE)
                .itemName("オイルシール φ30").itemCategory(ItemCategory.PART)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("100")).build(),
            Item.builder().itemCode("PART-005").effectiveFrom(EFFECTIVE_DATE)
                .itemName("ボルトセット M6").itemCategory(ItemCategory.PART)
                .unitCode("SET").leadTime(3).safetyStock(new BigDecimal("200")).build(),
            Item.builder().itemCode("PART-006").effectiveFrom(EFFECTIVE_DATE)
                .itemName("ワッシャーセット").itemCategory(ItemCategory.PART)
                .unitCode("SET").leadTime(3).safetyStock(new BigDecimal("200")).build(),
            Item.builder().itemCode("PART-007").effectiveFrom(EFFECTIVE_DATE)
                .itemName("Oリング φ25").itemCategory(ItemCategory.PART)
                .unitCode("PCS").leadTime(7).safetyStock(new BigDecimal("150")).build(),
            Item.builder().itemCode("PART-008").effectiveFrom(EFFECTIVE_DATE)
                .itemName("ピン φ3").itemCategory(ItemCategory.PART)
                .unitCode("PCS").leadTime(3).safetyStock(new BigDecimal("300")).build(),

            // 材料
            Item.builder().itemCode("MAT-001").effectiveFrom(EFFECTIVE_DATE)
                .itemName("丸棒材 SUS304 φ20").itemCategory(ItemCategory.MATERIAL)
                .unitCode("KG").leadTime(14).safetyStock(new BigDecimal("500")).build(),
            Item.builder().itemCode("MAT-002").effectiveFrom(EFFECTIVE_DATE)
                .itemName("アルミダイキャスト素材").itemCategory(ItemCategory.MATERIAL)
                .unitCode("PCS").leadTime(21).safetyStock(new BigDecimal("100")).build(),
            Item.builder().itemCode("MAT-003").effectiveFrom(EFFECTIVE_DATE)
                .itemName("歯車用素材 SCM415").itemCategory(ItemCategory.MATERIAL)
                .unitCode("KG").leadTime(14).safetyStock(new BigDecimal("300")).build(),
            Item.builder().itemCode("MAT-004").effectiveFrom(EFFECTIVE_DATE)
                .itemName("鋼板 SS400 t3").itemCategory(ItemCategory.MATERIAL)
                .unitCode("KG").leadTime(14).safetyStock(new BigDecimal("200")).build(),
            Item.builder().itemCode("MAT-005").effectiveFrom(EFFECTIVE_DATE)
                .itemName("丸棒材 S45C φ15").itemCategory(ItemCategory.MATERIAL)
                .unitCode("KG").leadTime(14).safetyStock(new BigDecimal("400")).build(),
            Item.builder().itemCode("MAT-006").effectiveFrom(EFFECTIVE_DATE)
                .itemName("真鍮丸棒 C3604 φ10").itemCategory(ItemCategory.MATERIAL)
                .unitCode("KG").leadTime(21).safetyStock(new BigDecimal("100")).build(),
            Item.builder().itemCode("MAT-010").effectiveFrom(EFFECTIVE_DATE)
                .itemName("包装材セット").itemCategory(ItemCategory.MATERIAL)
                .unitCode("SET").leadTime(3).safetyStock(new BigDecimal("500")).build(),
            Item.builder().itemCode("MAT-011").effectiveFrom(EFFECTIVE_DATE)
                .itemName("防錆紙").itemCategory(ItemCategory.MATERIAL)
                .unitCode("SET").leadTime(3).safetyStock(new BigDecimal("300")).build(),
            Item.builder().itemCode("MAT-012").effectiveFrom(EFFECTIVE_DATE)
                .itemName("段ボール箱").itemCategory(ItemCategory.MATERIAL)
                .unitCode("PCS").leadTime(3).safetyStock(new BigDecimal("200")).build()
        );

        int count = 0;
        for (Item item : items) {
            if (itemRepository.findByItemCode(item.getItemCode()).isEmpty()) {
                itemRepository.save(item);
                count++;
            }
        }
        log.info("品目マスタ {}件 投入完了", count);
    }

    private void seedBoms() {
        log.info("BOMを投入中...");

        // 既存データがあればスキップ
        if (!bomRepository.findByParentItemCode("PROD-A001").isEmpty()) {
            log.info("BOM は既に存在します。スキップします。");
            return;
        }

        List<Bom> boms = List.of(
            // 精密シャフトA の構成
            Bom.builder().parentItemCode("PROD-A001").childItemCode("SEMI-A001")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).defectRate(new BigDecimal("0.02")).sequence(1).build(),
            Bom.builder().parentItemCode("PROD-A001").childItemCode("PART-001")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(2).build(),
            Bom.builder().parentItemCode("PROD-A001").childItemCode("PART-002")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(3).build(),
            Bom.builder().parentItemCode("PROD-A001").childItemCode("MAT-010")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(4).build(),

            // 加工済みシャフト の構成
            Bom.builder().parentItemCode("SEMI-A001").childItemCode("MAT-001")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(new BigDecimal("0.5")).defectRate(new BigDecimal("0.05")).sequence(1).build(),

            // ギアボックスアセンブリ の構成
            Bom.builder().parentItemCode("PROD-B001").childItemCode("SEMI-B001")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(1).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("SEMI-B002")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(2).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("SEMI-B003")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(3).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("PART-003")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(4).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("PART-001")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(new BigDecimal("2")).sequence(5).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("PART-004")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(6).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("PART-005")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(7).build(),
            Bom.builder().parentItemCode("PROD-B001").childItemCode("MAT-010")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(8).build(),

            // ギアボックス本体 の構成
            Bom.builder().parentItemCode("SEMI-B001").childItemCode("MAT-002")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).defectRate(new BigDecimal("0.03")).sequence(1).build(),

            // 駆動ギア の構成
            Bom.builder().parentItemCode("SEMI-B002").childItemCode("MAT-003")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(new BigDecimal("0.8")).defectRate(new BigDecimal("0.05")).sequence(1).build(),

            // 従動ギア の構成
            Bom.builder().parentItemCode("SEMI-B003").childItemCode("MAT-003")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(new BigDecimal("0.6")).defectRate(new BigDecimal("0.05")).sequence(1).build(),

            // 精密プレート の構成
            Bom.builder().parentItemCode("PROD-C001").childItemCode("SEMI-C001")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(1).build(),
            Bom.builder().parentItemCode("PROD-C001").childItemCode("MAT-010")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(BigDecimal.ONE).sequence(2).build(),

            // 加工済みプレート の構成
            Bom.builder().parentItemCode("SEMI-C001").childItemCode("MAT-004")
                .effectiveFrom(EFFECTIVE_DATE).baseQuantity(BigDecimal.ONE)
                .requiredQuantity(new BigDecimal("2.5")).defectRate(new BigDecimal("0.02")).sequence(1).build()
        );

        boms.forEach(bomRepository::save);
        log.info("BOM {}件 投入完了", boms.size());
    }

    private void seedProcessRoutes() {
        log.info("工程表を投入中...");

        // 既存データがあればスキップ
        if (!processRouteRepository.findByItemCode("PROD-A001").isEmpty()) {
            log.info("工程表は既に存在します。スキップします。");
            return;
        }

        List<ProcessRoute> routes = List.of(
            // 精密シャフトA
            ProcessRoute.builder().itemCode("PROD-A001").sequence(1).processCode("ASM").standardTime(new BigDecimal("60")).setupTime(new BigDecimal("15")).build(),
            ProcessRoute.builder().itemCode("PROD-A001").sequence(2).processCode("INS-SHIP").standardTime(new BigDecimal("30")).setupTime(new BigDecimal("5")).build(),

            // 加工済みシャフト
            ProcessRoute.builder().itemCode("SEMI-A001").sequence(1).processCode("LATHE").standardTime(new BigDecimal("120")).setupTime(new BigDecimal("30")).build(),
            ProcessRoute.builder().itemCode("SEMI-A001").sequence(2).processCode("GRIND").standardTime(new BigDecimal("60")).setupTime(new BigDecimal("15")).build(),
            ProcessRoute.builder().itemCode("SEMI-A001").sequence(3).processCode("OUT-MEKI").standardTime(BigDecimal.ZERO).setupTime(BigDecimal.ZERO).build(),
            ProcessRoute.builder().itemCode("SEMI-A001").sequence(4).processCode("INS-PROC").standardTime(new BigDecimal("30")).setupTime(new BigDecimal("5")).build(),

            // ギアボックスアセンブリ
            ProcessRoute.builder().itemCode("PROD-B001").sequence(1).processCode("FINAL-ASM").standardTime(new BigDecimal("120")).setupTime(new BigDecimal("20")).build(),
            ProcessRoute.builder().itemCode("PROD-B001").sequence(2).processCode("INS-SHIP").standardTime(new BigDecimal("30")).setupTime(new BigDecimal("5")).build(),

            // ギアボックス本体
            ProcessRoute.builder().itemCode("SEMI-B001").sequence(1).processCode("MILL").standardTime(new BigDecimal("90")).setupTime(new BigDecimal("30")).build(),
            ProcessRoute.builder().itemCode("SEMI-B001").sequence(2).processCode("DRILL").standardTime(new BigDecimal("45")).setupTime(new BigDecimal("10")).build(),

            // 駆動ギア
            ProcessRoute.builder().itemCode("SEMI-B002").sequence(1).processCode("LATHE").standardTime(new BigDecimal("60")).setupTime(new BigDecimal("20")).build(),
            ProcessRoute.builder().itemCode("SEMI-B002").sequence(2).processCode("HOB").standardTime(new BigDecimal("90")).setupTime(new BigDecimal("30")).build(),
            ProcessRoute.builder().itemCode("SEMI-B002").sequence(3).processCode("OUT-HEAT").standardTime(BigDecimal.ZERO).setupTime(BigDecimal.ZERO).build(),
            ProcessRoute.builder().itemCode("SEMI-B002").sequence(4).processCode("GRIND").standardTime(new BigDecimal("30")).setupTime(new BigDecimal("10")).build(),

            // 従動ギア
            ProcessRoute.builder().itemCode("SEMI-B003").sequence(1).processCode("LATHE").standardTime(new BigDecimal("45")).setupTime(new BigDecimal("15")).build(),
            ProcessRoute.builder().itemCode("SEMI-B003").sequence(2).processCode("HOB").standardTime(new BigDecimal("75")).setupTime(new BigDecimal("25")).build(),
            ProcessRoute.builder().itemCode("SEMI-B003").sequence(3).processCode("OUT-HEAT").standardTime(BigDecimal.ZERO).setupTime(BigDecimal.ZERO).build(),
            ProcessRoute.builder().itemCode("SEMI-B003").sequence(4).processCode("GRIND").standardTime(new BigDecimal("25")).setupTime(new BigDecimal("8")).build(),

            // 精密プレート
            ProcessRoute.builder().itemCode("PROD-C001").sequence(1).processCode("ASM").standardTime(new BigDecimal("30")).setupTime(new BigDecimal("10")).build(),
            ProcessRoute.builder().itemCode("PROD-C001").sequence(2).processCode("INS-SHIP").standardTime(new BigDecimal("15")).setupTime(new BigDecimal("5")).build(),

            // 加工済みプレート
            ProcessRoute.builder().itemCode("SEMI-C001").sequence(1).processCode("MILL").standardTime(new BigDecimal("60")).setupTime(new BigDecimal("20")).build(),
            ProcessRoute.builder().itemCode("SEMI-C001").sequence(2).processCode("DRILL").standardTime(new BigDecimal("30")).setupTime(new BigDecimal("10")).build()
        );

        routes.forEach(processRouteRepository::save);
        log.info("工程表 {}件 投入完了", routes.size());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedStaff() {
        log.info("担当者マスタを投入中...");

        List<Staff> staffList = List.of(
            Staff.builder().staffCode("EMP-001").effectiveFrom(EFFECTIVE_DATE)
                .staffName("田中 太郎").departmentCode("MFG").email("tanaka@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-002").effectiveFrom(EFFECTIVE_DATE)
                .staffName("鈴木 一郎").departmentCode("MFG").email("suzuki@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-003").effectiveFrom(EFFECTIVE_DATE)
                .staffName("佐藤 次郎").departmentCode("MFG").email("sato@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-004").effectiveFrom(EFFECTIVE_DATE)
                .staffName("高橋 三郎").departmentCode("MFG").email("takahashi@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-005").effectiveFrom(EFFECTIVE_DATE)
                .staffName("伊藤 四郎").departmentCode("MFG").email("ito@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-006").effectiveFrom(EFFECTIVE_DATE)
                .staffName("渡辺 五郎").departmentCode("QUALITY").email("watanabe@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-007").effectiveFrom(EFFECTIVE_DATE)
                .staffName("山本 花子").departmentCode("QUALITY").email("yamamoto@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-008").effectiveFrom(EFFECTIVE_DATE)
                .staffName("中村 美咲").departmentCode("PROD-PLAN").email("nakamura@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-009").effectiveFrom(EFFECTIVE_DATE)
                .staffName("小林 健一").departmentCode("PURCHASE").email("kobayashi@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-010").effectiveFrom(EFFECTIVE_DATE)
                .staffName("加藤 正").departmentCode("WAREHOUSE").email("kato@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-011").effectiveFrom(EFFECTIVE_DATE)
                .staffName("吉田 誠").departmentCode("OUTSOURCE").email("yoshida@eprecision.co.jp").build(),
            Staff.builder().staffCode("EMP-012").effectiveFrom(EFFECTIVE_DATE)
                .staffName("山田 浩二").departmentCode("SALES").email("yamada@eprecision.co.jp").build()
        );

        int count = 0;
        for (Staff staff : staffList) {
            if (staffRepository.findByStaffCode(staff.getStaffCode()).isEmpty()) {
                staffRepository.save(staff);
                count++;
            }
        }
        log.info("担当者マスタ {}件 投入完了", count);
    }

    private void seedUnitPrices() {
        log.info("単価マスタを投入中...");

        // 既存データがあればスキップ
        if (unitPriceRepository.findByItemCodeAndSupplierCode("MAT-001", "SUP-001").isPresent()) {
            log.info("単価マスタは既に存在します。スキップします。");
            return;
        }

        List<UnitPrice> unitPrices = List.of(
            // 材料の仕入単価
            UnitPrice.builder().itemCode("MAT-001").supplierCode("SUP-001")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("1500")).build(),
            UnitPrice.builder().itemCode("MAT-002").supplierCode("SUP-002")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("3500")).build(),
            UnitPrice.builder().itemCode("MAT-003").supplierCode("SUP-002")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("2000")).build(),
            UnitPrice.builder().itemCode("MAT-004").supplierCode("SUP-001")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("800")).build(),
            UnitPrice.builder().itemCode("MAT-005").supplierCode("SUP-001")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("1200")).build(),
            UnitPrice.builder().itemCode("MAT-006").supplierCode("SUP-002")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("2500")).build(),
            UnitPrice.builder().itemCode("MAT-010").supplierCode("SUP-005")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("150")).build(),

            // 部品の仕入単価
            UnitPrice.builder().itemCode("PART-001").supplierCode("SUP-003")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("850")).build(),
            UnitPrice.builder().itemCode("PART-002").supplierCode("SUP-003")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("120")).build(),
            UnitPrice.builder().itemCode("PART-003").supplierCode("SUP-004")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("450")).build(),
            UnitPrice.builder().itemCode("PART-004").supplierCode("SUP-003")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("180")).build(),
            UnitPrice.builder().itemCode("PART-005").supplierCode("SUP-004")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("80")).build(),

            // 外注加工単価
            UnitPrice.builder().itemCode("SEMI-A001").supplierCode("OUT-001")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("500")).build(),
            UnitPrice.builder().itemCode("SEMI-B002").supplierCode("OUT-002")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("300")).build(),
            UnitPrice.builder().itemCode("SEMI-B003").supplierCode("OUT-002")
                .effectiveFrom(EFFECTIVE_DATE).price(new BigDecimal("280")).build()
        );

        unitPrices.forEach(unitPriceRepository::save);
        log.info("単価マスタ {}件 投入完了", unitPrices.size());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedDefects() {
        log.info("欠点マスタを投入中...");

        List<Defect> defects = List.of(
            Defect.builder().defectCode("DEF-001").defectName("寸法不良").defectCategory("加工不良").build(),
            Defect.builder().defectCode("DEF-002").defectName("表面傷").defectCategory("外観不良").build(),
            Defect.builder().defectCode("DEF-003").defectName("メッキ不良").defectCategory("表面処理不良").build(),
            Defect.builder().defectCode("DEF-004").defectName("熱処理不良").defectCategory("熱処理不良").build(),
            Defect.builder().defectCode("DEF-005").defectName("組立不良").defectCategory("組立不良").build(),
            Defect.builder().defectCode("DEF-006").defectName("材料不良").defectCategory("材料不良").build()
        );

        int count = 0;
        for (Defect defect : defects) {
            if (defectRepository.findByDefectCode(defect.getDefectCode()).isEmpty()) {
                defectRepository.save(defect);
                count++;
            }
        }
        log.info("欠点マスタ {}件 投入完了", count);
    }
}
