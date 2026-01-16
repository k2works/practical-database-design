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
import com.example.pms.domain.model.subcontract.ConsumptionDetail;
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
 * 消費明細データリポジトリテスト.
 */
@DisplayName("消費明細データリポジトリ")
class ConsumptionDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ConsumptionDetailRepository consumptionDetailRepository;

    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private String consumptionNumber;

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

        // Create consumption
        Consumption consumption = Consumption.builder()
                .consumptionNumber("CON-001")
                .receivingNumber("RCV-001")
                .consumptionDate(LocalDate.of(2024, 1, 25))
                .supplierCode("SUP001")
                .build();
        consumptionRepository.save(consumption);
        consumptionNumber = "CON-001";
    }

    private ConsumptionDetail createConsumptionDetail(Integer lineNumber, String itemCode) {
        return ConsumptionDetail.builder()
                .consumptionNumber(consumptionNumber)
                .lineNumber(lineNumber)
                .itemCode(itemCode)
                .quantity(new BigDecimal("45.00"))
                .remarks("テスト消費明細")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("消費明細を登録できる")
        void canRegisterConsumptionDetail() {
            // Arrange
            ConsumptionDetail detail = createConsumptionDetail(1, "ITEM001");

            // Act
            consumptionDetailRepository.save(detail);

            // Assert
            Optional<ConsumptionDetail> found = consumptionDetailRepository
                    .findByConsumptionNumberAndLineNumber(consumptionNumber, 1);
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getQuantity()).isEqualByComparingTo(new BigDecimal("45.00"));
        }

        @Test
        @DisplayName("複数明細を登録できる")
        void canRegisterMultipleDetails() {
            // Arrange
            ConsumptionDetail detail1 = createConsumptionDetail(1, "ITEM001");
            ConsumptionDetail detail2 = createConsumptionDetail(2, "ITEM002");
            ConsumptionDetail detail3 = createConsumptionDetail(3, "ITEM003");

            // Act
            consumptionDetailRepository.save(detail1);
            consumptionDetailRepository.save(detail2);
            consumptionDetailRepository.save(detail3);

            // Assert
            List<ConsumptionDetail> details = consumptionDetailRepository
                    .findByConsumptionNumber(consumptionNumber);
            assertThat(details).hasSize(3);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            consumptionDetailRepository.save(createConsumptionDetail(1, "ITEM001"));
            consumptionDetailRepository.save(createConsumptionDetail(2, "ITEM002"));
            consumptionDetailRepository.save(createConsumptionDetail(3, "ITEM003"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<ConsumptionDetail> detail = consumptionDetailRepository
                    .findByConsumptionNumberAndLineNumber(consumptionNumber, 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<ConsumptionDetail> found = consumptionDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("消費番号と行番号で検索できる")
        void canFindByConsumptionNumberAndLineNumber() {
            // Act
            Optional<ConsumptionDetail> found = consumptionDetailRepository
                    .findByConsumptionNumberAndLineNumber(consumptionNumber, 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("消費番号で検索できる")
        void canFindByConsumptionNumber() {
            // Act
            List<ConsumptionDetail> found = consumptionDetailRepository
                    .findByConsumptionNumber(consumptionNumber);

            // Assert
            assertThat(found).hasSize(3);
            assertThat(found).allMatch(d -> d.getConsumptionNumber().equals(consumptionNumber));
        }

        @Test
        @DisplayName("存在しない消費番号で検索すると空リストを返す")
        void returnsEmptyListForNonExistentConsumptionNumber() {
            // Act
            List<ConsumptionDetail> found = consumptionDetailRepository
                    .findByConsumptionNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<ConsumptionDetail> all = consumptionDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
