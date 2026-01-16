package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 発注明細データリポジトリテスト.
 */
@DisplayName("発注明細データリポジトリ")
class PurchaseOrderDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String purchaseOrderNumber;

    @BeforeEach
    void setUp() {
        purchaseOrderDetailRepository.deleteAll();
        purchaseOrderRepository.deleteAll();

        // Create purchase order for foreign key reference
        PurchaseOrder po = PurchaseOrder.builder()
                .purchaseOrderNumber("PO-001")
                .orderDate(LocalDate.of(2024, 1, 15))
                .supplierCode("SUP001")
                .status(PurchaseOrderStatus.ORDERED)
                .build();
        purchaseOrderRepository.save(po);
        purchaseOrderNumber = "PO-001";
    }

    private PurchaseOrderDetail createDetail(Integer lineNumber, String itemCode) {
        return PurchaseOrderDetail.builder()
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .itemCode(itemCode)
                .miscellaneousItemFlag(false)
                .expectedReceivingDate(LocalDate.of(2024, 1, 20))
                .orderUnitPrice(new BigDecimal("1000.00"))
                .orderQuantity(new BigDecimal("100.00"))
                .receivedQuantity(BigDecimal.ZERO)
                .inspectedQuantity(BigDecimal.ZERO)
                .acceptedQuantity(BigDecimal.ZERO)
                .orderAmount(new BigDecimal("100000.00"))
                .taxAmount(new BigDecimal("10000.00"))
                .completedFlag(false)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("発注明細を登録できる")
        void canRegisterPurchaseOrderDetail() {
            // Arrange
            PurchaseOrderDetail detail = createDetail(1, "ITEM001");

            // Act
            purchaseOrderDetailRepository.save(detail);

            // Assert
            Optional<PurchaseOrderDetail> found = purchaseOrderDetailRepository
                    .findByPurchaseOrderNumberAndLineNumber(purchaseOrderNumber, 1);
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getOrderQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("複数明細を登録できる")
        void canRegisterMultipleDetails() {
            // Arrange
            PurchaseOrderDetail detail1 = createDetail(1, "ITEM001");
            PurchaseOrderDetail detail2 = createDetail(2, "ITEM002");
            PurchaseOrderDetail detail3 = createDetail(3, "ITEM003");

            // Act
            purchaseOrderDetailRepository.save(detail1);
            purchaseOrderDetailRepository.save(detail2);
            purchaseOrderDetailRepository.save(detail3);

            // Assert
            List<PurchaseOrderDetail> details = purchaseOrderDetailRepository
                    .findByPurchaseOrderNumber(purchaseOrderNumber);
            assertThat(details).hasSize(3);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            purchaseOrderDetailRepository.save(createDetail(1, "ITEM001"));
            purchaseOrderDetailRepository.save(createDetail(2, "ITEM002"));
            purchaseOrderDetailRepository.save(createDetail(3, "ITEM003"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<PurchaseOrderDetail> detail = purchaseOrderDetailRepository
                    .findByPurchaseOrderNumberAndLineNumber(purchaseOrderNumber, 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<PurchaseOrderDetail> found = purchaseOrderDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("発注番号と行番号で検索できる")
        void canFindByPurchaseOrderNumberAndLineNumber() {
            // Act
            Optional<PurchaseOrderDetail> found = purchaseOrderDetailRepository
                    .findByPurchaseOrderNumberAndLineNumber(purchaseOrderNumber, 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("発注番号で検索できる")
        void canFindByPurchaseOrderNumber() {
            // Act
            List<PurchaseOrderDetail> found = purchaseOrderDetailRepository
                    .findByPurchaseOrderNumber(purchaseOrderNumber);

            // Assert
            assertThat(found).hasSize(3);
            assertThat(found).allMatch(d -> d.getPurchaseOrderNumber().equals(purchaseOrderNumber));
        }

        @Test
        @DisplayName("存在しない発注番号で検索すると空リストを返す")
        void returnsEmptyListForNonExistentPurchaseOrderNumber() {
            // Act
            List<PurchaseOrderDetail> found = purchaseOrderDetailRepository
                    .findByPurchaseOrderNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<PurchaseOrderDetail> all = purchaseOrderDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
