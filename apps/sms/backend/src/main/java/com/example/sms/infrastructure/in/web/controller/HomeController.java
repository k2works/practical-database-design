package com.example.sms.infrastructure.in.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面コントローラー.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        // ダッシュボード用のカウントを設定（後で実装）
        model.addAttribute("departmentCount", 0);
        model.addAttribute("employeeCount", 0);
        model.addAttribute("productClassificationCount", 0);
        model.addAttribute("productCount", 0);
        model.addAttribute("partnerCount", 0);
        model.addAttribute("estimateCount", 0);
        model.addAttribute("pendingOrderCount", 0);
        model.addAttribute("pendingShipmentCount", 0);
        model.addAttribute("salesCount", 0);
        model.addAttribute("unpaidInvoiceCount", 0);
        model.addAttribute("todayReceiptCount", 0);
        model.addAttribute("purchaseOrderCount", 0);
        model.addAttribute("receivingCount", 0);
        model.addAttribute("purchaseCount", 0);
        model.addAttribute("unpaidPaymentCount", 0);
        model.addAttribute("inventoryCount", 0);
        model.addAttribute("movementCount", 0);
        model.addAttribute("stocktakingCount", 0);
        return "index";
    }
}
