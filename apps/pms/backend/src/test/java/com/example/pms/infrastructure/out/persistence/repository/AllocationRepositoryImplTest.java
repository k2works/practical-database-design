package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.AllocationRepository;
import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.application.port.out.RequirementRepository;
import com.example.pms.domain.model.plan.Allocation;
import com.example.pms.domain.model.plan.AllocationType;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.OrderType;
import com.example.pms.domain.model.plan.PlanStatus;
import com.example.pms.domain.model.plan.Requirement;
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
 * 引当情報リポジトリテスト.
 */
@DisplayName("引当情報リポジトリ")
class AllocationRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Integer orderId;
    private Integer requirementId;

    @BeforeEach
    void setUp() {
        allocationRepository.deleteAll();
        requirementRepository.deleteAll();
        orderRepository.deleteAll();

        // Create an order for foreign key reference
        Order order = Order.builder()
                .orderNumber("ORD001")
                .orderType(OrderType.MANUFACTURING)
                .itemCode("PROD001")
                .startDate(LocalDate.of(2024, 1, 1))
                .dueDate(LocalDate.of(2024, 1, 15))
                .planQuantity(new BigDecimal("100.00"))
                .locationCode("WH001")
                .status(PlanStatus.DRAFT)
                .build();
        orderRepository.save(order);
        Optional<Order> savedOrder = orderRepository.findByOrderNumber("ORD001");
        orderId = savedOrder.get().getId();

        // Create a requirement for foreign key reference
        Requirement requirement = Requirement.builder()
                .requirementNumber("REQ001")
                .orderId(orderId)
                .itemCode("PART001")
                .dueDate(LocalDate.of(2024, 1, 10))
                .requiredQuantity(new BigDecimal("50.00"))
                .allocatedQuantity(BigDecimal.ZERO)
                .shortageQuantity(new BigDecimal("50.00"))
                .locationCode("WH001")
                .build();
        requirementRepository.save(requirement);
        Optional<Requirement> savedReq = requirementRepository.findByRequirementNumber("REQ001");
        requirementId = savedReq.get().getId();
    }

    private Allocation createAllocation(Integer requirementId, AllocationType allocationType) {
        return Allocation.builder()
                .requirementId(requirementId)
                .allocationType(allocationType)
                .allocationDate(LocalDate.of(2024, 1, 5))
                .allocatedQuantity(new BigDecimal("20.00"))
                .locationCode("WH001")
                .build();
    }

    @Nested
    @DisplayName("登録")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    class Registration {

        @Test
        @DisplayName("引当情報を登録できる")
        void canRegisterAllocation() {
            // Arrange
            Allocation allocation = createAllocation(requirementId, AllocationType.INVENTORY);

            // Act
            allocationRepository.save(allocation);

            // Assert
            List<Allocation> found = allocationRepository.findByRequirementId(requirementId);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getAllocationType()).isEqualTo(AllocationType.INVENTORY);
            assertThat(found.get(0).getAllocatedQuantity()).isEqualByComparingTo(new BigDecimal("20.00"));
        }

        @Test
        @DisplayName("各引当区分を登録できる")
        void canRegisterAllAllocationTypes() {
            // Arrange & Act & Assert
            for (AllocationType allocationType : AllocationType.values()) {
                allocationRepository.deleteAll();
                Allocation allocation = Allocation.builder()
                        .requirementId(requirementId)
                        .allocationType(allocationType)
                        .allocationDate(LocalDate.of(2024, 1, 5))
                        .allocatedQuantity(new BigDecimal("10.00"))
                        .locationCode("WH001")
                        .build();
                allocationRepository.save(allocation);

                List<Allocation> found = allocationRepository.findByRequirementId(requirementId);
                assertThat(found).hasSize(1);
                assertThat(found.get(0).getAllocationType()).isEqualTo(allocationType);
                assertThat(found.get(0).getAllocationType().getDisplayName())
                        .isEqualTo(allocationType.getDisplayName());
            }
        }

        @Test
        @DisplayName("オーダIDを関連付けて登録できる")
        void canRegisterWithOrderId() {
            // Arrange
            Allocation allocation = Allocation.builder()
                    .requirementId(requirementId)
                    .allocationType(AllocationType.PURCHASE_ORDER)
                    .orderId(orderId)
                    .allocationDate(LocalDate.of(2024, 1, 5))
                    .allocatedQuantity(new BigDecimal("30.00"))
                    .locationCode("WH001")
                    .build();

            // Act
            allocationRepository.save(allocation);

            // Assert
            List<Allocation> found = allocationRepository.findByRequirementId(requirementId);
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getOrderId()).isEqualTo(orderId);
            assertThat(found.get(0).getAllocationType()).isEqualTo(AllocationType.PURCHASE_ORDER);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            allocationRepository.save(createAllocation(requirementId, AllocationType.INVENTORY));
            allocationRepository.save(createAllocation(requirementId, AllocationType.PURCHASE_ORDER));
            allocationRepository.save(createAllocation(requirementId, AllocationType.MANUFACTURING_ORDER));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            List<Allocation> allocations = allocationRepository.findByRequirementId(requirementId);
            assertThat(allocations).isNotEmpty();
            Integer id = allocations.get(0).getId();

            // Act
            Optional<Allocation> found = allocationRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getRequirementId()).isEqualTo(requirementId);
        }

        @Test
        @DisplayName("所要IDで検索できる")
        void canFindByRequirementId() {
            // Act
            List<Allocation> found = allocationRepository.findByRequirementId(requirementId);

            // Assert
            assertThat(found).hasSize(3);
            assertThat(found).allMatch(alloc -> alloc.getRequirementId().equals(requirementId));
        }

        @Test
        @DisplayName("存在しない所要IDで検索すると空リストを返す")
        void returnsEmptyListForNonExistentRequirementId() {
            // Act
            List<Allocation> found = allocationRepository.findByRequirementId(99_999);

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Allocation> all = allocationRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("複数引当のシナリオ")
    class MultipleAllocationScenario {

        @Test
        @DisplayName("1つの所要に複数の引当を登録できる")
        void canRegisterMultipleAllocationsToOneRequirement() {
            // Arrange
            Allocation invAllocation = Allocation.builder()
                    .requirementId(requirementId)
                    .allocationType(AllocationType.INVENTORY)
                    .allocationDate(LocalDate.of(2024, 1, 5))
                    .allocatedQuantity(new BigDecimal("10.00"))
                    .locationCode("WH001")
                    .build();

            Allocation poAllocation = Allocation.builder()
                    .requirementId(requirementId)
                    .allocationType(AllocationType.PURCHASE_ORDER)
                    .orderId(orderId)
                    .allocationDate(LocalDate.of(2024, 1, 7))
                    .allocatedQuantity(new BigDecimal("20.00"))
                    .locationCode("WH001")
                    .build();

            Allocation moAllocation = Allocation.builder()
                    .requirementId(requirementId)
                    .allocationType(AllocationType.MANUFACTURING_ORDER)
                    .allocationDate(LocalDate.of(2024, 1, 10))
                    .allocatedQuantity(new BigDecimal("20.00"))
                    .locationCode("WH002")
                    .build();

            // Act
            allocationRepository.save(invAllocation);
            allocationRepository.save(poAllocation);
            allocationRepository.save(moAllocation);

            // Assert
            List<Allocation> found = allocationRepository.findByRequirementId(requirementId);
            assertThat(found).hasSize(3);

            BigDecimal totalAllocated = found.stream()
                    .map(Allocation::getAllocatedQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(totalAllocated).isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }
}
