package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.ReceivingUseCase;
import com.example.sms.application.port.in.SupplierUseCase;
import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import com.example.sms.infrastructure.in.web.form.ReceivingForm;
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
 * 入荷画面コントローラー.
 */
@Controller
@RequestMapping("/receivings")
public class ReceivingWebController {

    private final ReceivingUseCase receivingUseCase;
    private final SupplierUseCase supplierUseCase;
    private final ProductUseCase productUseCase;
    private final WarehouseUseCase warehouseUseCase;

    public ReceivingWebController(
            ReceivingUseCase receivingUseCase,
            SupplierUseCase supplierUseCase,
            ProductUseCase productUseCase,
            WarehouseUseCase warehouseUseCase) {
        this.receivingUseCase = receivingUseCase;
        this.supplierUseCase = supplierUseCase;
        this.productUseCase = productUseCase;
        this.warehouseUseCase = warehouseUseCase;
    }

    /**
     * 入荷一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Receiving> receivingPage = receivingUseCase.getReceivings(page, size, keyword);

        model.addAttribute("receivings", receivingPage.getContent());
        model.addAttribute("page", receivingPage);
        model.addAttribute("statuses", ReceivingStatus.values());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "receivings/list";
    }

    /**
     * 入荷詳細画面を表示.
     */
    @GetMapping("/{receivingNumber}")
    public String show(@PathVariable String receivingNumber, Model model) {
        Receiving receiving = receivingUseCase.getReceivingWithDetails(receivingNumber);
        model.addAttribute("receiving", receiving);
        return "receivings/show";
    }

    /**
     * 入荷登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ReceivingForm());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        model.addAttribute("products", productUseCase.getAllProducts());
        model.addAttribute("warehouses", warehouseUseCase.getAllWarehouses());
        model.addAttribute("statuses", ReceivingStatus.values());
        return "receivings/new";
    }

    /**
     * 入荷を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ReceivingForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
            model.addAttribute("products", productUseCase.getAllProducts());
            model.addAttribute("warehouses", warehouseUseCase.getAllWarehouses());
            model.addAttribute("statuses", ReceivingStatus.values());
            return "receivings/new";
        }

        Receiving created = receivingUseCase.createReceiving(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "入荷を登録しました: " + created.getReceivingNumber());
        return "redirect:/receivings/" + created.getReceivingNumber();
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new ReceivingForm.ReceivingDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "receivings/fragments :: detailRow";
    }
}
