package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.application.port.out.SupplyDetailRepository;
import com.example.pms.application.port.out.SupplyRepository;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.domain.model.subcontract.Supply;
import com.example.pms.domain.model.subcontract.SupplyDetail;
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
 * 支給明細データリポジトリテスト.
 */
@DisplayName("支給明細データリポジトリ")
class SupplyDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private SupplyDetailRepository supplyDetailRepository;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String supplyNumber;

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

        // Create purchase order detail
        PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                .purchaseOrderNumber("PO-001")
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

        // Create supply
        Supply supply = Supply.builder()
                .supplyNumber("SUP-001")
                .purchaseOrderNumber("PO-001")
                .lineNumber(1)
                .supplierCode("SUP001")
                .supplyDate(LocalDate.of(2024, 1, 16))
                .supplierPersonCode("EMP001")
                .supplyType(SupplyType.FREE)
                .build();
        supplyRepository.save(supply);
        supplyNumber = "SUP-001";
    }

    private SupplyDetail createSupplyDetail(Integer lineNumber, String itemCode) {
        return SupplyDetail.builder()
                .supplyNumber(supplyNumber)
                .lineNumber(lineNumber)
                .itemCode(itemCode)
                .quantity(new BigDecimal("50.00"))
                .unitPrice(new BigDecimal("200.00"))
                .amount(new BigDecimal("10000.00"))
                .remarks("テスト支給明細")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("支給明細を登録できる")
        void canRegisterSupplyDetail() {
            // Arrange
            SupplyDetail detail = createSupplyDetail(1, "ITEM001");

            // Act
            supplyDetailRepository.save(detail);

            // Assert
            Optional<SupplyDetail> found = supplyDetailRepository
                    .findBySupplyNumberAndLineNumber(supplyNumber, 1);
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getQuantity()).isEqualByComparingTo(new BigDecimal("50.00"));
        }

        @Test
        @DisplayName("複数明細を登録できる")
        void canRegisterMultipleDetails() {
            // Arrange
            SupplyDetail detail1 = createSupplyDetail(1, "ITEM001");
            SupplyDetail detail2 = createSupplyDetail(2, "ITEM002");
            SupplyDetail detail3 = createSupplyDetail(3, "ITEM003");

            // Act
            supplyDetailRepository.save(detail1);
            supplyDetailRepository.save(detail2);
            supplyDetailRepository.save(detail3);

            // Assert
            List<SupplyDetail> details = supplyDetailRepository.findBySupplyNumber(supplyNumber);
            assertThat(details).hasSize(3);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            supplyDetailRepository.save(createSupplyDetail(1, "ITEM001"));
            supplyDetailRepository.save(createSupplyDetail(2, "ITEM002"));
            supplyDetailRepository.save(createSupplyDetail(3, "ITEM003"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<SupplyDetail> detail = supplyDetailRepository
                    .findBySupplyNumberAndLineNumber(supplyNumber, 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<SupplyDetail> found = supplyDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("支給番号と行番号で検索できる")
        void canFindBySupplyNumberAndLineNumber() {
            // Act
            Optional<SupplyDetail> found = supplyDetailRepository
                    .findBySupplyNumberAndLineNumber(supplyNumber, 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("支給番号で検索できる")
        void canFindBySupplyNumber() {
            // Act
            List<SupplyDetail> found = supplyDetailRepository.findBySupplyNumber(supplyNumber);

            // Assert
            assertThat(found).hasSize(3);
            assertThat(found).allMatch(d -> d.getSupplyNumber().equals(supplyNumber));
        }

        @Test
        @DisplayName("存在しない支給番号で検索すると空リストを返す")
        void returnsEmptyListForNonExistentSupplyNumber() {
            // Act
            List<SupplyDetail> found = supplyDetailRepository.findBySupplyNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<SupplyDetail> all = supplyDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
