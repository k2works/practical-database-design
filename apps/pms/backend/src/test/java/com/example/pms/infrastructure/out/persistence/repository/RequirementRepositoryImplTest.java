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
 * 所要情報リポジトリテスト.
 */
@DisplayName("所要情報リポジトリ")
class RequirementRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Integer orderId;

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
    }

    private Requirement createRequirement(String requirementNumber, Integer orderId) {
        return Requirement.builder()
                .requirementNumber(requirementNumber)
                .orderId(orderId)
                .itemCode("PART001")
                .dueDate(LocalDate.of(2024, 1, 10))
                .requiredQuantity(new BigDecimal("50.00"))
                .allocatedQuantity(BigDecimal.ZERO)
                .shortageQuantity(new BigDecimal("50.00"))
                .locationCode("WH001")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("所要情報を登録できる")
        void canRegisterRequirement() {
            // Arrange
            Requirement requirement = createRequirement("REQ001", orderId);

            // Act
            requirementRepository.save(requirement);

            // Assert
            Optional<Requirement> found = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("PART001");
            assertThat(found.get().getRequiredQuantity()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(found.get().getOrderId()).isEqualTo(orderId);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            requirementRepository.save(createRequirement("REQ001", orderId));
            requirementRepository.save(createRequirement("REQ002", orderId));
            requirementRepository.save(createRequirement("REQ003", orderId));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Requirement> req = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(req).isPresent();
            Integer id = req.get().getId();

            // Act
            Optional<Requirement> found = requirementRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getRequirementNumber()).isEqualTo("REQ001");
        }

        @Test
        @DisplayName("所要番号で検索できる")
        void canFindByRequirementNumber() {
            // Act
            Optional<Requirement> found = requirementRepository.findByRequirementNumber("REQ002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("PART001");
        }

        @Test
        @DisplayName("存在しない所要番号で検索すると空を返す")
        void returnsEmptyForNonExistentRequirementNumber() {
            // Act
            Optional<Requirement> found = requirementRepository.findByRequirementNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("オーダIDで検索できる")
        void canFindByOrderId() {
            // Act
            List<Requirement> requirements = requirementRepository.findByOrderId(orderId);

            // Assert
            assertThat(requirements).hasSize(3);
            assertThat(requirements).allMatch(req -> req.getOrderId().equals(orderId));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Requirement> all = requirementRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("引当情報を更新できる")
        void canUpdateAllocation() {
            // Arrange
            Requirement requirement = createRequirement("REQ001", orderId);
            requirementRepository.save(requirement);
            Optional<Requirement> saved = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(saved).isPresent();
            assertThat(saved.get().getAllocatedQuantity()).isEqualByComparingTo(BigDecimal.ZERO);

            // Act
            BigDecimal allocatedQty = new BigDecimal("30.00");
            BigDecimal shortageQty = new BigDecimal("20.00");
            requirementRepository.updateAllocation(saved.get().getId(), allocatedQty, shortageQty);

            // Assert
            Optional<Requirement> updated = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getAllocatedQuantity()).isEqualByComparingTo(allocatedQty);
            assertThat(updated.get().getShortageQuantity()).isEqualByComparingTo(shortageQty);
        }

        @Test
        @DisplayName("全数引当済みの場合は不足数量がゼロになる")
        void shortageBecomesZeroWhenFullyAllocated() {
            // Arrange
            Requirement requirement = createRequirement("REQ001", orderId);
            requirementRepository.save(requirement);
            Optional<Requirement> saved = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(saved).isPresent();

            // Act
            BigDecimal requiredQty = saved.get().getRequiredQuantity();
            requirementRepository.updateAllocation(saved.get().getId(), requiredQty, BigDecimal.ZERO);

            // Assert
            Optional<Requirement> updated = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getAllocatedQuantity()).isEqualByComparingTo(requiredQty);
            assertThat(updated.get().getShortageQuantity()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("リレーション")
    class Relation {

        @Test
        @DisplayName("所要情報と引当を同時に取得できる")
        void canFindRequirementWithAllocations() {
            // Arrange
            Requirement requirement = createRequirement("REQ001", orderId);
            requirementRepository.save(requirement);
            Optional<Requirement> savedReq = requirementRepository.findByRequirementNumber("REQ001");
            assertThat(savedReq).isPresent();

            // Create allocations
            Allocation allocation1 = Allocation.builder()
                    .requirementId(savedReq.get().getId())
                    .allocationType(AllocationType.INVENTORY)
                    .allocationDate(LocalDate.of(2024, 1, 5))
                    .allocatedQuantity(new BigDecimal("20.00"))
                    .locationCode("WH001")
                    .build();
            allocationRepository.save(allocation1);

            Allocation allocation2 = Allocation.builder()
                    .requirementId(savedReq.get().getId())
                    .allocationType(AllocationType.PURCHASE_ORDER)
                    .allocationDate(LocalDate.of(2024, 1, 6))
                    .allocatedQuantity(new BigDecimal("30.00"))
                    .locationCode("WH001")
                    .build();
            allocationRepository.save(allocation2);

            // Act
            Optional<Requirement> found = requirementRepository.findByRequirementNumberWithAllocations("REQ001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getRequirementNumber()).isEqualTo("REQ001");
            assertThat(found.get().getAllocations()).isNotNull();
            assertThat(found.get().getAllocations()).hasSize(2);
        }

        @Test
        @DisplayName("引当がない場合は空リストを返す")
        void returnsEmptyListWhenNoAllocations() {
            // Arrange
            Requirement requirement = createRequirement("REQ002", orderId);
            requirementRepository.save(requirement);

            // Act
            Optional<Requirement> found = requirementRepository.findByRequirementNumberWithAllocations("REQ002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getAllocations()).isNotNull();
            assertThat(found.get().getAllocations()).isEmpty();
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {

        @Test
        @DisplayName("デフォルトバージョンは1である")
        void defaultVersionIsOne() {
            // Arrange
            Requirement requirement = createRequirement("REQ001", orderId);

            // Act
            requirementRepository.save(requirement);
            Optional<Requirement> found = requirementRepository.findByRequirementNumber("REQ001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getVersion()).isEqualTo(1);
        }
    }
}
