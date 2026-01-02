package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.application.port.out.ReceivingRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingDetail;
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
 * 入荷リポジトリテスト.
 */
@DisplayName("入荷リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class ReceivingRepositoryTest extends BaseIntegrationTest {

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

    private Integer testPurchaseOrderId;
    private Integer testPurchaseOrderDetailId;

    @BeforeEach
    void setUp() {
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
                                .orderQuantity(new BigDecimal("10"))
                                .unitPrice(new BigDecimal("5000"))
                                .orderAmount(new BigDecimal("50000"))
                                .expectedDeliveryDate(LocalDate.of(2025, 1, 31))
                                .build()
                ))
                .build();
        purchaseOrderRepository.save(purchaseOrder);
        testPurchaseOrderId = purchaseOrder.getId();

        // 発注明細IDを取得
        var savedOrder = purchaseOrderRepository.findWithDetailsByPurchaseOrderNumber("PO-2025-0001").get();
        testPurchaseOrderDetailId = savedOrder.getDetails().get(0).getId();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("入荷を登録できる")
        void canRegisterReceiving() {
            var receiving = Receiving.builder()
                    .receivingNumber("RCV-2025-0001")
                    .purchaseOrderId(testPurchaseOrderId)
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .receivingDate(LocalDate.of(2025, 1, 20))
                    .status(ReceivingStatus.WAITING)
                    .warehouseCode("WH001")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            receivingRepository.save(receiving);

            var result = receivingRepository.findByReceivingNumber("RCV-2025-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getReceivingNumber()).isEqualTo("RCV-2025-0001");
            assertThat(result.get().getStatus()).isEqualTo(ReceivingStatus.WAITING);
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("入荷明細付きで登録できる")
        void canRegisterReceivingWithDetails() {
            var receiving = Receiving.builder()
                    .receivingNumber("RCV-2025-0002")
                    .purchaseOrderId(testPurchaseOrderId)
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .receivingDate(LocalDate.of(2025, 1, 20))
                    .status(ReceivingStatus.WAITING)
                    .warehouseCode("WH001")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            ReceivingDetail.builder()
                                    .lineNumber(1)
                                    .purchaseOrderDetailId(testPurchaseOrderDetailId)
                                    .productCode("P001")
                                    .receivingQuantity(new BigDecimal("10"))
                                    .unitPrice(new BigDecimal("5000"))
                                    .amount(new BigDecimal("50000"))
                                    .build()
                    ))
                    .build();

            receivingRepository.save(receiving);

            var result = receivingRepository.findWithDetailsByReceivingNumber("RCV-2025-0002");
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
            var rcv1 = createReceiving("RCV-2025-0001", LocalDate.of(2025, 1, 20));
            var rcv2 = createReceiving("RCV-2025-0002", LocalDate.of(2025, 1, 25));
            receivingRepository.save(rcv1);
            receivingRepository.save(rcv2);

            var result = receivingRepository.findBySupplierCode("S001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            var rcv1 = createReceiving("RCV-2025-0001", LocalDate.of(2025, 1, 20));
            rcv1.setStatus(ReceivingStatus.INSPECTION_COMPLETED);
            var rcv2 = createReceiving("RCV-2025-0002", LocalDate.of(2025, 1, 25));
            rcv2.setStatus(ReceivingStatus.WAITING);
            receivingRepository.save(rcv1);
            receivingRepository.save(rcv2);

            var result = receivingRepository.findByStatus(ReceivingStatus.INSPECTION_COMPLETED);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReceivingNumber()).isEqualTo("RCV-2025-0001");
        }

        @Test
        @DisplayName("入荷日範囲で検索できる")
        void canFindByReceivingDateBetween() {
            var rcv1 = createReceiving("RCV-2025-0001", LocalDate.of(2025, 1, 10));
            var rcv2 = createReceiving("RCV-2025-0002", LocalDate.of(2025, 1, 20));
            var rcv3 = createReceiving("RCV-2025-0003", LocalDate.of(2025, 2, 1));
            receivingRepository.save(rcv1);
            receivingRepository.save(rcv2);
            receivingRepository.save(rcv3);

            var result = receivingRepository.findByReceivingDateBetween(
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("発注IDで検索できる")
        void canFindByPurchaseOrderId() {
            var rcv1 = createReceiving("RCV-2025-0001", LocalDate.of(2025, 1, 20));
            receivingRepository.save(rcv1);

            var result = receivingRepository.findByPurchaseOrderId(testPurchaseOrderId);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReceivingNumber()).isEqualTo("RCV-2025-0001");
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var receiving = createReceiving("RCV-2025-0001", LocalDate.of(2025, 1, 20));
            receivingRepository.save(receiving);

            var fetched = receivingRepository.findByReceivingNumber("RCV-2025-0001").get();
            fetched.setStatus(ReceivingStatus.INSPECTING);
            receivingRepository.update(fetched);

            var updated = receivingRepository.findByReceivingNumber("RCV-2025-0001").get();
            assertThat(updated.getStatus()).isEqualTo(ReceivingStatus.INSPECTING);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var receiving = createReceiving("RCV-2025-0002", LocalDate.of(2025, 1, 20));
            receivingRepository.save(receiving);

            var rcvA = receivingRepository.findByReceivingNumber("RCV-2025-0002").get();
            var rcvB = receivingRepository.findByReceivingNumber("RCV-2025-0002").get();

            rcvA.setStatus(ReceivingStatus.INSPECTING);
            receivingRepository.update(rcvA);

            rcvB.setStatus(ReceivingStatus.INSPECTION_COMPLETED);
            assertThatThrownBy(() -> receivingRepository.update(rcvB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var receiving = createReceiving("RCV-2025-0003", LocalDate.of(2025, 1, 20));
            receivingRepository.save(receiving);

            var fetched = receivingRepository.findByReceivingNumber("RCV-2025-0003").get();
            receivingRepository.deleteById(fetched.getId());

            fetched.setStatus(ReceivingStatus.INSPECTING);
            assertThatThrownBy(() -> receivingRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で入荷と入荷明細を取得できる")
        void canFetchReceivingWithDetailsUsingJoin() {
            var receiving = Receiving.builder()
                    .receivingNumber("RCV-2025-0010")
                    .purchaseOrderId(testPurchaseOrderId)
                    .supplierCode("S001")
                    .supplierBranchNumber("00")
                    .receivingDate(LocalDate.of(2025, 1, 20))
                    .status(ReceivingStatus.WAITING)
                    .warehouseCode("WH001")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            ReceivingDetail.builder()
                                    .lineNumber(1)
                                    .purchaseOrderDetailId(testPurchaseOrderDetailId)
                                    .productCode("P001")
                                    .receivingQuantity(new BigDecimal("5"))
                                    .unitPrice(new BigDecimal("5000"))
                                    .amount(new BigDecimal("25000"))
                                    .build(),
                            ReceivingDetail.builder()
                                    .lineNumber(2)
                                    .purchaseOrderDetailId(testPurchaseOrderDetailId)
                                    .productCode("P001")
                                    .receivingQuantity(new BigDecimal("5"))
                                    .unitPrice(new BigDecimal("5000"))
                                    .amount(new BigDecimal("25000"))
                                    .build()
                    ))
                    .build();
            receivingRepository.save(receiving);

            var result = receivingRepository.findWithDetailsByReceivingNumber("RCV-2025-0010");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getReceivingNumber()).isEqualTo("RCV-2025-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("明細がない入荷も正しく取得できる")
        void canFetchReceivingWithoutDetails() {
            var receiving = createReceiving("RCV-2025-0012", LocalDate.of(2025, 1, 20));
            receivingRepository.save(receiving);

            var result = receivingRepository.findWithDetailsByReceivingNumber("RCV-2025-0012");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getReceivingNumber()).isEqualTo("RCV-2025-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }

    private Receiving createReceiving(String receivingNumber, LocalDate receivingDate) {
        return Receiving.builder()
                .receivingNumber(receivingNumber)
                .purchaseOrderId(testPurchaseOrderId)
                .supplierCode("S001")
                .supplierBranchNumber("00")
                .receivingDate(receivingDate)
                .status(ReceivingStatus.WAITING)
                .warehouseCode("WH001")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
