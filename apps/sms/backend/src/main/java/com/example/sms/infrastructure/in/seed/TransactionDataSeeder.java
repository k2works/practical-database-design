package com.example.sms.infrastructure.in.seed;

import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.application.port.out.SalesRepository;
import com.example.sms.application.port.out.ShipmentRepository;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * トランザクションデータ Seeder.
 * B社事例に基づくトランザクションデータを投入する。
 */
@Component
public class TransactionDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(TransactionDataSeeder.class);

    private final InventoryRepository inventoryRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ShipmentRepository shipmentRepository;
    private final SalesRepository salesRepository;

    public TransactionDataSeeder(
            InventoryRepository inventoryRepository,
            SalesOrderRepository salesOrderRepository,
            ShipmentRepository shipmentRepository,
            SalesRepository salesRepository) {
        this.inventoryRepository = inventoryRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.shipmentRepository = shipmentRepository;
        this.salesRepository = salesRepository;
    }

    /**
     * すべてのトランザクションデータを投入.
     */
    public void seedAll(LocalDate effectiveDate) {
        seedInventories();
        seedOrders(effectiveDate);
    }

    /**
     * すべてのトランザクションデータを削除.
     */
    public void cleanAll() {
        salesRepository.deleteAll();
        shipmentRepository.deleteAll();
        salesOrderRepository.deleteAll();
        inventoryRepository.deleteAll();
    }

    private void seedInventories() {
        log.info("在庫情報を投入中...");

        List<Inventory> inventories = List.of(
            // 本社倉庫の在庫（牛肉）
            createInventory("WH-HQ", "BEEF-001", 50, 10),
            createInventory("WH-HQ", "BEEF-002", 80, 15),
            createInventory("WH-HQ", "BEEF-003", 100, 20),
            createInventory("WH-HQ", "BEEF-004", 30, 5),
            createInventory("WH-HQ", "BEEF-005", 150, 30),

            // 本社倉庫の在庫（豚肉）
            createInventory("WH-HQ", "PORK-001", 200, 30),
            createInventory("WH-HQ", "PORK-002", 250, 40),
            createInventory("WH-HQ", "PORK-003", 100, 15),
            createInventory("WH-HQ", "PORK-004", 300, 50),
            createInventory("WH-HQ", "PORK-005", 180, 25),

            // 本社倉庫の在庫（鶏肉）
            createInventory("WH-HQ", "CHKN-001", 300, 50),
            createInventory("WH-HQ", "CHKN-002", 350, 60),
            createInventory("WH-HQ", "CHKN-003", 200, 30),
            createInventory("WH-HQ", "CHKN-004", 180, 25),
            createInventory("WH-HQ", "CHKN-005", 150, 20),

            // 工場倉庫の在庫（加工品）
            createInventory("WH-FAC", "PROC-001", 100, 20),
            createInventory("WH-FAC", "PROC-002", 150, 30),
            createInventory("WH-FAC", "PROC-003", 200, 40),
            createInventory("WH-FAC", "PROC-004", 180, 35),
            createInventory("WH-FAC", "PROC-005", 300, 50)
        );

        inventories.forEach(inventoryRepository::save);
        log.info("在庫情報 {}件 投入完了", inventories.size());
    }

    private Inventory createInventory(String warehouseCode, String productCode,
                                        int quantity, int allocatedQuantity) {
        return Inventory.builder()
            .warehouseCode(warehouseCode)
            .productCode(productCode)
            .currentQuantity(new BigDecimal(quantity))
            .allocatedQuantity(new BigDecimal(allocatedQuantity))
            .orderedQuantity(BigDecimal.ZERO)
            .build();
    }

    private void seedOrders(LocalDate effectiveDate) {
        log.info("受注データを投入中...");

        // 受注1（百貨店向け）- 引当済み
        SalesOrder order1 = SalesOrder.builder()
            .orderNumber("ORD-2025-001")
            .orderDate(LocalDate.of(2025, 1, 10))
            .customerCode("CUS-001")
            .representativeCode("EMP-009")
            .requestedDeliveryDate(LocalDate.of(2025, 1, 15))
            .scheduledShippingDate(LocalDate.of(2025, 1, 14))
            .status(OrderStatus.ALLOCATED)
            .details(List.of(
                createOrderDetail(1, "BEEF-001", "黒毛和牛サーロイン", 10, 8000),
                createOrderDetail(2, "PROC-001", "ローストビーフ", 20, 3500)
            ))
            .build();
        calculateOrderTotals(order1);
        salesOrderRepository.save(order1);

        // 受注2（スーパー向け）- 引当済み
        SalesOrder order2 = SalesOrder.builder()
            .orderNumber("ORD-2025-002")
            .orderDate(LocalDate.of(2025, 1, 12))
            .customerCode("CUS-003")
            .representativeCode("EMP-009")
            .requestedDeliveryDate(LocalDate.of(2025, 1, 18))
            .scheduledShippingDate(LocalDate.of(2025, 1, 17))
            .status(OrderStatus.ALLOCATED)
            .details(List.of(
                createOrderDetail(1, "PORK-001", "豚ロース", 50, 1200),
                createOrderDetail(2, "CHKN-001", "鶏もも", 100, 480)
            ))
            .build();
        calculateOrderTotals(order2);
        salesOrderRepository.save(order2);

        // 受注3（ホテル向け）- 出荷済み
        SalesOrder order3 = SalesOrder.builder()
            .orderNumber("ORD-2025-003")
            .orderDate(LocalDate.of(2025, 1, 15))
            .customerCode("CUS-005")
            .representativeCode("EMP-010")
            .requestedDeliveryDate(LocalDate.of(2025, 1, 17))
            .scheduledShippingDate(LocalDate.of(2025, 1, 16))
            .status(OrderStatus.SHIPPED)
            .details(List.of(
                createOrderDetail(1, "BEEF-002", "黒毛和牛ロース", 30, 6000),
                createOrderDetail(2, "BEEF-003", "黒毛和牛カルビ", 25, 5500)
            ))
            .build();
        calculateOrderTotals(order3);
        salesOrderRepository.save(order3);

        log.info("受注データ 3件 投入完了");
        // 出荷・売上データは複雑な依存関係があるため、別途投入する
    }

    private SalesOrderDetail createOrderDetail(int lineNumber, String productCode,
                                                 String productName, int quantity, int unitPrice) {
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal price = new BigDecimal(unitPrice);
        BigDecimal amount = qty.multiply(price);
        BigDecimal taxRate = new BigDecimal("10.00");
        BigDecimal taxAmount = amount.multiply(taxRate).divide(new BigDecimal("100"));

        return SalesOrderDetail.builder()
            .lineNumber(lineNumber)
            .productCode(productCode)
            .productName(productName)
            .orderQuantity(qty)
            .allocatedQuantity(BigDecimal.ZERO)
            .shippedQuantity(BigDecimal.ZERO)
            .remainingQuantity(qty)
            .unitPrice(price)
            .amount(amount)
            .taxCategory(TaxCategory.EXCLUSIVE)
            .taxRate(taxRate)
            .taxAmount(taxAmount)
            .warehouseCode("WH-HQ")
            .build();
    }

    private void calculateOrderTotals(SalesOrder order) {
        BigDecimal orderAmount = order.getDetails().stream()
            .map(SalesOrderDetail::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = order.getDetails().stream()
            .map(SalesOrderDetail::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setOrderAmount(orderAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(orderAmount.add(taxAmount));
    }

}
