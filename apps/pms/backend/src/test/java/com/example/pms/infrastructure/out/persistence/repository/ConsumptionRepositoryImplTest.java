package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ConsumptionDetailRepository;
import com.example.pms.application.port.out.ConsumptionRepository;
import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.application.port.out.ReceivingRepository;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;
import com.example.pms.domain.model.subcontract.Consumption;
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
 * 消費データリポジトリテスト.
 */
@DisplayName("消費データリポジトリ")
class ConsumptionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private ConsumptionDetailRepository consumptionDetailRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String receivingNumber;

    @BeforeEach
    void setUp() {
        consumptionDetailRepository.deleteAll();
        consumptionRepository.deleteAll();
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

        // Create receiving
        Receiving receiving = Receiving.builder()
                .receivingNumber("RCV-001")
                .purchaseOrderNumber("PO-001")
                .lineNumber(1)
                .receivingDate(LocalDate.of(2024, 1, 20))
                .receivingType(ReceivingType.NORMAL)
                .itemCode("ITEM001")
                .miscellaneousItemFlag(false)
                .receivingQuantity(new BigDecimal("50.00"))
                .build();
        receivingRepository.save(receiving);
        receivingNumber = "RCV-001";
    }

    private Consumption createConsumption(String consumptionNumber) {
        return Consumption.builder()
                .consumptionNumber(consumptionNumber)
                .receivingNumber(receivingNumber)
                .consumptionDate(LocalDate.of(2024, 1, 25))
                .supplierCode("SUP001")
                .remarks("テスト消費")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("消費データを登録できる")
        void canRegisterConsumption() {
            // Arrange
            Consumption consumption = createConsumption("CON-001");

            // Act
            consumptionRepository.save(consumption);

            // Assert
            Optional<Consumption> found = consumptionRepository.findByConsumptionNumber("CON-001");
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo(receivingNumber);
            assertThat(found.get().getSupplierCode()).isEqualTo("SUP001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            consumptionRepository.save(createConsumption("CON-001"));
            consumptionRepository.save(createConsumption("CON-002"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Consumption> consumption = consumptionRepository.findByConsumptionNumber("CON-001");
            assertThat(consumption).isPresent();
            Integer id = consumption.get().getId();

            // Act
            Optional<Consumption> found = consumptionRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getConsumptionNumber()).isEqualTo("CON-001");
        }

        @Test
        @DisplayName("消費番号で検索できる")
        void canFindByConsumptionNumber() {
            // Act
            Optional<Consumption> found = consumptionRepository.findByConsumptionNumber("CON-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getReceivingNumber()).isEqualTo(receivingNumber);
        }

        @Test
        @DisplayName("入荷番号で検索できる")
        void canFindByReceivingNumber() {
            // Act
            List<Consumption> found = consumptionRepository.findByReceivingNumber(receivingNumber);

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(c -> c.getReceivingNumber().equals(receivingNumber));
        }

        @Test
        @DisplayName("取引先コードで検索できる")
        void canFindBySupplierCode() {
            // Act
            List<Consumption> found = consumptionRepository.findBySupplierCode("SUP001");

            // Assert
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("存在しない消費番号で検索すると空を返す")
        void returnsEmptyForNonExistentConsumptionNumber() {
            // Act
            Optional<Consumption> found = consumptionRepository.findByConsumptionNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Consumption> all = consumptionRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
