package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.PurchaseOrderUseCase;
import com.example.sms.application.port.in.SupplierUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import com.example.sms.infrastructure.in.web.form.PurchaseOrderForm;
import jakarta.validation.Valid;
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

/**
 * 発注画面コントローラー.
 */
@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderWebController {

    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final SupplierUseCase supplierUseCase;
    private final ProductUseCase productUseCase;

    public PurchaseOrderWebController(
            PurchaseOrderUseCase purchaseOrderUseCase,
            SupplierUseCase supplierUseCase,
            ProductUseCase productUseCase) {
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.supplierUseCase = supplierUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 発注一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<PurchaseOrder> purchaseOrderPage = purchaseOrderUseCase.getPurchaseOrders(page, size, keyword);

        model.addAttribute("purchaseOrders", purchaseOrderPage.getContent());
        model.addAttribute("page", purchaseOrderPage);
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "purchase-orders/list";
    }

    /**
     * 発注詳細画面を表示.
     */
    @GetMapping("/{purchaseOrderNumber}")
    public String show(@PathVariable String purchaseOrderNumber, Model model) {
        PurchaseOrder purchaseOrder = purchaseOrderUseCase.getPurchaseOrderWithDetails(purchaseOrderNumber);
        model.addAttribute("purchaseOrder", purchaseOrder);
        return "purchase-orders/show";
    }

    /**
     * 発注登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PurchaseOrderForm());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        model.addAttribute("products", productUseCase.getAllProducts());
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        return "purchase-orders/new";
    }

    /**
     * 発注を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") PurchaseOrderForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
            model.addAttribute("products", productUseCase.getAllProducts());
            model.addAttribute("statuses", PurchaseOrderStatus.values());
            return "purchase-orders/new";
        }

        PurchaseOrder created = purchaseOrderUseCase.createPurchaseOrder(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "発注を登録しました: " + created.getPurchaseOrderNumber());
        return "redirect:/purchase-orders/" + created.getPurchaseOrderNumber();
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new PurchaseOrderForm.PurchaseOrderDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "purchase-orders/fragments :: detailRow";
    }
}
