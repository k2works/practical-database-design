package com.example.sms.infrastructure.in.seed;

import com.example.sms.application.port.out.AccountsReceivableRepository;
import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.application.port.out.InvoiceRepository;
import com.example.sms.application.port.out.PayableBalanceRepository;
import com.example.sms.application.port.out.PaymentRepository;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.application.port.out.PurchaseRepository;
import com.example.sms.application.port.out.QuotationRepository;
import com.example.sms.application.port.out.ReceiptRepository;
import com.example.sms.application.port.out.ReceivingRepository;
import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.application.port.out.SalesRepository;
import com.example.sms.application.port.out.ShipmentRepository;
import com.example.sms.application.port.out.StocktakingRepository;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingDetail;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import com.example.sms.domain.model.invoice.AccountsReceivable;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.invoice.InvoiceType;
import com.example.sms.domain.model.payment.PayableBalance;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentMethod;
import com.example.sms.domain.model.payment.PaymentStatus;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptMethod;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.domain.model.purchase.PurchaseDetail;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingDetail;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationDetail;
import com.example.sms.domain.model.sales.QuotationStatus;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports",
    "PMD.CouplingBetweenObjects", "PMD.ExcessiveParameterList"})
public class TransactionDataSeeder {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionDataSeeder.class);
    private static final BigDecimal TAX_RATE = new BigDecimal("10.00");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    // 在庫管理リポジトリ
    private final InventoryRepository inventoryRepository;
    private final StocktakingRepository stocktakingRepository;

    // 販売管理リポジトリ
    private final QuotationRepository quotationRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ShipmentRepository shipmentRepository;
    private final SalesRepository salesRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptRepository receiptRepository;

    // 調達管理リポジトリ
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ReceivingRepository receivingRepository;
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;

    // 債権管理リポジトリ
    private final AccountsReceivableRepository accountsReceivableRepository;

    // 債務管理リポジトリ
    private final PayableBalanceRepository payableBalanceRepository;

    public TransactionDataSeeder(
            InventoryRepository inventoryRepository,
            StocktakingRepository stocktakingRepository,
            QuotationRepository quotationRepository,
            SalesOrderRepository salesOrderRepository,
            ShipmentRepository shipmentRepository,
            SalesRepository salesRepository,
            InvoiceRepository invoiceRepository,
            ReceiptRepository receiptRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            ReceivingRepository receivingRepository,
            PurchaseRepository purchaseRepository,
            PaymentRepository paymentRepository,
            AccountsReceivableRepository accountsReceivableRepository,
            PayableBalanceRepository payableBalanceRepository) {
        this.inventoryRepository = inventoryRepository;
        this.stocktakingRepository = stocktakingRepository;
        this.quotationRepository = quotationRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.shipmentRepository = shipmentRepository;
        this.salesRepository = salesRepository;
        this.invoiceRepository = invoiceRepository;
        this.receiptRepository = receiptRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.receivingRepository = receivingRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.accountsReceivableRepository = accountsReceivableRepository;
        this.payableBalanceRepository = payableBalanceRepository;
    }

    /**
     * すべてのトランザクションデータを投入.
     */
    @SuppressWarnings("java:S1172")
    public void seedAll(LocalDate effectiveDate) {
        // 在庫管理トランザクション
        seedInventories();
        seedStocktakings();

        // 販売管理トランザクション
        seedQuotations();
        seedOrders();
        seedShipments();
        seedSales();
        seedInvoices();
        seedReceipts();

        // 調達管理トランザクション
        seedPurchaseOrders();
        seedReceivings();
        seedPurchases();
        seedPayments();

        // 債権管理トランザクション
        seedAccountsReceivables();

        // 債務管理トランザクション
        seedPayableBalances();
    }

    /**
     * すべてのトランザクションデータを削除.
     */
    public void cleanAll() {
        // 債務管理トランザクション
        payableBalanceRepository.deleteAll();

        // 債権管理トランザクション
        accountsReceivableRepository.deleteAll();

        // 調達管理トランザクション（依存順）
        paymentRepository.deleteAll();
        purchaseRepository.deleteAll();
        receivingRepository.deleteAll();
        purchaseOrderRepository.deleteAll();

        // 販売管理トランザクション（依存順）
        receiptRepository.deleteAll();
        invoiceRepository.deleteAll();
        salesRepository.deleteAll();
        shipmentRepository.deleteAll();
        salesOrderRepository.deleteAll();
        quotationRepository.deleteAll();

        // 在庫管理トランザクション
        stocktakingRepository.deleteAll();
        inventoryRepository.deleteAll();
    }

    private void seedInventories() {
        LOG.info("在庫情報を投入中...");

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
        if (LOG.isInfoEnabled()) {
            LOG.info("在庫情報 {}件 投入完了", inventories.size());
        }
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

    private void seedStocktakings() {
        LOG.info("棚卸データを投入中...");

        // 棚卸1（本社倉庫）- 2024年12月度棚卸（確定済み）
        Stocktaking st1 = Stocktaking.builder()
            .stocktakingNumber("STK-2024-012")
            .warehouseCode("WH-HQ")
            .stocktakingDate(LocalDate.of(2024, 12, 28))
            .status(StocktakingStatus.CONFIRMED)
            .remarks("2024年12月度定期棚卸")
            .createdBy("EMP-011")
            .updatedBy("EMP-011")
            .details(List.of(
                createStocktakingDetail(1, "BEEF-001", null, null, 50, 48),
                createStocktakingDetail(2, "BEEF-002", null, null, 80, 80),
                createStocktakingDetail(3, "PORK-001", null, null, 200, 195),
                createStocktakingDetail(4, "CHKN-001", null, null, 300, 302)
            ))
            .build();
        stocktakingRepository.save(st1);

        // 棚卸2（工場倉庫）- 2025年1月度棚卸（実施中）
        Stocktaking st2 = Stocktaking.builder()
            .stocktakingNumber("STK-2025-001")
            .warehouseCode("WH-FAC")
            .stocktakingDate(LocalDate.of(2025, 1, 15))
            .status(StocktakingStatus.IN_PROGRESS)
            .remarks("2025年1月度定期棚卸")
            .createdBy("EMP-011")
            .updatedBy("EMP-011")
            .details(List.of(
                createStocktakingDetail(1, "PROC-001", null, null, 100, 98),
                createStocktakingDetail(2, "PROC-002", null, null, 150, null),
                createStocktakingDetail(3, "PROC-003", null, null, 200, null)
            ))
            .build();
        stocktakingRepository.save(st2);

        // 棚卸3（本社倉庫）- 2025年1月度棚卸（作成中）
        Stocktaking st3 = Stocktaking.builder()
            .stocktakingNumber("STK-2025-002")
            .warehouseCode("WH-HQ")
            .stocktakingDate(LocalDate.of(2025, 1, 20))
            .status(StocktakingStatus.DRAFT)
            .remarks("2025年1月度臨時棚卸（在庫差異調査）")
            .createdBy("EMP-011")
            .updatedBy("EMP-011")
            .details(List.of(
                createStocktakingDetail(1, "BEEF-003", null, null, 100, null),
                createStocktakingDetail(2, "BEEF-004", null, null, 30, null)
            ))
            .build();
        stocktakingRepository.save(st3);

        LOG.info("棚卸データ 3件 投入完了");
    }

    private StocktakingDetail createStocktakingDetail(int lineNumber, String productCode,
                                                        String locationCode, String lotNumber,
                                                        int bookQuantity, Integer actualQuantity) {
        BigDecimal bookQty = new BigDecimal(bookQuantity);
        BigDecimal actualQty = actualQuantity != null ? new BigDecimal(actualQuantity) : null;
        BigDecimal diffQty = actualQty != null ? actualQty.subtract(bookQty) : null;

        return StocktakingDetail.builder()
            .lineNumber(lineNumber)
            .productCode(productCode)
            .locationCode(locationCode)
            .lotNumber(lotNumber)
            .bookQuantity(bookQty)
            .actualQuantity(actualQty)
            .differenceQuantity(diffQty)
            .adjustedFlag(actualQty != null && diffQty != null && diffQty.signum() == 0)
            .build();
    }

    private void seedQuotations() {
        LOG.info("見積データを投入中...");

        // 見積1（百貨店向け）- 商談中
        Quotation quotation1 = Quotation.builder()
            .quotationNumber("QT-2025-001")
            .quotationDate(LocalDate.of(2025, 1, 5))
            .validUntil(LocalDate.of(2025, 2, 5))
            .customerCode("CUS-001")
            .salesRepCode("EMP-009")
            .subject("2025年1月分 和牛お取り寄せ")
            .status(QuotationStatus.NEGOTIATING)
            .details(List.of(
                createQuotationDetail(1, "BEEF-001", "黒毛和牛サーロイン", 15, "kg", 8000),
                createQuotationDetail(2, "BEEF-002", "黒毛和牛ロース", 10, "kg", 6000)
            ))
            .build();
        calculateQuotationTotals(quotation1);
        quotationRepository.save(quotation1);

        // 見積2（ホテル向け）- 受注確定
        Quotation quotation2 = Quotation.builder()
            .quotationNumber("QT-2025-002")
            .quotationDate(LocalDate.of(2025, 1, 8))
            .validUntil(LocalDate.of(2025, 2, 8))
            .customerCode("CUS-005")
            .salesRepCode("EMP-010")
            .subject("レストラン向け食材納品")
            .status(QuotationStatus.ORDERED)
            .details(List.of(
                createQuotationDetail(1, "BEEF-002", "黒毛和牛ロース", 30, "kg", 6000),
                createQuotationDetail(2, "BEEF-003", "黒毛和牛カルビ", 25, "kg", 5500),
                createQuotationDetail(3, "PROC-001", "ローストビーフ", 50, "個", 3500)
            ))
            .build();
        calculateQuotationTotals(quotation2);
        quotationRepository.save(quotation2);

        // 見積3（スーパー向け）- 失注
        Quotation quotation3 = Quotation.builder()
            .quotationNumber("QT-2025-003")
            .quotationDate(LocalDate.of(2024, 12, 20))
            .validUntil(LocalDate.of(2025, 1, 20))
            .customerCode("CUS-003")
            .salesRepCode("EMP-009")
            .subject("年末年始セール向け商品")
            .status(QuotationStatus.LOST)
            .remarks("価格面で他社に決定")
            .details(List.of(
                createQuotationDetail(1, "PORK-001", "豚ロース", 100, "kg", 1200),
                createQuotationDetail(2, "CHKN-001", "鶏もも", 200, "kg", 480)
            ))
            .build();
        calculateQuotationTotals(quotation3);
        quotationRepository.save(quotation3);

        LOG.info("見積データ 3件 投入完了");
    }

    private QuotationDetail createQuotationDetail(int lineNumber, String productCode,
                                                    String productName, int quantity,
                                                    String unit, int unitPrice) {
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal price = new BigDecimal(unitPrice);
        BigDecimal amount = qty.multiply(price);
        BigDecimal taxAmount = amount.multiply(TAX_RATE).divide(HUNDRED);

        return QuotationDetail.builder()
            .lineNumber(lineNumber)
            .productCode(productCode)
            .productName(productName)
            .quantity(qty)
            .unit(unit)
            .unitPrice(price)
            .amount(amount)
            .taxCategory(TaxCategory.EXCLUSIVE)
            .taxRate(TAX_RATE)
            .taxAmount(taxAmount)
            .build();
    }

    private void calculateQuotationTotals(Quotation quotation) {
        BigDecimal subtotal = quotation.getDetails().stream()
            .map(QuotationDetail::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = quotation.getDetails().stream()
            .map(QuotationDetail::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        quotation.setSubtotal(subtotal);
        quotation.setTaxAmount(taxAmount);
        quotation.setTotalAmount(subtotal.add(taxAmount));
    }

    private void seedOrders() {
        LOG.info("受注データを投入中...");

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

        LOG.info("受注データ 3件 投入完了");
    }

    private SalesOrderDetail createOrderDetail(int lineNumber, String productCode,
                                                 String productName, int quantity, int unitPrice) {
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal price = new BigDecimal(unitPrice);
        BigDecimal amount = qty.multiply(price);
        BigDecimal taxAmount = amount.multiply(TAX_RATE).divide(HUNDRED);

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
            .taxRate(TAX_RATE)
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

    private void seedShipments() {
        // 出荷データは受注IDへの外部キー参照が必要なため、初期シードでは投入しない
        // 受注→出荷の業務フローで生成される
        LOG.info("出荷データ: 依存関係のため初期シードではスキップ");
    }

    private void seedSales() {
        // 売上データは受注ID/出荷IDへの外部キー参照が必要なため、初期シードでは投入しない
        // 出荷→売上の業務フローで生成される
        LOG.info("売上データ: 依存関係のため初期シードではスキップ");
    }

    private void seedInvoices() {
        LOG.info("請求データを投入中...");

        // 2024年12月締め分の請求データ
        LocalDate closingDate = LocalDate.of(2024, 12, 31);
        LocalDate invoiceDate = LocalDate.of(2025, 1, 5);

        // 百貨店（CUS-001）- 発行済（一部入金）
        Invoice inv1 = Invoice.builder()
            .invoiceNumber("INV-2024-001")
            .invoiceDate(invoiceDate)
            .billingCode("CUS-001")
            .customerCode("CUS-001")
            .customerBranchNumber("00")
            .closingDate(closingDate)
            .invoiceType(InvoiceType.CLOSING)
            .previousBalance(new BigDecimal("500000"))
            .receiptAmount(new BigDecimal("500000"))
            .carriedBalance(BigDecimal.ZERO)
            .currentSalesAmount(new BigDecimal("180000"))
            .currentTaxAmount(new BigDecimal("18000"))
            .currentInvoiceAmount(new BigDecimal("198000"))
            .invoiceBalance(new BigDecimal("198000"))
            .dueDate(LocalDate.of(2025, 1, 31))
            .status(InvoiceStatus.ISSUED)
            .build();
        invoiceRepository.save(inv1);

        // レストラン（CUS-002）- 発行済
        Invoice inv2 = Invoice.builder()
            .invoiceNumber("INV-2024-002")
            .invoiceDate(invoiceDate)
            .billingCode("CUS-002")
            .customerCode("CUS-002")
            .customerBranchNumber("00")
            .closingDate(closingDate)
            .invoiceType(InvoiceType.CLOSING)
            .previousBalance(new BigDecimal("300000"))
            .receiptAmount(new BigDecimal("300000"))
            .carriedBalance(BigDecimal.ZERO)
            .currentSalesAmount(new BigDecimal("205000"))
            .currentTaxAmount(new BigDecimal("20500"))
            .currentInvoiceAmount(new BigDecimal("225500"))
            .invoiceBalance(new BigDecimal("225500"))
            .dueDate(LocalDate.of(2025, 1, 31))
            .status(InvoiceStatus.ISSUED)
            .build();
        invoiceRepository.save(inv2);

        // スーパー（CUS-003）- 入金済
        Invoice inv3 = Invoice.builder()
            .invoiceNumber("INV-2024-003")
            .invoiceDate(invoiceDate)
            .billingCode("CUS-003")
            .customerCode("CUS-003")
            .customerBranchNumber("00")
            .closingDate(closingDate)
            .invoiceType(InvoiceType.CLOSING)
            .previousBalance(new BigDecimal("250000"))
            .receiptAmount(new BigDecimal("418000"))
            .carriedBalance(BigDecimal.ZERO)
            .currentSalesAmount(new BigDecimal("168000"))
            .currentTaxAmount(new BigDecimal("16800"))
            .currentInvoiceAmount(new BigDecimal("184800"))
            .invoiceBalance(BigDecimal.ZERO)
            .dueDate(LocalDate.of(2025, 1, 31))
            .status(InvoiceStatus.PAID)
            .build();
        invoiceRepository.save(inv3);

        // ホテル（CUS-005）- 一部入金
        Invoice inv4 = Invoice.builder()
            .invoiceNumber("INV-2024-004")
            .invoiceDate(invoiceDate)
            .billingCode("CUS-005")
            .customerCode("CUS-005")
            .customerBranchNumber("00")
            .closingDate(closingDate)
            .invoiceType(InvoiceType.CLOSING)
            .previousBalance(new BigDecimal("800000"))
            .receiptAmount(new BigDecimal("600000"))
            .carriedBalance(new BigDecimal("200000"))
            .currentSalesAmount(new BigDecimal("317500"))
            .currentTaxAmount(new BigDecimal("31750"))
            .currentInvoiceAmount(new BigDecimal("349250"))
            .invoiceBalance(new BigDecimal("549250"))
            .dueDate(LocalDate.of(2025, 1, 31))
            .status(InvoiceStatus.PARTIALLY_PAID)
            .build();
        invoiceRepository.save(inv4);

        // 居酒屋（CUS-007）- 回収遅延
        Invoice inv5 = Invoice.builder()
            .invoiceNumber("INV-2024-005")
            .invoiceDate(invoiceDate)
            .billingCode("CUS-007")
            .customerCode("CUS-007")
            .customerBranchNumber("00")
            .closingDate(closingDate)
            .invoiceType(InvoiceType.CLOSING)
            .previousBalance(new BigDecimal("150000"))
            .receiptAmount(BigDecimal.ZERO)
            .carriedBalance(new BigDecimal("150000"))
            .currentSalesAmount(new BigDecimal("85000"))
            .currentTaxAmount(new BigDecimal("8500"))
            .currentInvoiceAmount(new BigDecimal("93500"))
            .invoiceBalance(new BigDecimal("243500"))
            .dueDate(LocalDate.of(2025, 1, 31))
            .status(InvoiceStatus.OVERDUE)
            .build();
        invoiceRepository.save(inv5);

        LOG.info("請求データ 5件 投入完了");
    }

    private void seedReceipts() {
        LOG.info("入金データを投入中...");

        // 2024年12月分の入金データ
        LocalDate receiptDate = LocalDate.of(2024, 12, 25);

        // 百貨店（CUS-001）- 前月残高500,000円入金
        Receipt rec1 = Receipt.builder()
            .receiptNumber("REC-2024-001")
            .receiptDate(receiptDate)
            .customerCode("CUS-001")
            .customerBranchNumber("00")
            .receiptMethod(ReceiptMethod.BANK_TRANSFER)
            .receiptAmount(new BigDecimal("500000"))
            .appliedAmount(new BigDecimal("500000"))
            .unappliedAmount(BigDecimal.ZERO)
            .bankFee(BigDecimal.ZERO)
            .payerName("百貨店株式会社")
            .bankName("みずほ銀行")
            .accountNumber("1234567")
            .status(ReceiptStatus.APPLIED)
            .build();
        receiptRepository.save(rec1);

        // レストラン（CUS-002）- 前月残高300,000円入金
        Receipt rec2 = Receipt.builder()
            .receiptNumber("REC-2024-002")
            .receiptDate(receiptDate)
            .customerCode("CUS-002")
            .customerBranchNumber("00")
            .receiptMethod(ReceiptMethod.BANK_TRANSFER)
            .receiptAmount(new BigDecimal("300000"))
            .appliedAmount(new BigDecimal("300000"))
            .unappliedAmount(BigDecimal.ZERO)
            .bankFee(BigDecimal.ZERO)
            .payerName("レストランチェーン株式会社")
            .bankName("三菱UFJ銀行")
            .accountNumber("2345678")
            .status(ReceiptStatus.APPLIED)
            .build();
        receiptRepository.save(rec2);

        // スーパー（CUS-003）- 418,000円入金（完済）
        Receipt rec3 = Receipt.builder()
            .receiptNumber("REC-2024-003")
            .receiptDate(receiptDate)
            .customerCode("CUS-003")
            .customerBranchNumber("00")
            .receiptMethod(ReceiptMethod.BANK_TRANSFER)
            .receiptAmount(new BigDecimal("418000"))
            .appliedAmount(new BigDecimal("418000"))
            .unappliedAmount(BigDecimal.ZERO)
            .bankFee(BigDecimal.ZERO)
            .payerName("スーパーマーケット株式会社")
            .bankName("三井住友銀行")
            .accountNumber("3456789")
            .status(ReceiptStatus.APPLIED)
            .build();
        receiptRepository.save(rec3);

        // ホテル（CUS-005）- 600,000円入金（一部）
        Receipt rec4 = Receipt.builder()
            .receiptNumber("REC-2024-004")
            .receiptDate(receiptDate)
            .customerCode("CUS-005")
            .customerBranchNumber("00")
            .receiptMethod(ReceiptMethod.BANK_TRANSFER)
            .receiptAmount(new BigDecimal("600000"))
            .appliedAmount(new BigDecimal("600000"))
            .unappliedAmount(BigDecimal.ZERO)
            .bankFee(BigDecimal.ZERO)
            .payerName("ホテルグループ株式会社")
            .bankName("りそな銀行")
            .accountNumber("4567890")
            .status(ReceiptStatus.APPLIED)
            .build();
        receiptRepository.save(rec4);

        LOG.info("入金データ 4件 投入完了");
    }

    // ========================================
    // 調達管理トランザクションデータ
    // ========================================

    private void seedPurchaseOrders() {
        LOG.info("発注データを投入中...");

        // 発注1（牛肉仕入先向け）- 確定済み
        PurchaseOrder po1 = PurchaseOrder.builder()
            .purchaseOrderNumber("PO-2025-001")
            .supplierCode("SUP-001")
            .orderDate(LocalDate.of(2025, 1, 8))
            .desiredDeliveryDate(LocalDate.of(2025, 1, 15))
            .status(PurchaseOrderStatus.CONFIRMED)
            .purchaserCode("EMP-011")
            .details(List.of(
                createPurchaseOrderDetail(1, "BEEF-001", 100, 5000, LocalDate.of(2025, 1, 15)),
                createPurchaseOrderDetail(2, "BEEF-002", 150, 4000, LocalDate.of(2025, 1, 15))
            ))
            .build();
        calculatePurchaseOrderTotals(po1);
        purchaseOrderRepository.save(po1);

        // 発注2（豚肉仕入先向け）- 一部入荷
        PurchaseOrder po2 = PurchaseOrder.builder()
            .purchaseOrderNumber("PO-2025-002")
            .supplierCode("SUP-002")
            .orderDate(LocalDate.of(2025, 1, 10))
            .desiredDeliveryDate(LocalDate.of(2025, 1, 18))
            .status(PurchaseOrderStatus.PARTIALLY_RECEIVED)
            .purchaserCode("EMP-011")
            .details(List.of(
                createPurchaseOrderDetail(1, "PORK-001", 200, 800, LocalDate.of(2025, 1, 18)),
                createPurchaseOrderDetail(2, "PORK-002", 180, 900, LocalDate.of(2025, 1, 18))
            ))
            .build();
        calculatePurchaseOrderTotals(po2);
        purchaseOrderRepository.save(po2);

        // 発注3（鶏肉仕入先向け）- 入荷完了
        PurchaseOrder po3 = PurchaseOrder.builder()
            .purchaseOrderNumber("PO-2024-050")
            .supplierCode("SUP-003")
            .orderDate(LocalDate.of(2024, 12, 20))
            .desiredDeliveryDate(LocalDate.of(2024, 12, 28))
            .status(PurchaseOrderStatus.COMPLETED)
            .purchaserCode("EMP-011")
            .details(List.of(
                createPurchaseOrderDetail(1, "CHKN-001", 300, 300, LocalDate.of(2024, 12, 28)),
                createPurchaseOrderDetail(2, "CHKN-002", 250, 320, LocalDate.of(2024, 12, 28))
            ))
            .build();
        calculatePurchaseOrderTotals(po3);
        purchaseOrderRepository.save(po3);

        LOG.info("発注データ 3件 投入完了");
    }

    private PurchaseOrderDetail createPurchaseOrderDetail(int lineNumber, String productCode,
                                                            int quantity, int unitPrice,
                                                            LocalDate expectedDeliveryDate) {
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal price = new BigDecimal(unitPrice);
        BigDecimal amount = qty.multiply(price);

        return PurchaseOrderDetail.builder()
            .lineNumber(lineNumber)
            .productCode(productCode)
            .orderQuantity(qty)
            .unitPrice(price)
            .orderAmount(amount)
            .expectedDeliveryDate(expectedDeliveryDate)
            .receivedQuantity(BigDecimal.ZERO)
            .remainingQuantity(qty)
            .build();
    }

    private void calculatePurchaseOrderTotals(PurchaseOrder order) {
        BigDecimal totalAmount = order.getDetails().stream()
            .map(PurchaseOrderDetail::getOrderAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = totalAmount.multiply(TAX_RATE).divide(HUNDRED);
        order.setTotalAmount(totalAmount);
        order.setTaxAmount(taxAmount);
    }

    private void seedReceivings() {
        LOG.info("入荷データを投入中...");

        // PO-2024-050（鶏肉仕入先・完了）の入荷データ
        PurchaseOrder po3 = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2024-050")
            .orElseThrow(() -> new IllegalStateException("発注 PO-2024-050 が見つかりません"));

        Receiving rec1 = Receiving.builder()
            .receivingNumber("RCV-2024-001")
            .purchaseOrderId(po3.getId())
            .supplierCode("SUP-003")
            .supplierBranchNumber("00")
            .receivingDate(LocalDate.of(2024, 12, 28))
            .status(ReceivingStatus.PURCHASE_RECORDED)
            .receiverCode("EMP-011")
            .warehouseCode("WH-HQ")
            .details(List.of(
                createReceivingDetail(1, po3.getDetails().get(0).getId(), "CHKN-001",
                    new BigDecimal("300"), new BigDecimal("300")),
                createReceivingDetail(2, po3.getDetails().get(1).getId(), "CHKN-002",
                    new BigDecimal("250"), new BigDecimal("320"))
            ))
            .build();
        receivingRepository.save(rec1);

        // PO-2025-002（豚肉仕入先・一部入荷）の入荷データ
        PurchaseOrder po2 = purchaseOrderRepository.findByPurchaseOrderNumber("PO-2025-002")
            .orElseThrow(() -> new IllegalStateException("発注 PO-2025-002 が見つかりません"));

        Receiving rec2 = Receiving.builder()
            .receivingNumber("RCV-2025-001")
            .purchaseOrderId(po2.getId())
            .supplierCode("SUP-002")
            .supplierBranchNumber("00")
            .receivingDate(LocalDate.of(2025, 1, 15))
            .status(ReceivingStatus.PURCHASE_RECORDED)
            .receiverCode("EMP-011")
            .warehouseCode("WH-HQ")
            .details(List.of(
                createReceivingDetail(1, po2.getDetails().get(0).getId(), "PORK-001",
                    new BigDecimal("100"), new BigDecimal("800")),
                createReceivingDetail(2, po2.getDetails().get(1).getId(), "PORK-002",
                    new BigDecimal("90"), new BigDecimal("900"))
            ))
            .build();
        receivingRepository.save(rec2);

        LOG.info("入荷データ 2件 投入完了");
    }

    private ReceivingDetail createReceivingDetail(int lineNumber, Integer purchaseOrderDetailId,
                                                    String productCode, BigDecimal quantity,
                                                    BigDecimal unitPrice) {
        BigDecimal amount = quantity.multiply(unitPrice);
        return ReceivingDetail.builder()
            .lineNumber(lineNumber)
            .purchaseOrderDetailId(purchaseOrderDetailId)
            .productCode(productCode)
            .receivingQuantity(quantity)
            .inspectedQuantity(quantity)
            .acceptedQuantity(quantity)
            .rejectedQuantity(BigDecimal.ZERO)
            .unitPrice(unitPrice)
            .amount(amount)
            .build();
    }

    private void seedPurchases() {
        LOG.info("仕入データを投入中...");

        // RCV-2024-001（鶏肉・完了）の仕入データ
        Receiving rec1 = receivingRepository.findByReceivingNumber("RCV-2024-001")
            .orElseThrow(() -> new IllegalStateException("入荷 RCV-2024-001 が見つかりません"));

        Purchase pur1 = Purchase.builder()
            .purchaseNumber("PUR-2024-001")
            .receivingId(rec1.getId())
            .supplierCode("SUP-003")
            .supplierBranchNumber("00")
            .purchaseDate(LocalDate.of(2024, 12, 28))
            .details(List.of(
                createPurchaseDetail(1, "CHKN-001", new BigDecimal("300"), new BigDecimal("300")),
                createPurchaseDetail(2, "CHKN-002", new BigDecimal("250"), new BigDecimal("320"))
            ))
            .build();
        pur1.recalculateTotalAmount();
        purchaseRepository.save(pur1);

        // RCV-2025-001（豚肉・一部）の仕入データ
        Receiving rec2 = receivingRepository.findByReceivingNumber("RCV-2025-001")
            .orElseThrow(() -> new IllegalStateException("入荷 RCV-2025-001 が見つかりません"));

        Purchase pur2 = Purchase.builder()
            .purchaseNumber("PUR-2025-001")
            .receivingId(rec2.getId())
            .supplierCode("SUP-002")
            .supplierBranchNumber("00")
            .purchaseDate(LocalDate.of(2025, 1, 15))
            .details(List.of(
                createPurchaseDetail(1, "PORK-001", new BigDecimal("100"), new BigDecimal("800")),
                createPurchaseDetail(2, "PORK-002", new BigDecimal("90"), new BigDecimal("900"))
            ))
            .build();
        pur2.recalculateTotalAmount();
        purchaseRepository.save(pur2);

        LOG.info("仕入データ 2件 投入完了");
    }

    private PurchaseDetail createPurchaseDetail(int lineNumber, String productCode,
                                                  BigDecimal quantity, BigDecimal unitPrice) {
        BigDecimal amount = quantity.multiply(unitPrice);
        return PurchaseDetail.builder()
            .lineNumber(lineNumber)
            .productCode(productCode)
            .purchaseQuantity(quantity)
            .unitPrice(unitPrice)
            .purchaseAmount(amount)
            .build();
    }

    private void seedPayments() {
        LOG.info("支払データを投入中...");

        // 2024年12月分の支払データ
        LocalDate closingDate = LocalDate.of(2024, 12, 31);
        LocalDate dueDate = LocalDate.of(2025, 1, 31);
        LocalDate executionDate = LocalDate.of(2024, 12, 25);

        // 地域食肉卸A社（SUP-001）- 前月残高800,000円支払済
        Payment pay1 = Payment.builder()
            .paymentNumber("PAY-2024-001")
            .supplierCode("SUP-001")
            .paymentClosingDate(closingDate)
            .paymentDueDate(dueDate)
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .paymentAmount(new BigDecimal("800000"))
            .taxAmount(BigDecimal.ZERO)
            .withholdingAmount(BigDecimal.ZERO)
            .netPaymentAmount(new BigDecimal("800000"))
            .paymentExecutionDate(executionDate)
            .status(PaymentStatus.PAID)
            .bankCode("0001")
            .branchCode("001")
            .accountType("普通")
            .accountNumber("1111111")
            .accountName("チイキショクニクオロシエーシャ")
            .build();
        paymentRepository.save(pay1);

        // 地域食肉卸B社（SUP-002）- 前月残高500,000円支払済
        Payment pay2 = Payment.builder()
            .paymentNumber("PAY-2024-002")
            .supplierCode("SUP-002")
            .paymentClosingDate(closingDate)
            .paymentDueDate(dueDate)
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .paymentAmount(new BigDecimal("500000"))
            .taxAmount(BigDecimal.ZERO)
            .withholdingAmount(BigDecimal.ZERO)
            .netPaymentAmount(new BigDecimal("500000"))
            .paymentExecutionDate(executionDate)
            .status(PaymentStatus.PAID)
            .bankCode("0005")
            .branchCode("002")
            .accountType("普通")
            .accountNumber("2222222")
            .accountName("チイキショクニクオロシビーシャ")
            .build();
        paymentRepository.save(pay2);

        // 地域畜産農家（SUP-003）- 340,000円支払済（完済）
        Payment pay3 = Payment.builder()
            .paymentNumber("PAY-2024-003")
            .supplierCode("SUP-003")
            .paymentClosingDate(closingDate)
            .paymentDueDate(dueDate)
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .paymentAmount(new BigDecimal("340000"))
            .taxAmount(BigDecimal.ZERO)
            .withholdingAmount(BigDecimal.ZERO)
            .netPaymentAmount(new BigDecimal("340000"))
            .paymentExecutionDate(executionDate)
            .status(PaymentStatus.PAID)
            .bankCode("0009")
            .branchCode("003")
            .accountType("普通")
            .accountNumber("3333333")
            .accountName("チイキチクサンノウカ")
            .build();
        paymentRepository.save(pay3);

        // 県内畜産組合（SUP-004）- 前月残高250,000円支払済
        Payment pay4 = Payment.builder()
            .paymentNumber("PAY-2024-004")
            .supplierCode("SUP-004")
            .paymentClosingDate(closingDate)
            .paymentDueDate(dueDate)
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .paymentAmount(new BigDecimal("250000"))
            .taxAmount(BigDecimal.ZERO)
            .withholdingAmount(BigDecimal.ZERO)
            .netPaymentAmount(new BigDecimal("250000"))
            .paymentExecutionDate(executionDate)
            .status(PaymentStatus.PAID)
            .bankCode("0017")
            .branchCode("004")
            .accountType("普通")
            .accountNumber("4444444")
            .accountName("ケンナイチクサンクミアイ")
            .build();
        paymentRepository.save(pay4);

        LOG.info("支払データ 4件 投入完了");
    }

    // ========================================
    // 債権管理トランザクションデータ
    // ========================================

    private void seedAccountsReceivables() {
        LOG.info("売掛金残高データを投入中...");

        // 2024年12月末時点の売掛金残高（前月繰越データ）
        LocalDate baseDate = LocalDate.of(2024, 12, 31);

        // 百貨店（CUS-001）- 残高あり
        AccountsReceivable ar1 = AccountsReceivable.builder()
            .customerCode("CUS-001")
            .customerBranchNumber("00")
            .baseDate(baseDate)
            .previousMonthBalance(new BigDecimal("500000"))
            .currentMonthSales(new BigDecimal("180000"))
            .currentMonthReceipts(new BigDecimal("500000"))
            .currentMonthBalance(new BigDecimal("180000"))
            .build();
        accountsReceivableRepository.save(ar1);

        // レストラン（CUS-002）- 残高あり
        AccountsReceivable ar2 = AccountsReceivable.builder()
            .customerCode("CUS-002")
            .customerBranchNumber("00")
            .baseDate(baseDate)
            .previousMonthBalance(new BigDecimal("300000"))
            .currentMonthSales(new BigDecimal("205000"))
            .currentMonthReceipts(new BigDecimal("300000"))
            .currentMonthBalance(new BigDecimal("205000"))
            .build();
        accountsReceivableRepository.save(ar2);

        // スーパー（CUS-003）- 完済
        AccountsReceivable ar3 = AccountsReceivable.builder()
            .customerCode("CUS-003")
            .customerBranchNumber("00")
            .baseDate(baseDate)
            .previousMonthBalance(new BigDecimal("250000"))
            .currentMonthSales(new BigDecimal("168000"))
            .currentMonthReceipts(new BigDecimal("418000"))
            .currentMonthBalance(BigDecimal.ZERO)
            .build();
        accountsReceivableRepository.save(ar3);

        // ホテル（CUS-005）- 残高あり
        AccountsReceivable ar4 = AccountsReceivable.builder()
            .customerCode("CUS-005")
            .customerBranchNumber("00")
            .baseDate(baseDate)
            .previousMonthBalance(new BigDecimal("800000"))
            .currentMonthSales(new BigDecimal("317500"))
            .currentMonthReceipts(new BigDecimal("600000"))
            .currentMonthBalance(new BigDecimal("517500"))
            .build();
        accountsReceivableRepository.save(ar4);

        // 居酒屋（CUS-007）- 残高あり（滞留）
        AccountsReceivable ar5 = AccountsReceivable.builder()
            .customerCode("CUS-007")
            .customerBranchNumber("00")
            .baseDate(baseDate)
            .previousMonthBalance(new BigDecimal("150000"))
            .currentMonthSales(new BigDecimal("85000"))
            .currentMonthReceipts(BigDecimal.ZERO)
            .currentMonthBalance(new BigDecimal("235000"))
            .build();
        accountsReceivableRepository.save(ar5);

        LOG.info("売掛金残高データ 5件 投入完了");
    }

    // ========================================
    // 債務管理トランザクションデータ
    // ========================================

    private void seedPayableBalances() {
        LOG.info("買掛金残高データを投入中...");

        // 2024年12月末時点の買掛金残高（前月繰越データ）
        LocalDate yearMonth = LocalDate.of(2024, 12, 31);

        // 地域食肉卸A社（SUP-001）- 残高あり
        PayableBalance pb1 = PayableBalance.builder()
            .supplierCode("SUP-001")
            .yearMonth(yearMonth)
            .previousBalance(new BigDecimal("800000"))
            .currentPurchaseAmount(new BigDecimal("1100000"))
            .currentPaymentAmount(new BigDecimal("800000"))
            .currentBalance(new BigDecimal("1100000"))
            .build();
        payableBalanceRepository.save(pb1);

        // 地域食肉卸B社（SUP-002）- 残高あり
        PayableBalance pb2 = PayableBalance.builder()
            .supplierCode("SUP-002")
            .yearMonth(yearMonth)
            .previousBalance(new BigDecimal("500000"))
            .currentPurchaseAmount(new BigDecimal("322000"))
            .currentPaymentAmount(new BigDecimal("500000"))
            .currentBalance(new BigDecimal("322000"))
            .build();
        payableBalanceRepository.save(pb2);

        // 地域畜産農家（SUP-003）- 完済
        PayableBalance pb3 = PayableBalance.builder()
            .supplierCode("SUP-003")
            .yearMonth(yearMonth)
            .previousBalance(new BigDecimal("170000"))
            .currentPurchaseAmount(new BigDecimal("170000"))
            .currentPaymentAmount(new BigDecimal("340000"))
            .currentBalance(BigDecimal.ZERO)
            .build();
        payableBalanceRepository.save(pb3);

        // 県内畜産組合（SUP-004）- 残高あり
        PayableBalance pb4 = PayableBalance.builder()
            .supplierCode("SUP-004")
            .yearMonth(yearMonth)
            .previousBalance(new BigDecimal("250000"))
            .currentPurchaseAmount(new BigDecimal("180000"))
            .currentPaymentAmount(new BigDecimal("250000"))
            .currentBalance(new BigDecimal("180000"))
            .build();
        payableBalanceRepository.save(pb4);

        LOG.info("買掛金残高データ 4件 投入完了");
    }

}
