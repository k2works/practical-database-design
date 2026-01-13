package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.command.CreatePurchaseOrderCommand;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.infrastructure.in.web.form.PurchaseOrderForm;
import com.example.pms.infrastructure.report.ExcelReportGenerator;
import com.example.pms.infrastructure.report.PdfReportGenerator;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * 発注業務画面コントローラー.
 */
@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderWebController {

    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final ItemUseCase itemUseCase;
    private final SupplierRepository supplierRepository;
    private final ExcelReportGenerator excelReportGenerator;
    private final PdfReportGenerator pdfReportGenerator;

    public PurchaseOrderWebController(
            PurchaseOrderUseCase purchaseOrderUseCase,
            ItemUseCase itemUseCase,
            SupplierRepository supplierRepository,
            ExcelReportGenerator excelReportGenerator,
            PdfReportGenerator pdfReportGenerator) {
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.itemUseCase = itemUseCase;
        this.supplierRepository = supplierRepository;
        this.excelReportGenerator = excelReportGenerator;
        this.pdfReportGenerator = pdfReportGenerator;
    }

    /**
     * 発注一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) PurchaseOrderStatus status,
            Model model) {

        List<PurchaseOrder> orders;
        if (status != null) {
            orders = purchaseOrderUseCase.getOrdersByStatus(status);
        } else {
            orders = purchaseOrderUseCase.getAllOrders();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        model.addAttribute("selectedStatus", status);
        return "purchase-orders/list";
    }

    /**
     * 発注詳細画面を表示する.
     */
    @GetMapping("/{orderNumber}")
    public String show(@PathVariable String orderNumber, Model model) {
        PurchaseOrder order = purchaseOrderUseCase.getOrder(orderNumber);
        model.addAttribute("order", order);
        return "purchase-orders/show";
    }

    /**
     * 発注登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PurchaseOrderForm());
        addFormAttributes(model);
        return "purchase-orders/new";
    }

    /**
     * 発注を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") PurchaseOrderForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "purchase-orders/new";
        }

        CreatePurchaseOrderCommand command = form.toCommand();
        PurchaseOrder order = purchaseOrderUseCase.createOrder(command);
        redirectAttributes.addFlashAttribute("successMessage",
            "発注「" + order.getPurchaseOrderNumber() + "」を登録しました");
        return "redirect:/purchase-orders";
    }

    /**
     * 発注を確定する.
     */
    @PostMapping("/{orderNumber}/confirm")
    public String confirm(
            @PathVariable String orderNumber,
            RedirectAttributes redirectAttributes) {

        purchaseOrderUseCase.confirmOrder(orderNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "発注「" + orderNumber + "」を確定しました");
        return "redirect:/purchase-orders/" + orderNumber;
    }

    /**
     * 発注を取消する.
     */
    @PostMapping("/{orderNumber}/cancel")
    public String cancel(
            @PathVariable String orderNumber,
            RedirectAttributes redirectAttributes) {

        purchaseOrderUseCase.cancelOrder(orderNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "発注「" + orderNumber + "」を取消しました");
        return "redirect:/purchase-orders";
    }

    /**
     * 発注一覧を Excel で出力する.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) PurchaseOrderStatus status) {

        List<PurchaseOrder> orders = getOrdersByStatus(status);
        byte[] excelData = excelReportGenerator.generatePurchaseOrderList(orders);

        String filename = "purchase_orders_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excelData);
    }

    /**
     * 発注一覧を PDF で出力する.
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) PurchaseOrderStatus status) {

        List<PurchaseOrder> orders = getOrdersByStatus(status);
        byte[] pdfData = pdfReportGenerator.generatePurchaseOrderList(orders);

        String filename = "purchase_orders_" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfData);
    }

    /**
     * 発注書 PDF を出力する.
     */
    @GetMapping("/{orderNumber}/pdf")
    public ResponseEntity<byte[]> exportOrderPdf(@PathVariable String orderNumber) {
        PurchaseOrder order = purchaseOrderUseCase.getOrder(orderNumber);
        byte[] pdfData = pdfReportGenerator.generatePurchaseOrderPdf(order);

        String filename = "purchase_order_" + orderNumber + ".pdf";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfData);
    }

    private List<PurchaseOrder> getOrdersByStatus(PurchaseOrderStatus status) {
        if (status != null) {
            return purchaseOrderUseCase.getOrdersByStatus(status);
        }
        return purchaseOrderUseCase.getAllOrders();
    }

    private void addFormAttributes(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll();
        List<Item> materials = itemUseCase.getItemsByCategory(ItemCategory.MATERIAL);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("items", materials);
    }
}
