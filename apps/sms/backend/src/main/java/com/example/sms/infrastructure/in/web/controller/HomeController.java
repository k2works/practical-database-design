package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.DepartmentUseCase;
import com.example.sms.application.port.in.EmployeeUseCase;
import com.example.sms.application.port.in.InventoryUseCase;
import com.example.sms.application.port.in.InvoiceUseCase;
import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.PaymentUseCase;
import com.example.sms.application.port.in.ProductClassificationUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.PurchaseOrderUseCase;
import com.example.sms.application.port.in.PurchaseUseCase;
import com.example.sms.application.port.in.QuotationUseCase;
import com.example.sms.application.port.in.ReceiptUseCase;
import com.example.sms.application.port.in.ReceivingUseCase;
import com.example.sms.application.port.in.SalesUseCase;
import com.example.sms.application.port.in.ShipmentUseCase;
import com.example.sms.application.port.in.StocktakingUseCase;
import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.payment.PaymentStatus;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.QuotationStatus;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * ホーム画面コントローラー.
 */
@Controller
public class HomeController {

    private final DepartmentUseCase departmentUseCase;
    private final EmployeeUseCase employeeUseCase;
    private final ProductClassificationUseCase productClassificationUseCase;
    private final ProductUseCase productUseCase;
    private final PartnerUseCase partnerUseCase;
    private final WarehouseUseCase warehouseUseCase;
    private final QuotationUseCase quotationUseCase;
    private final OrderUseCase orderUseCase;
    private final ShipmentUseCase shipmentUseCase;
    private final SalesUseCase salesUseCase;
    private final InvoiceUseCase invoiceUseCase;
    private final ReceiptUseCase receiptUseCase;
    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final ReceivingUseCase receivingUseCase;
    private final PurchaseUseCase purchaseUseCase;
    private final PaymentUseCase paymentUseCase;
    private final InventoryUseCase inventoryUseCase;
    private final StocktakingUseCase stocktakingUseCase;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public HomeController(
            DepartmentUseCase departmentUseCase,
            EmployeeUseCase employeeUseCase,
            ProductClassificationUseCase productClassificationUseCase,
            ProductUseCase productUseCase,
            PartnerUseCase partnerUseCase,
            WarehouseUseCase warehouseUseCase,
            QuotationUseCase quotationUseCase,
            OrderUseCase orderUseCase,
            ShipmentUseCase shipmentUseCase,
            SalesUseCase salesUseCase,
            InvoiceUseCase invoiceUseCase,
            ReceiptUseCase receiptUseCase,
            PurchaseOrderUseCase purchaseOrderUseCase,
            ReceivingUseCase receivingUseCase,
            PurchaseUseCase purchaseUseCase,
            PaymentUseCase paymentUseCase,
            InventoryUseCase inventoryUseCase,
            StocktakingUseCase stocktakingUseCase) {
        this.departmentUseCase = departmentUseCase;
        this.employeeUseCase = employeeUseCase;
        this.productClassificationUseCase = productClassificationUseCase;
        this.productUseCase = productUseCase;
        this.partnerUseCase = partnerUseCase;
        this.warehouseUseCase = warehouseUseCase;
        this.quotationUseCase = quotationUseCase;
        this.orderUseCase = orderUseCase;
        this.shipmentUseCase = shipmentUseCase;
        this.salesUseCase = salesUseCase;
        this.invoiceUseCase = invoiceUseCase;
        this.receiptUseCase = receiptUseCase;
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.receivingUseCase = receivingUseCase;
        this.purchaseUseCase = purchaseUseCase;
        this.paymentUseCase = paymentUseCase;
        this.inventoryUseCase = inventoryUseCase;
        this.stocktakingUseCase = stocktakingUseCase;
    }

    @GetMapping("/")
    public String index(Model model) {
        // マスタ系カウント
        model.addAttribute("departmentCount", departmentUseCase.getAllDepartments().size());
        model.addAttribute("employeeCount", employeeUseCase.getAllEmployees().size());
        model.addAttribute("productClassificationCount", productClassificationUseCase.getAllClassifications().size());
        model.addAttribute("productCount", productUseCase.getAllProducts().size());
        model.addAttribute("partnerCount", partnerUseCase.getAllPartners().size());
        model.addAttribute("customerCount", partnerUseCase.getCustomers().size());
        model.addAttribute("supplierCount", partnerUseCase.getSuppliers().size());
        model.addAttribute("warehouseCount", warehouseUseCase.getAllWarehouses().size());

        // 販売系カウント
        model.addAttribute("estimateCount", quotationUseCase.getQuotationsByStatus(QuotationStatus.NEGOTIATING).size());
        int pendingOrderCount = orderUseCase.getOrdersByStatus(OrderStatus.RECEIVED).size()
            + orderUseCase.getOrdersByStatus(OrderStatus.ALLOCATED).size();
        model.addAttribute("pendingOrderCount", pendingOrderCount);
        int pendingShipmentCount = shipmentUseCase.getShipmentsByStatus(ShipmentStatus.INSTRUCTED).size()
            + shipmentUseCase.getShipmentsByStatus(ShipmentStatus.PREPARING).size();
        model.addAttribute("pendingShipmentCount", pendingShipmentCount);
        model.addAttribute("salesCount", salesUseCase.getAllSales().size());
        int unpaidInvoiceCount = invoiceUseCase.getInvoicesByStatus(InvoiceStatus.ISSUED).size()
            + invoiceUseCase.getInvoicesByStatus(InvoiceStatus.PARTIALLY_PAID).size()
            + invoiceUseCase.getInvoicesByStatus(InvoiceStatus.OVERDUE).size();
        model.addAttribute("unpaidInvoiceCount", unpaidInvoiceCount);
        LocalDate today = LocalDate.now();
        model.addAttribute("todayReceiptCount", receiptUseCase.getReceiptsByDateRange(today, today).size());

        // 購買系カウント
        model.addAttribute("purchaseOrderCount", purchaseOrderUseCase.getAllPurchaseOrders().size());
        model.addAttribute("receivingCount", receivingUseCase.getAllReceivings().size());
        model.addAttribute("purchaseCount", purchaseUseCase.getAllPurchases().size());
        int unpaidPaymentCount = paymentUseCase.getPaymentsByStatus(PaymentStatus.DRAFT).size()
            + paymentUseCase.getPaymentsByStatus(PaymentStatus.PENDING_APPROVAL).size()
            + paymentUseCase.getPaymentsByStatus(PaymentStatus.APPROVED).size();
        model.addAttribute("unpaidPaymentCount", unpaidPaymentCount);

        // 在庫系カウント
        model.addAttribute("inventoryCount", inventoryUseCase.getAllInventories().size());
        model.addAttribute("movementCount", inventoryUseCase.getAllStockMovements().size());
        model.addAttribute("stocktakingCount", stocktakingUseCase.getAllStocktakings().size());

        return "index";
    }
}
