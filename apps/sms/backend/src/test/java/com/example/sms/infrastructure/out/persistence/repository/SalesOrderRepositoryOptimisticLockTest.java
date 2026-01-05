package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.CustomerRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 受注リポジトリ - 楽観ロックテスト.
 */
@DisplayName("受注リポジトリ - 楽観ロック")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals",
        "PMD.BigIntegerInstantiation"})
class SalesOrderRepositoryOptimisticLockTest extends BaseIntegrationTest {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        salesOrderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
        partnerRepository.deleteAll();

        // テスト用の取引先と顧客を登録
        var partner = Partner.builder()
                .partnerCode("C001")
                .partnerName("テスト顧客")
                .isCustomer(true)
                .build();
        partnerRepository.save(partner);

        var customer = Customer.builder()
                .customerCode("C001")
                .customerBranchNumber("00")
                .customerName("テスト顧客本社")
                .build();
        customerRepository.save(customer);

        // テスト用の商品を登録
        var product1 = Product.builder()
                .productCode("P001")
                .productFullName("商品A")
                .productName("商品A")
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(TaxCategory.EXCLUSIVE)
                .build();
        productRepository.save(product1);

        var product2 = Product.builder()
                .productCode("P002")
                .productFullName("商品B")
                .productName("商品B")
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(TaxCategory.EXCLUSIVE)
                .build();
        productRepository.save(product2);

        var product3 = Product.builder()
                .productCode("P003")
                .productFullName("商品C")
                .productName("商品C")
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(TaxCategory.EXCLUSIVE)
                .build();
        productRepository.save(product3);
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0001")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .totalAmount(new BigDecimal("55000"))
                    .status(OrderStatus.RECEIVED)
                    .build();
            salesOrderRepository.save(order);

            // Act
            var fetched = salesOrderRepository.findByOrderNumber("SO-2025-0001").get();
            fetched.setOrderAmount(new BigDecimal("60000"));
            salesOrderRepository.update(fetched);

            // Assert
            var updated = salesOrderRepository.findByOrderNumber("SO-2025-0001").get();
            assertThat(updated.getOrderAmount()).isEqualByComparingTo(new BigDecimal("60000"));
            assertThat(updated.getVersion()).isEqualTo(2); // バージョンがインクリメント
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0002")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .totalAmount(new BigDecimal("55000"))
                    .status(OrderStatus.RECEIVED)
                    .build();
            salesOrderRepository.save(order);

            // ユーザーAが取得
            var orderA = salesOrderRepository.findByOrderNumber("SO-2025-0002").get();
            // ユーザーBが取得
            var orderB = salesOrderRepository.findByOrderNumber("SO-2025-0002").get();

            // ユーザーAが更新（成功）
            orderA.setOrderAmount(new BigDecimal("60000"));
            salesOrderRepository.update(orderA);

            // Act & Assert: ユーザーBが古いバージョンで更新（失敗）
            orderB.setOrderAmount(new BigDecimal("70000"));
            assertThatThrownBy(() -> salesOrderRepository.update(orderB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0003")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .totalAmount(new BigDecimal("55000"))
                    .status(OrderStatus.RECEIVED)
                    .build();
            salesOrderRepository.save(order);

            // 取得
            var fetched = salesOrderRepository.findByOrderNumber("SO-2025-0003").get();

            // 別のユーザーが削除
            salesOrderRepository.deleteById(fetched.getId());

            // Act & Assert: 削除されたエンティティを更新（失敗）
            fetched.setOrderAmount(new BigDecimal("60000"));
            assertThatThrownBy(() -> salesOrderRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }

        @Test
        @DisplayName("複数回の更新でバージョンが正しくインクリメントされる")
        void versionIncrementsCorrectlyOnMultipleUpdates() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0004")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(new BigDecimal("50000"))
                    .taxAmount(new BigDecimal("5000"))
                    .totalAmount(new BigDecimal("55000"))
                    .status(OrderStatus.RECEIVED)
                    .build();
            salesOrderRepository.save(order);

            // Act: 3回更新
            for (int i = 1; i <= 3; i++) {
                var current = salesOrderRepository.findByOrderNumber("SO-2025-0004").get();
                current.setOrderAmount(new BigDecimal(50000 + i * 10000));
                salesOrderRepository.update(current);
            }

            // Assert
            var updated = salesOrderRepository.findByOrderNumber("SO-2025-0004").get();
            assertThat(updated.getVersion()).isEqualTo(4); // 初期1 + 3回更新 = 4
            assertThat(updated.getOrderAmount()).isEqualByComparingTo(new BigDecimal("80000"));
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で受注と受注明細を取得できる")
        void canFetchOrderWithDetailsUsingJoin() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0010")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(new BigDecimal("100000"))
                    .taxAmount(new BigDecimal("10000"))
                    .totalAmount(new BigDecimal("110000"))
                    .status(OrderStatus.RECEIVED)
                    .details(List.of(
                            SalesOrderDetail.builder()
                                    .lineNumber(1)
                                    .productCode("P001")
                                    .productName("商品A")
                                    .orderQuantity(new BigDecimal("10"))
                                    .remainingQuantity(new BigDecimal("10"))
                                    .unit("個")
                                    .unitPrice(new BigDecimal("5000"))
                                    .amount(new BigDecimal("50000"))
                                    .taxCategory(TaxCategory.EXCLUSIVE)
                                    .taxRate(new BigDecimal("10.00"))
                                    .taxAmount(new BigDecimal("5000"))
                                    .build(),
                            SalesOrderDetail.builder()
                                    .lineNumber(2)
                                    .productCode("P002")
                                    .productName("商品B")
                                    .orderQuantity(new BigDecimal("5"))
                                    .remainingQuantity(new BigDecimal("5"))
                                    .unit("個")
                                    .unitPrice(new BigDecimal("10000"))
                                    .amount(new BigDecimal("50000"))
                                    .taxCategory(TaxCategory.EXCLUSIVE)
                                    .taxRate(new BigDecimal("10.00"))
                                    .taxAmount(new BigDecimal("5000"))
                                    .build()
                    ))
                    .build();
            salesOrderRepository.save(order);

            // Act
            var result = salesOrderRepository.findWithDetailsByOrderNumber("SO-2025-0010");

            // Assert
            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getOrderNumber()).isEqualTo("SO-2025-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);
            assertThat(detail1.getProductCode()).isEqualTo("P001");
            assertThat(detail1.getVersion()).isEqualTo(1);

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
            assertThat(detail2.getProductCode()).isEqualTo("P002");
            assertThat(detail2.getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("IDによるJOIN一括取得で受注と受注明細を取得できる")
        void canFetchOrderWithDetailsByIdUsingJoin() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0011")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(new BigDecimal("30000"))
                    .taxAmount(new BigDecimal("3000"))
                    .totalAmount(new BigDecimal("33000"))
                    .status(OrderStatus.RECEIVED)
                    .details(List.of(
                            SalesOrderDetail.builder()
                                    .lineNumber(1)
                                    .productCode("P003")
                                    .productName("商品C")
                                    .orderQuantity(new BigDecimal("3"))
                                    .remainingQuantity(new BigDecimal("3"))
                                    .unit("個")
                                    .unitPrice(new BigDecimal("10000"))
                                    .amount(new BigDecimal("30000"))
                                    .taxCategory(TaxCategory.EXCLUSIVE)
                                    .taxRate(new BigDecimal("10.00"))
                                    .taxAmount(new BigDecimal("3000"))
                                    .build()
                    ))
                    .build();
            salesOrderRepository.save(order);

            // Act
            var result = salesOrderRepository.findByIdWithDetails(order.getId());

            // Assert
            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getOrderNumber()).isEqualTo("SO-2025-0011");
            assertThat(fetched.getDetails()).hasSize(1);
            assertThat(fetched.getDetails().get(0).getProductCode()).isEqualTo("P003");
        }

        @Test
        @DisplayName("明細がない受注も正しく取得できる")
        void canFetchOrderWithoutDetails() {
            // Arrange
            var order = SalesOrder.builder()
                    .orderNumber("SO-2025-0012")
                    .orderDate(LocalDate.of(2025, 1, 20))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .orderAmount(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO)
                    .status(OrderStatus.RECEIVED)
                    .build();
            salesOrderRepository.save(order);

            // Act
            var result = salesOrderRepository.findWithDetailsByOrderNumber("SO-2025-0012");

            // Assert
            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getOrderNumber()).isEqualTo("SO-2025-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }
}
