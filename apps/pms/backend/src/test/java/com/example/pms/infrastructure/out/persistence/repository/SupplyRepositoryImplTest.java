package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.application.port.out.SupplyDetailRepository;
import com.example.pms.application.port.out.SupplyRepository;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.domain.model.subcontract.Supply;
import com.example.pms.domain.model.subcontract.SupplyType;
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
 * 支給データリポジトリテスト.
 */
@DisplayName("支給データリポジトリ")
class SupplyRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private SupplyDetailRepository supplyDetailRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String purchaseOrderNumber;
    private Integer lineNumber;

    @BeforeEach
    void setUp() {
        supplyDetailRepository.deleteAll();
        supplyRepository.deleteAll();
        purchaseOrderDetailRepository.deleteAll();
        purchaseOrderRepository.deleteAll();

        // Create purchase order
        PurchaseOrder po = PurchaseOrder.builder()
                .purchaseOrderNumber("PO-001")
                .orderDate(LocalDate.of(2024, 1, 15))
                .supplierCode("SUP001")
                .status(PurchaseOrderStatus.ORDERED)
                .build();
        purchaseOrderRepository.save(po);
        purchaseOrderNumber = "PO-001";

        // Create purchase order detail
        PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(1)
                .itemCode("ITEM001")
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
                .build();
        purchaseOrderDetailRepository.save(detail);
        lineNumber = 1;
    }

    private Supply createSupply(String supplyNumber, SupplyType supplyType) {
        return Supply.builder()
                .supplyNumber(supplyNumber)
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .supplierCode("SUP001")
                .supplyDate(LocalDate.of(2024, 1, 16))
                .supplierPersonCode("EMP001")
                .supplyType(supplyType)
                .remarks("テスト支給")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("無償支給を登録できる")
        void canRegisterFreeSupply() {
            // Arrange
            Supply supply = createSupply("SUP-001", SupplyType.FREE);

            // Act
            supplyRepository.save(supply);

            // Assert
            Optional<Supply> found = supplyRepository.findBySupplyNumber("SUP-001");
            assertThat(found).isPresent();
            assertThat(found.get().getSupplyType()).isEqualTo(SupplyType.FREE);
            assertThat(found.get().getSupplierCode()).isEqualTo("SUP001");
        }

        @Test
        @DisplayName("有償支給を登録できる")
        void canRegisterPaidSupply() {
            // Arrange
            Supply supply = createSupply("SUP-002", SupplyType.PAID);

            // Act
            supplyRepository.save(supply);

            // Assert
            Optional<Supply> found = supplyRepository.findBySupplyNumber("SUP-002");
            assertThat(found).isPresent();
            assertThat(found.get().getSupplyType()).isEqualTo(SupplyType.PAID);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            supplyRepository.save(createSupply("SUP-001", SupplyType.FREE));
            supplyRepository.save(createSupply("SUP-002", SupplyType.PAID));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Supply> supply = supplyRepository.findBySupplyNumber("SUP-001");
            assertThat(supply).isPresent();
            Integer id = supply.get().getId();

            // Act
            Optional<Supply> found = supplyRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getSupplyNumber()).isEqualTo("SUP-001");
        }

        @Test
        @DisplayName("支給番号で検索できる")
        void canFindBySupplyNumber() {
            // Act
            Optional<Supply> found = supplyRepository.findBySupplyNumber("SUP-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getSupplyType()).isEqualTo(SupplyType.PAID);
        }

        @Test
        @DisplayName("発注番号で検索できる")
        void canFindByPurchaseOrderNumber() {
            // Act
            List<Supply> found = supplyRepository.findByPurchaseOrderNumber(purchaseOrderNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(s -> s.getPurchaseOrderNumber().equals(purchaseOrderNumber));
        }

        @Test
        @DisplayName("発注番号と行番号で検索できる")
        void canFindByPurchaseOrderNumberAndLineNumber() {
            // Act
            List<Supply> found = supplyRepository.findByPurchaseOrderNumberAndLineNumber(
                    purchaseOrderNumber, lineNumber);

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("取引先コードで検索できる")
        void canFindBySupplierCode() {
            // Act
            List<Supply> found = supplyRepository.findBySupplierCode("SUP001");

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("存在しない支給番号で検索すると空を返す")
        void returnsEmptyForNonExistentSupplyNumber() {
            // Act
            Optional<Supply> found = supplyRepository.findBySupplyNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Supply> all = supplyRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
