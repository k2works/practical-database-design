package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PurchaseUseCase;
import com.example.sms.domain.model.purchase.Purchase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * 仕入画面コントローラー.
 */
@Controller
@RequestMapping("/purchases")
public class PurchaseWebController {

    private final PurchaseUseCase purchaseUseCase;

    public PurchaseWebController(PurchaseUseCase purchaseUseCase) {
        this.purchaseUseCase = purchaseUseCase;
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
}
