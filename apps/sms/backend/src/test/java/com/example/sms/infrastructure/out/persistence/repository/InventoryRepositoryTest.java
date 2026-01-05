package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 在庫リポジトリテスト.
 */
@DisplayName("在庫リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class InventoryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 商品を参照するトランザクションデータを先に削除
        jdbcTemplate.execute("TRUNCATE TABLE \"入出庫履歴データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"棚卸明細データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"棚卸データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"仕入明細データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"仕入データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"入荷明細データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"入荷データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"発注明細データ\" CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE \"発注データ\" CASCADE");

        inventoryRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM \"ロケーションマスタ\"");
        jdbcTemplate.execute("DELETE FROM \"倉庫マスタ\"");
        productRepository.deleteAll();

        // 倉庫マスタに登録
        jdbcTemplate.update(
                "INSERT INTO \"倉庫マスタ\" (\"倉庫コード\", \"倉庫名\", \"倉庫区分\") VALUES (?, ?, ?::倉庫区分)",
                "WH001", "メイン倉庫", "自社");

        // 商品を登録
        var product = Product.builder()
                .productCode("P001")
                .productName("テスト商品")
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(TaxCategory.EXCLUSIVE)
                .build();
        productRepository.save(product);
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("在庫を登録できる")
        void canRegisterInventory() {
            var inventory = Inventory.builder()
                    .warehouseCode("WH001")
                    .productCode("P001")
                    .currentQuantity(new BigDecimal("100"))
                    .allocatedQuantity(BigDecimal.ZERO)
                    .orderedQuantity(BigDecimal.ZERO)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            inventoryRepository.save(inventory);

            var result = inventoryRepository.findByWarehouseAndProduct("WH001", "P001");
            assertThat(result).isPresent();
            assertThat(result.get().getCurrentQuantity()).isEqualByComparingTo(new BigDecimal("100"));
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("ロット番号付きで登録できる")
        void canRegisterInventoryWithLot() {
            var inventory = Inventory.builder()
                    .warehouseCode("WH001")
                    .productCode("P001")
                    .currentQuantity(new BigDecimal("50"))
                    .allocatedQuantity(BigDecimal.ZERO)
                    .orderedQuantity(BigDecimal.ZERO)
                    .lotNumber("LOT-2025-001")
                    .expirationDate(LocalDate.of(2025, 12, 31))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            inventoryRepository.save(inventory);

            var result = inventoryRepository.findByWarehouseAndProduct("WH001", "P001");
            assertThat(result).isPresent();
            assertThat(result.get().getLotNumber()).isEqualTo("LOT-2025-001");
            assertThat(result.get().getExpirationDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("倉庫コードで検索できる")
        void canFindByWarehouseCode() {
            var inv1 = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inv1);

            var result = inventoryRepository.findByWarehouseCode("WH001");
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("商品コードで検索できる")
        void canFindByProductCode() {
            var inv1 = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inv1);

            var result = inventoryRepository.findByProductCode("P001");
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getWarehouseCode()).isEqualTo("WH001");
        }
    }

    @Nested
    @DisplayName("在庫操作")
    class InventoryOperations {

        @Test
        @DisplayName("引当ができる")
        void canAllocate() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var saved = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            inventoryRepository.allocate(saved.getId(), new BigDecimal("30"));

            var result = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThat(result.getAllocatedQuantity()).isEqualByComparingTo(new BigDecimal("30"));
        }

        @Test
        @DisplayName("引当解除ができる")
        void canDeallocate() {
            var inventory = Inventory.builder()
                    .warehouseCode("WH001")
                    .productCode("P001")
                    .currentQuantity(new BigDecimal("100"))
                    .allocatedQuantity(new BigDecimal("30"))
                    .orderedQuantity(BigDecimal.ZERO)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();
            inventoryRepository.save(inventory);

            var saved = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            inventoryRepository.deallocate(saved.getId(), BigDecimal.TEN);

            var result = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThat(result.getAllocatedQuantity()).isEqualByComparingTo(new BigDecimal("20"));
        }

        @Test
        @DisplayName("入庫ができる")
        void canReceive() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var saved = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            inventoryRepository.receive(saved.getId(), new BigDecimal("50"), LocalDate.of(2025, 1, 20));

            var result = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThat(result.getCurrentQuantity()).isEqualByComparingTo(new BigDecimal("150"));
            assertThat(result.getLastReceiptDate()).isEqualTo(LocalDate.of(2025, 1, 20));
        }

        @Test
        @DisplayName("出庫ができる")
        void canShip() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var saved = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            inventoryRepository.ship(saved.getId(), new BigDecimal("30"), LocalDate.of(2025, 1, 20));

            var result = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThat(result.getCurrentQuantity()).isEqualByComparingTo(new BigDecimal("70"));
            assertThat(result.getLastShipmentDate()).isEqualTo(LocalDate.of(2025, 1, 20));
        }

        @Test
        @DisplayName("有効在庫不足時は引当に失敗する")
        void allocateFailsWhenInsufficientStock() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var saved = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThatThrownBy(() -> inventoryRepository.allocate(saved.getId(), new BigDecimal("150")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("引当に失敗しました");
        }

        @Test
        @DisplayName("在庫不足時は出庫に失敗する")
        void shipFailsWhenInsufficientStock() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var saved = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThatThrownBy(() -> inventoryRepository.ship(saved.getId(), new BigDecimal("150"), LocalDate.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("出庫に失敗しました");
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var fetched = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            fetched.setCurrentQuantity(new BigDecimal("150"));
            inventoryRepository.update(fetched);

            var updated = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            assertThat(updated.getCurrentQuantity()).isEqualByComparingTo(new BigDecimal("150"));
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var invA = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            var invB = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();

            invA.setCurrentQuantity(new BigDecimal("150"));
            inventoryRepository.update(invA);

            invB.setCurrentQuantity(new BigDecimal("200"));
            assertThatThrownBy(() -> inventoryRepository.update(invB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var inventory = createInventory("WH001", "P001", new BigDecimal("100"));
            inventoryRepository.save(inventory);

            var fetched = inventoryRepository.findByWarehouseAndProduct("WH001", "P001").get();
            inventoryRepository.deleteById(fetched.getId());

            fetched.setCurrentQuantity(new BigDecimal("150"));
            assertThatThrownBy(() -> inventoryRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    private Inventory createInventory(String warehouseCode, String productCode, BigDecimal quantity) {
        return Inventory.builder()
                .warehouseCode(warehouseCode)
                .productCode(productCode)
                .currentQuantity(quantity)
                .allocatedQuantity(BigDecimal.ZERO)
                .orderedQuantity(BigDecimal.ZERO)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
