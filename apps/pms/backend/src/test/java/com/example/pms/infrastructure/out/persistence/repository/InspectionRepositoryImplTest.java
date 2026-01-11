package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.InspectionRepository;
import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.application.port.out.ReceivingRepository;
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
 * 受入検査データリポジトリテスト.
 */
@DisplayName("受入検査データリポジトリ")
class InspectionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String receivingNumber;
    private String purchaseOrderNumber;
    private Integer lineNumber;

    @BeforeEach
    void setUp() {
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
        receivingNumber = "RCV-001";
    }

    private Inspection createInspection(String inspectionNumber) {
        return Inspection.builder()
                .inspectionNumber(inspectionNumber)
                .receivingNumber(receivingNumber)
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .inspectionDate(LocalDate.of(2024, 1, 21))
                .inspectorCode("EMP001")
                .itemCode("ITEM001")
                .miscellaneousItemFlag(false)
                .goodQuantity(new BigDecimal("48.00"))
                .defectQuantity(new BigDecimal("2.00"))
                .remarks("テスト検査")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("受入検査を登録できる")
        void canRegisterInspection() {
            // Arrange
            Inspection inspection = createInspection("INS-001");

            // Act
            inspectionRepository.save(inspection);

            // Assert
            Optional<Inspection> found = inspectionRepository.findByInspectionNumber("INS-001");
            assertThat(found).isPresent();
            assertThat(found.get().getGoodQuantity()).isEqualByComparingTo(new BigDecimal("48.00"));
            assertThat(found.get().getDefectQuantity()).isEqualByComparingTo(new BigDecimal("2.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            inspectionRepository.save(createInspection("INS-001"));
            inspectionRepository.save(createInspection("INS-002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Inspection> inspection = inspectionRepository.findByInspectionNumber("INS-001");
            assertThat(inspection).isPresent();
            Integer id = inspection.get().getId();

            // Act
            Optional<Inspection> found = inspectionRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getInspectionNumber()).isEqualTo("INS-001");
        }

        @Test
        @DisplayName("受入検査番号で検索できる")
        void canFindByInspectionNumber() {
            // Act
            Optional<Inspection> found = inspectionRepository.findByInspectionNumber("INS-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo(receivingNumber);
        }

        @Test
        @DisplayName("入荷番号で検索できる")
        void canFindByReceivingNumber() {
            // Act
            List<Inspection> found = inspectionRepository.findByReceivingNumber(receivingNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(i -> i.getReceivingNumber().equals(receivingNumber));
        }

        @Test
        @DisplayName("存在しない入荷番号で検索すると空リストを返す")
        void returnsEmptyListForNonExistentReceivingNumber() {
            // Act
            List<Inspection> found = inspectionRepository.findByReceivingNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Inspection> all = inspectionRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
