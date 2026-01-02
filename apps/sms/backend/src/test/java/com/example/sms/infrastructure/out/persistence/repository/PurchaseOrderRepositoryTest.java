package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
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
 * 発注リポジトリテスト.
 */
@DisplayName("発注リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class PurchaseOrderRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        purchaseOrderRepository.deleteAll();
        // 仕入先マスタをクリア
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
        @DisplayName("発注を登録できる")
        void canRegisterPurchaseOrder() {
            var purchaseOrder = PurchaseOrder.builder()
                    .purchaseOrderNumber("PO-2025-0001")
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .orderDate(LocalDate.of(2025, 1, 15))
                    .desiredDeliveryDate(LocalDate.of(2025, 1, 31))
                    .status(PurchaseOrderStatus.DRAFT)
                    .totalAmount(new BigDecimal("100000"))
                    .taxAmount(new BigDecimal("10000"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            purchaseOrderRepository.save(purchaseOrder);

            var result = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getPurchaseOrderNumber()).isEqualTo("PO-2025-0001");
            assertThat(result.get().getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT);
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("発注明細付きで登録できる")
        void canRegisterPurchaseOrderWithDetails() {
            var purchaseOrder = PurchaseOrder.builder()
                    .purchaseOrderNumber("PO-2025-0002")
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .orderDate(LocalDate.of(2025, 1, 15))
                    .desiredDeliveryDate(LocalDate.of(2025, 1, 31))
                    .status(PurchaseOrderStatus.DRAFT)
                    .totalAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            PurchaseOrderDetail.builder()
                                    .lineNumber(1)
                                    .productCode("P001")
                                    .orderQuantity(new BigDecimal("10"))
                                    .unitPrice(new BigDecimal("5000"))
                                    .orderAmount(new BigDecimal("50000"))
                                    .expectedDeliveryDate(LocalDate.of(2025, 1, 31))
                                    .build()
                    ))
                    .build();

            purchaseOrderRepository.save(purchaseOrder);

            var result = purchaseOrderRepository.findWithDetailsByPurchaseOrderNumber("PO-2025-0002");
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
            var po1 = createPurchaseOrder("PO-2025-0001", LocalDate.of(2025, 1, 15));
            var po2 = createPurchaseOrder("PO-2025-0002", LocalDate.of(2025, 1, 20));
            purchaseOrderRepository.save(po1);
            purchaseOrderRepository.save(po2);

            var result = purchaseOrderRepository.findBySupplierCode("S001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            var po1 = createPurchaseOrder("PO-2025-0001", LocalDate.of(2025, 1, 15));
            po1.setStatus(PurchaseOrderStatus.CONFIRMED);
            var po2 = createPurchaseOrder("PO-2025-0002", LocalDate.of(2025, 1, 20));
            po2.setStatus(PurchaseOrderStatus.DRAFT);
            purchaseOrderRepository.save(po1);
            purchaseOrderRepository.save(po2);

            var result = purchaseOrderRepository.findByStatus(PurchaseOrderStatus.CONFIRMED);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPurchaseOrderNumber()).isEqualTo("PO-2025-0001");
        }

        @Test
        @DisplayName("発注日範囲で検索できる")
        void canFindByOrderDateBetween() {
            var po1 = createPurchaseOrder("PO-2025-0001", LocalDate.of(2025, 1, 10));
            var po2 = createPurchaseOrder("PO-2025-0002", LocalDate.of(2025, 1, 20));
            var po3 = createPurchaseOrder("PO-2025-0003", LocalDate.of(2025, 2, 1));
            purchaseOrderRepository.save(po1);
            purchaseOrderRepository.save(po2);
            purchaseOrderRepository.save(po3);

            var result = purchaseOrderRepository.findByOrderDateBetween(
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var purchaseOrder = createPurchaseOrder("PO-2025-0001", LocalDate.of(2025, 1, 15));
            purchaseOrderRepository.save(purchaseOrder);

            var fetched = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-0001").get();
            fetched.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrderRepository.update(fetched);

            var updated = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-0001").get();
            assertThat(updated.getStatus()).isEqualTo(PurchaseOrderStatus.CONFIRMED);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var purchaseOrder = createPurchaseOrder("PO-2025-0002", LocalDate.of(2025, 1, 15));
            purchaseOrderRepository.save(purchaseOrder);

            var poA = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-0002").get();
            var poB = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-0002").get();

            poA.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrderRepository.update(poA);

            poB.setStatus(PurchaseOrderStatus.CANCELLED);
            assertThatThrownBy(() -> purchaseOrderRepository.update(poB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var purchaseOrder = createPurchaseOrder("PO-2025-0003", LocalDate.of(2025, 1, 15));
            purchaseOrderRepository.save(purchaseOrder);

            var fetched = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-0003").get();
            purchaseOrderRepository.deleteById(fetched.getId());

            fetched.setStatus(PurchaseOrderStatus.CONFIRMED);
            assertThatThrownBy(() -> purchaseOrderRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で発注と発注明細を取得できる")
        void canFetchPurchaseOrderWithDetailsUsingJoin() {
            var purchaseOrder = PurchaseOrder.builder()
                    .purchaseOrderNumber("PO-2025-0010")
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .orderDate(LocalDate.of(2025, 1, 15))
                    .desiredDeliveryDate(LocalDate.of(2025, 1, 31))
                    .status(PurchaseOrderStatus.DRAFT)
                    .totalAmount(new BigDecimal("100000"))
                    .taxAmount(new BigDecimal("10000"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            PurchaseOrderDetail.builder()
                                    .lineNumber(1)
                                    .productCode("P001")
                                    .orderQuantity(new BigDecimal("10"))
                                    .unitPrice(new BigDecimal("5000"))
                                    .orderAmount(new BigDecimal("50000"))
                                    .expectedDeliveryDate(LocalDate.of(2025, 1, 31))
                                    .build(),
                            PurchaseOrderDetail.builder()
                                    .lineNumber(2)
                                    .productCode("P001")
                                    .orderQuantity(new BigDecimal("10"))
                                    .unitPrice(new BigDecimal("5000"))
                                    .orderAmount(new BigDecimal("50000"))
                                    .expectedDeliveryDate(LocalDate.of(2025, 1, 31))
                                    .build()
                    ))
                    .build();
            purchaseOrderRepository.save(purchaseOrder);

            var result = purchaseOrderRepository.findWithDetailsByPurchaseOrderNumber("PO-2025-0010");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getPurchaseOrderNumber()).isEqualTo("PO-2025-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("明細がない発注も正しく取得できる")
        void canFetchPurchaseOrderWithoutDetails() {
            var purchaseOrder = createPurchaseOrder("PO-2025-0012", LocalDate.of(2025, 1, 15));
            purchaseOrderRepository.save(purchaseOrder);

            var result = purchaseOrderRepository.findWithDetailsByPurchaseOrderNumber("PO-2025-0012");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getPurchaseOrderNumber()).isEqualTo("PO-2025-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }

    private PurchaseOrder createPurchaseOrder(String purchaseOrderNumber, LocalDate orderDate) {
        return PurchaseOrder.builder()
                .purchaseOrderNumber(purchaseOrderNumber)
                .supplierCode("S001")
                .supplierBranchNumber("00")
                .orderDate(orderDate)
                .desiredDeliveryDate(orderDate.plusDays(14))
                .status(PurchaseOrderStatus.DRAFT)
                .totalAmount(new BigDecimal("100000"))
                .taxAmount(new BigDecimal("10000"))
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
