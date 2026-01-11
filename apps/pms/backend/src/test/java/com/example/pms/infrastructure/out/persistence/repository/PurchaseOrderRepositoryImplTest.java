package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 発注データリポジトリテスト.
 */
@DisplayName("発注データリポジトリ")
class PurchaseOrderRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @BeforeEach
    void setUp() {
        purchaseOrderRepository.deleteAll();
    }

    private PurchaseOrder createPurchaseOrder(String purchaseOrderNumber, PurchaseOrderStatus status) {
        return PurchaseOrder.builder()
                .purchaseOrderNumber(purchaseOrderNumber)
                .orderDate(LocalDate.of(2024, 1, 15))
                .supplierCode("SUP001")
                .ordererCode("EMP001")
                .departmentCode("DEPT01")
                .status(status)
                .remarks("テスト発注")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    class Registration {

        @Test
        @DisplayName("発注を登録できる")
        void canRegisterPurchaseOrder() {
            // Arrange
            PurchaseOrder po = createPurchaseOrder("PO-202401-0001", PurchaseOrderStatus.CREATING);

            // Act
            purchaseOrderRepository.save(po);

            // Assert
            Optional<PurchaseOrder> found = purchaseOrderRepository.findByPurchaseOrderNumber("PO-202401-0001");
            assertThat(found).isPresent();
            assertThat(found.get().getSupplierCode()).isEqualTo("SUP001");
            assertThat(found.get().getStatus()).isEqualTo(PurchaseOrderStatus.CREATING);
        }

        @Test
        @DisplayName("各発注ステータスを登録できる")
        void canRegisterAllPurchaseOrderStatuses() {
            // Arrange & Act & Assert
            int index = 0;
            for (PurchaseOrderStatus status : PurchaseOrderStatus.values()) {
                purchaseOrderRepository.deleteAll();
                String orderNumber = "PO-" + String.format("%03d", index++);
                PurchaseOrder po = PurchaseOrder.builder()
                        .purchaseOrderNumber(orderNumber)
                        .orderDate(LocalDate.of(2024, 1, 15))
                        .supplierCode("SUP001")
                        .status(status)
                        .build();
                purchaseOrderRepository.save(po);

                Optional<PurchaseOrder> found = purchaseOrderRepository.findByPurchaseOrderNumber(orderNumber);
                assertThat(found).isPresent();
                assertThat(found.get().getStatus()).isEqualTo(status);
                assertThat(found.get().getStatus().getDisplayName()).isEqualTo(status.getDisplayName());
            }
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            purchaseOrderRepository.save(createPurchaseOrder("PO-001", PurchaseOrderStatus.CREATING));
            purchaseOrderRepository.save(createPurchaseOrder("PO-002", PurchaseOrderStatus.ORDERED));
            purchaseOrderRepository.save(createPurchaseOrder("PO-003", PurchaseOrderStatus.CREATING));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<PurchaseOrder> po = purchaseOrderRepository.findByPurchaseOrderNumber("PO-001");
            assertThat(po).isPresent();
            Integer id = po.get().getId();

            // Act
            Optional<PurchaseOrder> found = purchaseOrderRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getPurchaseOrderNumber()).isEqualTo("PO-001");
        }

        @Test
        @DisplayName("発注番号で検索できる")
        void canFindByPurchaseOrderNumber() {
            // Act
            Optional<PurchaseOrder> found = purchaseOrderRepository.findByPurchaseOrderNumber("PO-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(PurchaseOrderStatus.ORDERED);
        }

        @Test
        @DisplayName("存在しない発注番号で検索すると空を返す")
        void returnsEmptyForNonExistentPurchaseOrderNumber() {
            // Act
            Optional<PurchaseOrder> found = purchaseOrderRepository.findByPurchaseOrderNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            // Act
            List<PurchaseOrder> creating = purchaseOrderRepository.findByStatus(PurchaseOrderStatus.CREATING);

            // Assert
            assertThat(creating).hasSize(2);
            assertThat(creating).allMatch(po -> po.getStatus() == PurchaseOrderStatus.CREATING);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<PurchaseOrder> all = purchaseOrderRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("ステータスを更新できる")
        void canUpdateStatus() {
            // Arrange
            PurchaseOrder po = createPurchaseOrder("PO-001", PurchaseOrderStatus.CREATING);
            purchaseOrderRepository.save(po);
            Optional<PurchaseOrder> saved = purchaseOrderRepository.findByPurchaseOrderNumber("PO-001");
            assertThat(saved).isPresent();

            // Act
            purchaseOrderRepository.updateStatus(saved.get().getId(), PurchaseOrderStatus.ORDERED);

            // Assert
            Optional<PurchaseOrder> updated = purchaseOrderRepository.findByPurchaseOrderNumber("PO-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getStatus()).isEqualTo(PurchaseOrderStatus.ORDERED);
        }
    }
}
