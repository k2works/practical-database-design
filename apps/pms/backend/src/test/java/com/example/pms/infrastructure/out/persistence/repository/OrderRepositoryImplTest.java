package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.MpsRepository;
import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.OrderType;
import com.example.pms.domain.model.plan.PlanStatus;
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
 * オーダ情報リポジトリテスト.
 */
@DisplayName("オーダ情報リポジトリ")
class OrderRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MpsRepository mpsRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        mpsRepository.deleteAll();
    }

    private Order createOrder(String orderNumber, OrderType orderType, PlanStatus status) {
        return Order.builder()
                .orderNumber(orderNumber)
                .orderType(orderType)
                .itemCode("ITEM001")
                .startDate(LocalDate.of(2024, 1, 1))
                .dueDate(LocalDate.of(2024, 1, 15))
                .planQuantity(new BigDecimal("100.00"))
                .locationCode("WH001")
                .status(status)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    private MasterProductionSchedule createMps(String mpsNumber) {
        return MasterProductionSchedule.builder()
                .mpsNumber(mpsNumber)
                .planDate(LocalDate.of(2024, 1, 1))
                .itemCode("PROD001")
                .planQuantity(new BigDecimal("100.00"))
                .dueDate(LocalDate.of(2024, 1, 15))
                .status(PlanStatus.DRAFT)
                .locationCode("WH001")
                .build();
    }

    @Nested
    @DisplayName("登録")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    class Registration {

        @Test
        @DisplayName("オーダを登録できる")
        void canRegisterOrder() {
            // Arrange
            Order order = createOrder("ORD001", OrderType.MANUFACTURING, PlanStatus.DRAFT);

            // Act
            orderRepository.save(order);

            // Assert
            Optional<Order> found = orderRepository.findByOrderNumber("ORD001");
            assertThat(found).isPresent();
            assertThat(found.get().getOrderType()).isEqualTo(OrderType.MANUFACTURING);
            assertThat(found.get().getStatus()).isEqualTo(PlanStatus.DRAFT);
            assertThat(found.get().getPlanQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("各オーダ種別を登録できる")
        void canRegisterAllOrderTypes() {
            // Arrange & Act & Assert
            for (OrderType orderType : OrderType.values()) {
                Order order = Order.builder()
                        .orderNumber("ORD_" + orderType.name())
                        .orderType(orderType)
                        .itemCode("ITEM001")
                        .startDate(LocalDate.of(2024, 1, 1))
                        .dueDate(LocalDate.of(2024, 1, 15))
                        .planQuantity(new BigDecimal("100.00"))
                        .locationCode("WH001")
                        .status(PlanStatus.DRAFT)
                        .build();
                orderRepository.save(order);

                Optional<Order> found = orderRepository.findByOrderNumber("ORD_" + orderType.name());
                assertThat(found).isPresent();
                assertThat(found.get().getOrderType()).isEqualTo(orderType);
                assertThat(found.get().getOrderType().getDisplayName()).isEqualTo(orderType.getDisplayName());
            }
        }

        @Test
        @DisplayName("MPS IDを関連付けて登録できる")
        void canRegisterWithMpsId() {
            // Arrange
            MasterProductionSchedule mps = createMps("MPS001");
            mpsRepository.save(mps);
            Optional<MasterProductionSchedule> savedMps = mpsRepository.findByMpsNumber("MPS001");
            assertThat(savedMps).isPresent();

            Order order = Order.builder()
                    .orderNumber("ORD001")
                    .orderType(OrderType.MANUFACTURING)
                    .itemCode("ITEM001")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .dueDate(LocalDate.of(2024, 1, 15))
                    .planQuantity(new BigDecimal("100.00"))
                    .locationCode("WH001")
                    .status(PlanStatus.DRAFT)
                    .mpsId(savedMps.get().getId())
                    .build();

            // Act
            orderRepository.save(order);

            // Assert
            Optional<Order> found = orderRepository.findByOrderNumber("ORD001");
            assertThat(found).isPresent();
            assertThat(found.get().getMpsId()).isEqualTo(savedMps.get().getId());
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            orderRepository.save(createOrder("ORD001", OrderType.PURCHASE, PlanStatus.DRAFT));
            orderRepository.save(createOrder("ORD002", OrderType.MANUFACTURING, PlanStatus.CONFIRMED));
            orderRepository.save(createOrder("ORD003", OrderType.PURCHASE, PlanStatus.DRAFT));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Order> order = orderRepository.findByOrderNumber("ORD001");
            assertThat(order).isPresent();
            Integer id = order.get().getId();

            // Act
            Optional<Order> found = orderRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getOrderNumber()).isEqualTo("ORD001");
        }

        @Test
        @DisplayName("オーダ番号で検索できる")
        void canFindByOrderNumber() {
            // Act
            Optional<Order> found = orderRepository.findByOrderNumber("ORD002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getOrderType()).isEqualTo(OrderType.MANUFACTURING);
        }

        @Test
        @DisplayName("存在しないオーダ番号で検索すると空を返す")
        void returnsEmptyForNonExistentOrderNumber() {
            // Act
            Optional<Order> found = orderRepository.findByOrderNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("MPS IDで検索できる")
        void canFindByMpsId() {
            // Arrange
            mpsRepository.save(createMps("MPS001"));
            Optional<MasterProductionSchedule> savedMps = mpsRepository.findByMpsNumber("MPS001");
            assertThat(savedMps).isPresent();

            Order order1 = Order.builder()
                    .orderNumber("ORD_MPS1")
                    .orderType(OrderType.MANUFACTURING)
                    .itemCode("ITEM001")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .dueDate(LocalDate.of(2024, 1, 15))
                    .planQuantity(new BigDecimal("100.00"))
                    .locationCode("WH001")
                    .status(PlanStatus.DRAFT)
                    .mpsId(savedMps.get().getId())
                    .build();
            Order order2 = Order.builder()
                    .orderNumber("ORD_MPS2")
                    .orderType(OrderType.MANUFACTURING)
                    .itemCode("ITEM002")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .dueDate(LocalDate.of(2024, 1, 15))
                    .planQuantity(new BigDecimal("50.00"))
                    .locationCode("WH001")
                    .status(PlanStatus.DRAFT)
                    .mpsId(savedMps.get().getId())
                    .build();
            orderRepository.save(order1);
            orderRepository.save(order2);

            // Act
            List<Order> orders = orderRepository.findByMpsId(savedMps.get().getId());

            // Assert
            assertThat(orders).hasSize(2);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Order> all = orderRepository.findAll();

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
            Order order = createOrder("ORD001", OrderType.MANUFACTURING, PlanStatus.DRAFT);
            orderRepository.save(order);
            Optional<Order> saved = orderRepository.findByOrderNumber("ORD001");
            assertThat(saved).isPresent();

            // Act
            orderRepository.updateStatus(saved.get().getId(), PlanStatus.CONFIRMED);

            // Assert
            Optional<Order> updated = orderRepository.findByOrderNumber("ORD001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getStatus()).isEqualTo(PlanStatus.CONFIRMED);
        }

        @Test
        @DisplayName("親オーダIDを更新できる")
        void canUpdateParentOrderId() {
            // Arrange
            Order parentOrder = createOrder("ORD_PARENT", OrderType.MANUFACTURING, PlanStatus.DRAFT);
            orderRepository.save(parentOrder);
            Optional<Order> savedParent = orderRepository.findByOrderNumber("ORD_PARENT");
            assertThat(savedParent).isPresent();

            Order childOrder = createOrder("ORD_CHILD", OrderType.MANUFACTURING, PlanStatus.DRAFT);
            orderRepository.save(childOrder);
            Optional<Order> savedChild = orderRepository.findByOrderNumber("ORD_CHILD");
            assertThat(savedChild).isPresent();

            // Act
            orderRepository.updateParentOrderId(savedChild.get().getId(), savedParent.get().getId());

            // Assert
            Optional<Order> updated = orderRepository.findByOrderNumber("ORD_CHILD");
            assertThat(updated).isPresent();
            assertThat(updated.get().getParentOrderId()).isEqualTo(savedParent.get().getId());

            // Verify parent order has children
            List<Order> children = orderRepository.findByParentOrderId(savedParent.get().getId());
            assertThat(children).hasSize(1);
            assertThat(children.get(0).getOrderNumber()).isEqualTo("ORD_CHILD");
        }
    }
}
