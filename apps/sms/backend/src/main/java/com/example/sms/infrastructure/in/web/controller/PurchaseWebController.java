package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.PurchaseUseCase;
import com.example.sms.application.port.in.SupplierUseCase;
import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.infrastructure.in.web.form.PurchaseForm;
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

import java.util.List;
import java.util.Locale;

/**
 * 仕入画面コントローラー.
 */
@Controller
@RequestMapping("/purchases")
public class PurchaseWebController {

    private final PurchaseUseCase purchaseUseCase;
    private final SupplierUseCase supplierUseCase;
    private final ProductUseCase productUseCase;

    public PurchaseWebController(
            PurchaseUseCase purchaseUseCase,
            SupplierUseCase supplierUseCase,
            ProductUseCase productUseCase) {
        this.purchaseUseCase = purchaseUseCase;
        this.supplierUseCase = supplierUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 仕入一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Purchase> purchases = getFilteredPurchases(supplierCode);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            purchases = purchases.stream()
                .filter(p -> (p.getPurchaseNumber() != null
                        && p.getPurchaseNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (p.getSupplierCode() != null
                        && p.getSupplierCode().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("purchases", purchases);
        model.addAttribute("selectedSupplier", supplierCode);
        model.addAttribute("keyword", keyword);
        return "purchases/list";
    }

    private List<Purchase> getFilteredPurchases(String supplierCode) {
        if (supplierCode != null && !supplierCode.isBlank()) {
            return purchaseUseCase.getPurchasesBySupplier(supplierCode);
        } else {
            return purchaseUseCase.getAllPurchases();
        }
    }

    /**
     * 仕入詳細画面を表示.
     */
    @GetMapping("/{purchaseNumber}")
    public String show(@PathVariable String purchaseNumber, Model model) {
        Purchase purchase = purchaseUseCase.getPurchaseWithDetails(purchaseNumber);
        model.addAttribute("purchase", purchase);
        return "purchases/show";
    }

    /**
     * 仕入登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PurchaseForm());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "purchases/new";
    }

    /**
     * 仕入を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") PurchaseForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
            model.addAttribute("products", productUseCase.getAllProducts());
            return "purchases/new";
        }

        Purchase created = purchaseUseCase.createPurchase(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "仕入を登録しました: " + created.getPurchaseNumber());
        return "redirect:/purchases/" + created.getPurchaseNumber();
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new PurchaseForm.PurchaseDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "purchases/fragments :: detailRow";
    }
}
