package com.example.pms.infrastructure.in.seed;

import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.application.port.out.StockRepository;
import com.example.pms.application.port.out.WorkOrderDetailRepository;
import com.example.pms.application.port.out.WorkOrderRepository;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.OrderType;
import com.example.pms.domain.model.plan.PlanStatus;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderDetail;
import com.example.pms.domain.model.process.WorkOrderStatus;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * トランザクションデータ Seeder.
 * chapter31.md 準拠の E 社トランザクションデータを投入する。
 */
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
public class TransactionDataSeeder {

    private static final LocalDate EFFECTIVE_DATE = LocalDate.of(2025, 1, 1);

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderDetailRepository workOrderDetailRepository;

    /**
     * すべてのトランザクションデータを投入.
     */
    public void seedAll() {
        log.info("トランザクションデータを投入中...");
        seedStocks();
        seedOrders();
        seedPurchaseOrders();
        seedWorkOrders();
        log.info("トランザクションデータ投入完了");
    }

    /**
     * すべてのトランザクションデータを削除.
     */
    public void cleanAll() {
        log.info("トランザクションデータを削除中...");
        workOrderDetailRepository.deleteAll();
        workOrderRepository.deleteAll();
        purchaseOrderDetailRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        log.info("トランザクションデータ削除完了");
    }

    /**
     * 在庫情報を投入.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedStocks() {
        log.info("在庫情報を投入中...");

        List<Stock> stocks = List.of(
            // 材料倉庫
            Stock.builder().locationCode("WH-MAT").itemCode("MAT-001")
                .stockQuantity(new BigDecimal("800")).passedQuantity(new BigDecimal("800"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-MAT").itemCode("MAT-002")
                .stockQuantity(new BigDecimal("150")).passedQuantity(new BigDecimal("150"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-MAT").itemCode("MAT-003")
                .stockQuantity(new BigDecimal("450")).passedQuantity(new BigDecimal("450"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-MAT").itemCode("MAT-004")
                .stockQuantity(new BigDecimal("350")).passedQuantity(new BigDecimal("350"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-MAT").itemCode("MAT-010")
                .stockQuantity(new BigDecimal("600")).passedQuantity(new BigDecimal("600"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),

            // 部品倉庫
            Stock.builder().locationCode("WH-PART").itemCode("PART-001")
                .stockQuantity(new BigDecimal("200")).passedQuantity(new BigDecimal("200"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("PART-002")
                .stockQuantity(new BigDecimal("150")).passedQuantity(new BigDecimal("150"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("PART-003")
                .stockQuantity(new BigDecimal("80")).passedQuantity(new BigDecimal("80"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("PART-004")
                .stockQuantity(new BigDecimal("120")).passedQuantity(new BigDecimal("120"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("PART-005")
                .stockQuantity(new BigDecimal("300")).passedQuantity(new BigDecimal("300"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),

            // 半製品在庫
            Stock.builder().locationCode("WH-PART").itemCode("SEMI-A001")
                .stockQuantity(new BigDecimal("50")).passedQuantity(new BigDecimal("50"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("SEMI-B001")
                .stockQuantity(new BigDecimal("30")).passedQuantity(new BigDecimal("30"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("SEMI-B002")
                .stockQuantity(new BigDecimal("40")).passedQuantity(new BigDecimal("40"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("SEMI-B003")
                .stockQuantity(new BigDecimal("40")).passedQuantity(new BigDecimal("40"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PART").itemCode("SEMI-C001")
                .stockQuantity(new BigDecimal("60")).passedQuantity(new BigDecimal("60"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),

            // 製品倉庫
            Stock.builder().locationCode("WH-PROD").itemCode("PROD-A001")
                .stockQuantity(new BigDecimal("80")).passedQuantity(new BigDecimal("80"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PROD").itemCode("PROD-B001")
                .stockQuantity(new BigDecimal("45")).passedQuantity(new BigDecimal("45"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build(),
            Stock.builder().locationCode("WH-PROD").itemCode("PROD-C001")
                .stockQuantity(new BigDecimal("60")).passedQuantity(new BigDecimal("60"))
                .defectiveQuantity(BigDecimal.ZERO).uninspectedQuantity(BigDecimal.ZERO).build()
        );

        int count = 0;
        for (Stock stock : stocks) {
            if (stockRepository.findByLocationAndItem(stock.getLocationCode(), stock.getItemCode()).isEmpty()) {
                stockRepository.save(stock);
                count++;
            }
        }
        log.info("在庫情報 {}件 投入完了", count);
    }

    /**
     * オーダ情報を投入.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedOrders() {
        log.info("オーダ情報を投入中...");

        List<Order> orders = List.of(
            // 製造オーダ
            Order.builder().orderNumber("MO-2025-001").orderType(OrderType.MANUFACTURING)
                .itemCode("PROD-A001").planQuantity(new BigDecimal("100"))
                .startDate(LocalDate.of(2025, 1, 15))
                .dueDate(LocalDate.of(2025, 1, 31))
                .locationCode("WH-PROD").status(PlanStatus.CONFIRMED).build(),
            Order.builder().orderNumber("MO-2025-002").orderType(OrderType.MANUFACTURING)
                .itemCode("PROD-A001").planQuantity(new BigDecimal("100"))
                .startDate(LocalDate.of(2025, 2, 1))
                .dueDate(LocalDate.of(2025, 2, 7))
                .locationCode("WH-PROD").status(PlanStatus.DRAFT).build(),
            Order.builder().orderNumber("MO-2025-003").orderType(OrderType.MANUFACTURING)
                .itemCode("PROD-B001").planQuantity(new BigDecimal("50"))
                .startDate(LocalDate.of(2025, 1, 20))
                .dueDate(LocalDate.of(2025, 1, 31))
                .locationCode("WH-PROD").status(PlanStatus.CONFIRMED).build(),
            Order.builder().orderNumber("MO-2025-004").orderType(OrderType.MANUFACTURING)
                .itemCode("PROD-C001").planQuantity(new BigDecimal("80"))
                .startDate(LocalDate.of(2025, 1, 10))
                .dueDate(LocalDate.of(2025, 1, 20))
                .locationCode("WH-PROD").status(PlanStatus.CONFIRMED).build(),

            // 購買オーダ
            Order.builder().orderNumber("PO-2025-001").orderType(OrderType.PURCHASE)
                .itemCode("MAT-001").planQuantity(new BigDecimal("200"))
                .startDate(LocalDate.of(2025, 1, 6))
                .dueDate(LocalDate.of(2025, 1, 20))
                .locationCode("WH-MAT").status(PlanStatus.CONFIRMED).build(),
            Order.builder().orderNumber("PO-2025-002").orderType(OrderType.PURCHASE)
                .itemCode("MAT-003").planQuantity(new BigDecimal("150"))
                .startDate(LocalDate.of(2025, 1, 8))
                .dueDate(LocalDate.of(2025, 1, 22))
                .locationCode("WH-MAT").status(PlanStatus.CONFIRMED).build(),
            Order.builder().orderNumber("PO-2025-003").orderType(OrderType.PURCHASE)
                .itemCode("PART-001").planQuantity(new BigDecimal("100"))
                .startDate(LocalDate.of(2025, 1, 10))
                .dueDate(LocalDate.of(2025, 1, 17))
                .locationCode("WH-PART").status(PlanStatus.CONFIRMED).build()
        );

        int count = 0;
        for (Order order : orders) {
            if (orderRepository.findByOrderNumber(order.getOrderNumber()).isEmpty()) {
                orderRepository.save(order);
                count++;
            }
        }
        log.info("オーダ情報 {}件 投入完了", count);
    }

    /**
     * 発注データを投入.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedPurchaseOrders() {
        log.info("発注データを投入中...");

        // 発注1: MAT-001
        if (purchaseOrderRepository.findByPurchaseOrderNumber("PUR-2025-001").isEmpty()) {
            PurchaseOrder po1 = PurchaseOrder.builder()
                .purchaseOrderNumber("PUR-2025-001")
                .orderDate(LocalDate.of(2025, 1, 10))
                .supplierCode("SUP-001")
                .status(PurchaseOrderStatus.ORDERED)
                .build();
            purchaseOrderRepository.save(po1);

            PurchaseOrderDetail detail1 = PurchaseOrderDetail.builder()
                .purchaseOrderNumber("PUR-2025-001")
                .lineNumber(1)
                .itemCode("MAT-001")
                .miscellaneousItemFlag(false)
                .orderQuantity(new BigDecimal("200"))
                .orderUnitPrice(new BigDecimal("1500"))
                .orderAmount(new BigDecimal("300000"))
                .taxAmount(new BigDecimal("30000"))
                .expectedReceivingDate(LocalDate.of(2025, 1, 20))
                .deliveryLocationCode("WH-MAT")
                .receivedQuantity(BigDecimal.ZERO)
                .inspectedQuantity(BigDecimal.ZERO)
                .acceptedQuantity(BigDecimal.ZERO)
                .completedFlag(false)
                .build();
            purchaseOrderDetailRepository.save(detail1);
        }

        // 発注2: MAT-003
        if (purchaseOrderRepository.findByPurchaseOrderNumber("PUR-2025-002").isEmpty()) {
            PurchaseOrder po2 = PurchaseOrder.builder()
                .purchaseOrderNumber("PUR-2025-002")
                .orderDate(LocalDate.of(2025, 1, 10))
                .supplierCode("SUP-002")
                .status(PurchaseOrderStatus.ORDERED)
                .build();
            purchaseOrderRepository.save(po2);

            PurchaseOrderDetail detail2 = PurchaseOrderDetail.builder()
                .purchaseOrderNumber("PUR-2025-002")
                .lineNumber(1)
                .itemCode("MAT-003")
                .miscellaneousItemFlag(false)
                .orderQuantity(new BigDecimal("150"))
                .orderUnitPrice(new BigDecimal("2000"))
                .orderAmount(new BigDecimal("300000"))
                .taxAmount(new BigDecimal("30000"))
                .expectedReceivingDate(LocalDate.of(2025, 1, 22))
                .deliveryLocationCode("WH-MAT")
                .receivedQuantity(BigDecimal.ZERO)
                .inspectedQuantity(BigDecimal.ZERO)
                .acceptedQuantity(BigDecimal.ZERO)
                .completedFlag(false)
                .build();
            purchaseOrderDetailRepository.save(detail2);
        }

        // 発注3: PART-001
        if (purchaseOrderRepository.findByPurchaseOrderNumber("PUR-2025-003").isEmpty()) {
            PurchaseOrder po3 = PurchaseOrder.builder()
                .purchaseOrderNumber("PUR-2025-003")
                .orderDate(LocalDate.of(2025, 1, 12))
                .supplierCode("SUP-003")
                .status(PurchaseOrderStatus.ORDERED)
                .build();
            purchaseOrderRepository.save(po3);

            PurchaseOrderDetail detail3 = PurchaseOrderDetail.builder()
                .purchaseOrderNumber("PUR-2025-003")
                .lineNumber(1)
                .itemCode("PART-001")
                .miscellaneousItemFlag(false)
                .orderQuantity(new BigDecimal("100"))
                .orderUnitPrice(new BigDecimal("850"))
                .orderAmount(new BigDecimal("85000"))
                .taxAmount(new BigDecimal("8500"))
                .expectedReceivingDate(LocalDate.of(2025, 1, 17))
                .deliveryLocationCode("WH-PART")
                .receivedQuantity(BigDecimal.ZERO)
                .inspectedQuantity(BigDecimal.ZERO)
                .acceptedQuantity(BigDecimal.ZERO)
                .completedFlag(false)
                .build();
            purchaseOrderDetailRepository.save(detail3);
        }

        log.info("発注データ 3件 投入完了");
    }

    /**
     * 作業指示データを投入.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedWorkOrders() {
        log.info("作業指示データを投入中...");

        // 作業指示1: MO-2025-001 用
        if (workOrderRepository.findByWorkOrderNumber("WO-2025-001").isEmpty()) {
            WorkOrder wo1 = WorkOrder.builder()
                .workOrderNumber("WO-2025-001")
                .orderNumber("MO-2025-001")
                .workOrderDate(LocalDate.of(2025, 1, 15))
                .itemCode("PROD-A001")
                .orderQuantity(new BigDecimal("100"))
                .locationCode("LINE1")
                .plannedStartDate(LocalDate.of(2025, 1, 20))
                .plannedEndDate(LocalDate.of(2025, 1, 25))
                .completedQuantity(BigDecimal.ZERO)
                .totalGoodQuantity(BigDecimal.ZERO)
                .totalDefectQuantity(BigDecimal.ZERO)
                .status(WorkOrderStatus.IN_PROGRESS)
                .completedFlag(false)
                .build();
            workOrderRepository.save(wo1);

            // 作業指示明細1: 組立
            WorkOrderDetail detail1 = WorkOrderDetail.builder()
                .workOrderNumber("WO-2025-001")
                .sequence(1)
                .processCode("ASM")
                .build();
            workOrderDetailRepository.save(detail1);

            // 作業指示明細2: 出荷検査
            WorkOrderDetail detail2 = WorkOrderDetail.builder()
                .workOrderNumber("WO-2025-001")
                .sequence(2)
                .processCode("INS-SHIP")
                .build();
            workOrderDetailRepository.save(detail2);
        }

        // 作業指示2: MO-2025-003 用
        if (workOrderRepository.findByWorkOrderNumber("WO-2025-002").isEmpty()) {
            WorkOrder wo2 = WorkOrder.builder()
                .workOrderNumber("WO-2025-002")
                .orderNumber("MO-2025-003")
                .workOrderDate(LocalDate.of(2025, 1, 20))
                .itemCode("PROD-B001")
                .orderQuantity(new BigDecimal("50"))
                .locationCode("LINE1")
                .plannedStartDate(LocalDate.of(2025, 1, 25))
                .plannedEndDate(LocalDate.of(2025, 1, 30))
                .completedQuantity(BigDecimal.ZERO)
                .totalGoodQuantity(BigDecimal.ZERO)
                .totalDefectQuantity(BigDecimal.ZERO)
                .status(WorkOrderStatus.NOT_STARTED)
                .completedFlag(false)
                .build();
            workOrderRepository.save(wo2);

            // 作業指示明細: 最終組立
            WorkOrderDetail detail = WorkOrderDetail.builder()
                .workOrderNumber("WO-2025-002")
                .sequence(1)
                .processCode("FINAL-ASM")
                .build();
            workOrderDetailRepository.save(detail);
        }

        // 作業指示3: 完了済み（SEMI-A001 製造）
        if (workOrderRepository.findByWorkOrderNumber("WO-2025-003").isEmpty()) {
            WorkOrder wo3 = WorkOrder.builder()
                .workOrderNumber("WO-2025-003")
                .orderNumber("MO-2025-001")
                .workOrderDate(LocalDate.of(2025, 1, 12))
                .itemCode("SEMI-A001")
                .orderQuantity(new BigDecimal("120"))
                .locationCode("LINE1")
                .plannedStartDate(LocalDate.of(2025, 1, 15))
                .plannedEndDate(LocalDate.of(2025, 1, 17))
                .actualStartDate(LocalDate.of(2025, 1, 15))
                .actualEndDate(LocalDate.of(2025, 1, 16))
                .completedQuantity(new BigDecimal("118"))
                .totalGoodQuantity(new BigDecimal("115"))
                .totalDefectQuantity(new BigDecimal("3"))
                .status(WorkOrderStatus.COMPLETED)
                .completedFlag(true)
                .build();
            workOrderRepository.save(wo3);

            WorkOrderDetail detail = WorkOrderDetail.builder()
                .workOrderNumber("WO-2025-003")
                .sequence(1)
                .processCode("LATHE")
                .build();
            workOrderDetailRepository.save(detail);
        }

        log.info("作業指示データ 3件 投入完了");
    }
}
