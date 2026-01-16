package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.AcceptanceRepository;
import com.example.pms.application.port.out.InspectionRepository;
import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.application.port.out.ReceivingRepository;
import com.example.pms.domain.model.purchase.Acceptance;
import com.example.pms.domain.model.purchase.Inspection;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;
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
 * 検収データリポジトリテスト.
 */
@DisplayName("検収データリポジトリ")
class AcceptanceRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private AcceptanceRepository acceptanceRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String inspectionNumber;
    private String purchaseOrderNumber;
    private Integer lineNumber;

    @BeforeEach
    void setUp() {
        acceptanceRepository.deleteAll();
        inspectionRepository.deleteAll();
        receivingRepository.deleteAll();
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

        // Create receiving
        Receiving receiving = Receiving.builder()
                .receivingNumber("RCV-001")
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .receivingDate(LocalDate.of(2024, 1, 20))
                .receivingType(ReceivingType.NORMAL)
                .itemCode("ITEM001")
                .miscellaneousItemFlag(false)
                .receivingQuantity(new BigDecimal("50.00"))
                .build();
        receivingRepository.save(receiving);

        // Create inspection
        Inspection inspection = Inspection.builder()
                .inspectionNumber("INS-001")
                .receivingNumber("RCV-001")
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .inspectionDate(LocalDate.of(2024, 1, 21))
                .itemCode("ITEM001")
                .miscellaneousItemFlag(false)
                .goodQuantity(new BigDecimal("48.00"))
                .defectQuantity(new BigDecimal("2.00"))
                .build();
        inspectionRepository.save(inspection);
        inspectionNumber = "INS-001";
    }

    private Acceptance createAcceptance(String acceptanceNumber) {
        return Acceptance.builder()
                .acceptanceNumber(acceptanceNumber)
                .inspectionNumber(inspectionNumber)
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .acceptanceDate(LocalDate.of(2024, 1, 22))
                .acceptorCode("EMP001")
                .supplierCode("SUP001")
                .itemCode("ITEM001")
                .miscellaneousItemFlag(false)
                .acceptedQuantity(new BigDecimal("48.00"))
                .unitPrice(new BigDecimal("1000.00"))
                .amount(new BigDecimal("48000.00"))
                .taxAmount(new BigDecimal("4800.00"))
                .remarks("テスト検収")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("検収を登録できる")
        void canRegisterAcceptance() {
            // Arrange
            Acceptance acceptance = createAcceptance("ACC-001");

            // Act
            acceptanceRepository.save(acceptance);

            // Assert
            Optional<Acceptance> found = acceptanceRepository.findByAcceptanceNumber("ACC-001");
            assertThat(found).isPresent();
            assertThat(found.get().getAcceptedQuantity()).isEqualByComparingTo(new BigDecimal("48.00"));
            assertThat(found.get().getAmount()).isEqualByComparingTo(new BigDecimal("48000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            acceptanceRepository.save(createAcceptance("ACC-001"));
            acceptanceRepository.save(createAcceptance("ACC-002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Acceptance> acceptance = acceptanceRepository.findByAcceptanceNumber("ACC-001");
            assertThat(acceptance).isPresent();
            Integer id = acceptance.get().getId();

            // Act
            Optional<Acceptance> found = acceptanceRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getAcceptanceNumber()).isEqualTo("ACC-001");
        }

        @Test
        @DisplayName("検収番号で検索できる")
        void canFindByAcceptanceNumber() {
            // Act
            Optional<Acceptance> found = acceptanceRepository.findByAcceptanceNumber("ACC-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getInspectionNumber()).isEqualTo(inspectionNumber);
        }

        @Test
        @DisplayName("受入検査番号で検索できる")
        void canFindByInspectionNumber() {
            // Act
            List<Acceptance> found = acceptanceRepository.findByInspectionNumber(inspectionNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(a -> a.getInspectionNumber().equals(inspectionNumber));
        }

        @Test
        @DisplayName("発注番号で検索できる")
        void canFindByPurchaseOrderNumber() {
            // Act
            List<Acceptance> found = acceptanceRepository.findByPurchaseOrderNumber(purchaseOrderNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(a -> a.getPurchaseOrderNumber().equals(purchaseOrderNumber));
        }

        @Test
        @DisplayName("存在しない検収番号で検索すると空を返す")
        void returnsEmptyForNonExistentAcceptanceNumber() {
            // Act
            Optional<Acceptance> found = acceptanceRepository.findByAcceptanceNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Acceptance> all = acceptanceRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
