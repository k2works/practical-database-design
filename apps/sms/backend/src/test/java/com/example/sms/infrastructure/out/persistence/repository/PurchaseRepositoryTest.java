package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.application.port.out.PurchaseRepository;
import com.example.sms.application.port.out.ReceivingRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.domain.model.purchase.PurchaseDetail;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 仕入リポジトリテスト.
 */
@DisplayName("仕入リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class PurchaseRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Integer testReceivingId;

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        receivingRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM \"倉庫マスタ\"");
        jdbcTemplate.execute("DELETE FROM \"仕入先マスタ\"");
        partnerRepository.deleteAll();
        productRepository.deleteAll();

        // 取引先（仕入先）を登録
        var partner = Partner.builder()
                .partnerCode("S001")
                .partnerName("テスト仕入先")
                .isSupplier(true)
                .build();
        partnerRepository.save(partner);

        // 仕入先マスタに登録
        jdbcTemplate.update(
                "INSERT INTO \"仕入先マスタ\" (\"仕入先コード\", \"仕入先枝番\") VALUES (?, ?)",
                "S001", "00");

        // 倉庫マスタに登録
        jdbcTemplate.update(
                "INSERT INTO \"倉庫マスタ\" (\"倉庫コード\", \"倉庫名\") VALUES (?, ?)",
                "WH001", "メイン倉庫");

        // 商品を登録
        var product = Product.builder()
                .productCode("P001")
                .productName("テスト商品")
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(TaxCategory.EXCLUSIVE)
                .build();
        productRepository.save(product);

        // 発注を登録
        var purchaseOrder = PurchaseOrder.builder()
                .purchaseOrderNumber("PO-2025-0001")
                .supplierCode("S001")
                .supplierBranchNumber("00")
                .orderDate(LocalDate.of(2025, 1, 15))
                .desiredDeliveryDate(LocalDate.of(2025, 1, 31))
                .status(PurchaseOrderStatus.CONFIRMED)
                .totalAmount(new BigDecimal("50000"))
                .taxAmount(new BigDecimal("5000"))
                .createdBy("test-user")
                .updatedBy("test-user")
                .details(List.of(
                        PurchaseOrderDetail.builder()
                                .lineNumber(1)
                                .productCode("P001")
                                .orderQuantity(BigDecimal.TEN)
                                .unitPrice(new BigDecimal("5000"))
                                .orderAmount(new BigDecimal("50000"))
                                .expectedDeliveryDate(LocalDate.of(2025, 1, 31))
                                .build()
                ))
                .build();
        purchaseOrderRepository.save(purchaseOrder);

        // 入荷を登録
        var receiving = Receiving.builder()
                .receivingNumber("RCV-2025-0001")
                .purchaseOrderId(purchaseOrder.getId())
                .supplierCode("S001")
                .supplierBranchNumber("00")
                .receivingDate(LocalDate.of(2025, 1, 20))
                .status(ReceivingStatus.INSPECTION_COMPLETED)
                .warehouseCode("WH001")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        receivingRepository.save(receiving);
        testReceivingId = receiving.getId();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("仕入を登録できる")
        void canRegisterPurchase() {
            var purchase = Purchase.builder()
                    .purchaseNumber("PUR-2025-0001")
                    .receivingId(testReceivingId)
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .purchaseDate(LocalDate.of(2025, 1, 20))
                    .totalAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            purchaseRepository.save(purchase);

            var result = purchaseRepository.findByPurchaseNumber("PUR-2025-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getPurchaseNumber()).isEqualTo("PUR-2025-0001");
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("仕入明細付きで登録できる")
        void canRegisterPurchaseWithDetails() {
            var purchase = Purchase.builder()
                    .purchaseNumber("PUR-2025-0002")
                    .receivingId(testReceivingId)
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .purchaseDate(LocalDate.of(2025, 1, 20))
                    .totalAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            PurchaseDetail.builder()
                                    .lineNumber(1)
                                    .productCode("P001")
                                    .purchaseQuantity(BigDecimal.TEN)
                                    .unitPrice(new BigDecimal("5000"))
                                    .purchaseAmount(new BigDecimal("50000"))
                                    .build()
                    ))
                    .build();

            purchaseRepository.save(purchase);

            var result = purchaseRepository.findWithDetailsByPurchaseNumber("PUR-2025-0002");
            assertThat(result).isPresent();
            assertThat(result.get().getDetails()).hasSize(1);
            assertThat(result.get().getDetails().get(0).getProductCode()).isEqualTo("P001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("仕入先コードで検索できる")
        void canFindBySupplierCode() {
            var pur1 = createPurchase("PUR-2025-0001", LocalDate.of(2025, 1, 20));
            var pur2 = createPurchase("PUR-2025-0002", LocalDate.of(2025, 1, 25));
            purchaseRepository.save(pur1);
            purchaseRepository.save(pur2);

            var result = purchaseRepository.findBySupplierCode("S001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("仕入日範囲で検索できる")
        void canFindByPurchaseDateBetween() {
            var pur1 = createPurchase("PUR-2025-0001", LocalDate.of(2025, 1, 10));
            var pur2 = createPurchase("PUR-2025-0002", LocalDate.of(2025, 1, 20));
            var pur3 = createPurchase("PUR-2025-0003", LocalDate.of(2025, 2, 1));
            purchaseRepository.save(pur1);
            purchaseRepository.save(pur2);
            purchaseRepository.save(pur3);

            var result = purchaseRepository.findByPurchaseDateBetween(
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("入荷IDで検索できる")
        void canFindByReceivingId() {
            var pur1 = createPurchase("PUR-2025-0001", LocalDate.of(2025, 1, 20));
            purchaseRepository.save(pur1);

            var result = purchaseRepository.findByReceivingId(testReceivingId);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPurchaseNumber()).isEqualTo("PUR-2025-0001");
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var purchase = createPurchase("PUR-2025-0001", LocalDate.of(2025, 1, 20));
            purchaseRepository.save(purchase);

            var fetched = purchaseRepository.findByPurchaseNumber("PUR-2025-0001").get();
            fetched.setRemarks("更新テスト");
            purchaseRepository.update(fetched);

            var updated = purchaseRepository.findByPurchaseNumber("PUR-2025-0001").get();
            assertThat(updated.getRemarks()).isEqualTo("更新テスト");
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var purchase = createPurchase("PUR-2025-0002", LocalDate.of(2025, 1, 20));
            purchaseRepository.save(purchase);

            var purA = purchaseRepository.findByPurchaseNumber("PUR-2025-0002").get();
            var purB = purchaseRepository.findByPurchaseNumber("PUR-2025-0002").get();

            purA.setRemarks("更新A");
            purchaseRepository.update(purA);

            purB.setRemarks("更新B");
            assertThatThrownBy(() -> purchaseRepository.update(purB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var purchase = createPurchase("PUR-2025-0003", LocalDate.of(2025, 1, 20));
            purchaseRepository.save(purchase);

            var fetched = purchaseRepository.findByPurchaseNumber("PUR-2025-0003").get();
            purchaseRepository.deleteById(fetched.getId());

            fetched.setRemarks("更新テスト");
            assertThatThrownBy(() -> purchaseRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で仕入と仕入明細を取得できる")
        void canFetchPurchaseWithDetailsUsingJoin() {
            var purchase = Purchase.builder()
                    .purchaseNumber("PUR-2025-0010")
                    .receivingId(testReceivingId)
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .purchaseDate(LocalDate.of(2025, 1, 20))
                    .totalAmount(new BigDecimal("100000"))
                    .taxAmount(new BigDecimal("10000"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            PurchaseDetail.builder()
                                    .lineNumber(1)
                                    .productCode("P001")
                                    .purchaseQuantity(BigDecimal.TEN)
                                    .unitPrice(new BigDecimal("5000"))
                                    .purchaseAmount(new BigDecimal("50000"))
                                    .build(),
                            PurchaseDetail.builder()
                                    .lineNumber(2)
                                    .productCode("P001")
                                    .purchaseQuantity(BigDecimal.TEN)
                                    .unitPrice(new BigDecimal("5000"))
                                    .purchaseAmount(new BigDecimal("50000"))
                                    .build()
                    ))
                    .build();
            purchaseRepository.save(purchase);

            var result = purchaseRepository.findWithDetailsByPurchaseNumber("PUR-2025-0010");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getPurchaseNumber()).isEqualTo("PUR-2025-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("明細がない仕入も正しく取得できる")
        void canFetchPurchaseWithoutDetails() {
            var purchase = createPurchase("PUR-2025-0012", LocalDate.of(2025, 1, 20));
            purchaseRepository.save(purchase);

            var result = purchaseRepository.findWithDetailsByPurchaseNumber("PUR-2025-0012");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getPurchaseNumber()).isEqualTo("PUR-2025-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }

    private Purchase createPurchase(String purchaseNumber, LocalDate purchaseDate) {
        return Purchase.builder()
                .purchaseNumber(purchaseNumber)
                .receivingId(testReceivingId)
                .supplierCode("S001")
                .supplierBranchNumber("00")
                .purchaseDate(purchaseDate)
                .totalAmount(new BigDecimal("50000"))
                .taxAmount(new BigDecimal("5000"))
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
