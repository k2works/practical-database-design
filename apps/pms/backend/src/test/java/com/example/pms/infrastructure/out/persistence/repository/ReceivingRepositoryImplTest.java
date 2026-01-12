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
 * 入荷受入データリポジトリテスト.
 */
@DisplayName("入荷受入データリポジトリ")
class ReceivingRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    private String purchaseOrderNumber;
    private Integer lineNumber;

    @BeforeEach
    void setUp() {
        inspectionRepository.deleteAll();
        receivingRepository.deleteAll();
        purchaseOrderDetailRepository.deleteAll();
        purchaseOrderRepository.deleteAll();

        // Create purchase order and detail for foreign key reference
        PurchaseOrder po = PurchaseOrder.builder()
                .purchaseOrderNumber("PO-001")
                .orderDate(LocalDate.of(2024, 1, 15))
                .supplierCode("SUP001")
                .status(PurchaseOrderStatus.ORDERED)
                .build();
        purchaseOrderRepository.save(po);
        purchaseOrderNumber = "PO-001";

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

    private Receiving createReceiving(String receivingNumber, ReceivingType receivingType) {
        return Receiving.builder()
                .receivingNumber(receivingNumber)
                .purchaseOrderNumber(purchaseOrderNumber)
                .lineNumber(lineNumber)
                .receivingDate(LocalDate.of(2024, 1, 20))
                .receiverCode("EMP001")
                .receivingType(receivingType)
                .itemCode("ITEM001")
                .miscellaneousItemFlag(false)
                .receivingQuantity(new BigDecimal("50.00"))
                .remarks("テスト入荷")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    class Registration {

        @Test
        @DisplayName("入荷を登録できる")
        void canRegisterReceiving() {
            // Arrange
            Receiving receiving = createReceiving("RCV-001", ReceivingType.NORMAL);

            // Act
            receivingRepository.save(receiving);

            // Assert
            Optional<Receiving> found = receivingRepository.findByReceivingNumber("RCV-001");
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingType()).isEqualTo(ReceivingType.NORMAL);
            assertThat(found.get().getReceivingQuantity()).isEqualByComparingTo(new BigDecimal("50.00"));
        }

        @Test
        @DisplayName("各入荷受入区分を登録できる")
        void canRegisterAllReceivingTypes() {
            // Arrange & Act & Assert
            for (ReceivingType type : ReceivingType.values()) {
                receivingRepository.deleteAll();
                Receiving receiving = Receiving.builder()
                        .receivingNumber("RCV_" + type.name())
                        .purchaseOrderNumber(purchaseOrderNumber)
                        .lineNumber(lineNumber)
                        .receivingDate(LocalDate.of(2024, 1, 20))
                        .receivingType(type)
                        .itemCode("ITEM001")
                        .miscellaneousItemFlag(false)
                        .receivingQuantity(new BigDecimal("10.00"))
                        .build();
                receivingRepository.save(receiving);

                Optional<Receiving> found = receivingRepository.findByReceivingNumber("RCV_" + type.name());
                assertThat(found).isPresent();
                assertThat(found.get().getReceivingType()).isEqualTo(type);
                assertThat(found.get().getReceivingType().getDisplayName()).isEqualTo(type.getDisplayName());
            }
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            receivingRepository.save(createReceiving("RCV-001", ReceivingType.NORMAL));
            receivingRepository.save(createReceiving("RCV-002", ReceivingType.SPLIT));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Receiving> receiving = receivingRepository.findByReceivingNumber("RCV-001");
            assertThat(receiving).isPresent();
            Integer id = receiving.get().getId();

            // Act
            Optional<Receiving> found = receivingRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo("RCV-001");
        }

        @Test
        @DisplayName("入荷番号で検索できる")
        void canFindByReceivingNumber() {
            // Act
            Optional<Receiving> found = receivingRepository.findByReceivingNumber("RCV-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingType()).isEqualTo(ReceivingType.SPLIT);
        }

        @Test
        @DisplayName("発注番号で検索できる")
        void canFindByPurchaseOrderNumber() {
            // Act
            List<Receiving> found = receivingRepository.findByPurchaseOrderNumber(purchaseOrderNumber);

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Receiving> all = receivingRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }

    @Nested
    @DisplayName("リレーション")
    class Relation {

        @Test
        @DisplayName("入荷データと検査を同時に取得できる")
        void canFindReceivingWithInspections() {
            // Arrange
            Receiving receiving = createReceiving("RCV-001", ReceivingType.NORMAL);
            receivingRepository.save(receiving);

            // Create inspections
            Inspection inspection1 = Inspection.builder()
                    .inspectionNumber("INS-001")
                    .receivingNumber("RCV-001")
                    .purchaseOrderNumber(purchaseOrderNumber)
                    .lineNumber(lineNumber)
                    .inspectionDate(LocalDate.of(2024, 1, 21))
                    .inspectorCode("EMP001")
                    .itemCode("ITEM001")
                    .miscellaneousItemFlag(false)
                    .goodQuantity(new BigDecimal("25.00"))
                    .defectQuantity(new BigDecimal("0.00"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();
            inspectionRepository.save(inspection1);

            Inspection inspection2 = Inspection.builder()
                    .inspectionNumber("INS-002")
                    .receivingNumber("RCV-001")
                    .purchaseOrderNumber(purchaseOrderNumber)
                    .lineNumber(lineNumber)
                    .inspectionDate(LocalDate.of(2024, 1, 22))
                    .inspectorCode("EMP002")
                    .itemCode("ITEM001")
                    .miscellaneousItemFlag(false)
                    .goodQuantity(new BigDecimal("23.00"))
                    .defectQuantity(new BigDecimal("2.00"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();
            inspectionRepository.save(inspection2);

            // Act
            Optional<Receiving> found = receivingRepository.findByReceivingNumberWithInspections("RCV-001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo("RCV-001");
            assertThat(found.get().getInspections()).isNotNull();
            assertThat(found.get().getInspections()).hasSize(2);
        }

        @Test
        @DisplayName("検査がない場合は空リストを返す")
        void returnsEmptyListWhenNoInspections() {
            // Arrange
            Receiving receiving = createReceiving("RCV-002", ReceivingType.SPLIT);
            receivingRepository.save(receiving);

            // Act
            Optional<Receiving> found = receivingRepository.findByReceivingNumberWithInspections("RCV-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getInspections()).isNotNull();
            assertThat(found.get().getInspections()).isEmpty();
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {

        @Test
        @DisplayName("デフォルトバージョンは1である")
        void defaultVersionIsOne() {
            // Arrange
            Receiving receiving = createReceiving("RCV-001", ReceivingType.NORMAL);

            // Act
            receivingRepository.save(receiving);
            Optional<Receiving> found = receivingRepository.findByReceivingNumber("RCV-001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getVersion()).isEqualTo(1);
        }
    }
}
