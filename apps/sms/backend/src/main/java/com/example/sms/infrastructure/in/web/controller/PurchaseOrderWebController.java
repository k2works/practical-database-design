package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PurchaseOrderUseCase;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * 発注画面コントローラー.
 */
@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderWebController {

    private final PurchaseOrderUseCase purchaseOrderUseCase;

    public PurchaseOrderWebController(PurchaseOrderUseCase purchaseOrderUseCase) {
        this.purchaseOrderUseCase = purchaseOrderUseCase;
    }

    /**
     * 発注一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) PurchaseOrderStatus status,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<PurchaseOrder> purchaseOrders = getFilteredPurchaseOrders(status);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            purchaseOrders = purchaseOrders.stream()
                .filter(po -> (po.getPurchaseOrderNumber() != null
                        && po.getPurchaseOrderNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (po.getSupplierCode() != null
                        && po.getSupplierCode().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "purchase-orders/list";
    }

    private List<PurchaseOrder> getFilteredPurchaseOrders(PurchaseOrderStatus status) {
        if (status != null) {
            return purchaseOrderUseCase.getPurchaseOrdersByStatus(status);
        } else {
            return purchaseOrderUseCase.getAllPurchaseOrders();
        }
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
}
