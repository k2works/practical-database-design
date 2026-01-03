package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ReceivingUseCase;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * 入荷画面コントローラー.
 */
@Controller
@RequestMapping("/receivings")
public class ReceivingWebController {

    private final ReceivingUseCase receivingUseCase;

    public ReceivingWebController(ReceivingUseCase receivingUseCase) {
        this.receivingUseCase = receivingUseCase;
    }

    /**
     * 入荷一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) ReceivingStatus status,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Receiving> receivings = getFilteredReceivings(status);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            receivings = receivings.stream()
                .filter(r -> (r.getReceivingNumber() != null
                        && r.getReceivingNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (r.getSupplierCode() != null
                        && r.getSupplierCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (r.getWarehouseCode() != null
                        && r.getWarehouseCode().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("receivings", receivings);
        model.addAttribute("statuses", ReceivingStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "receivings/list";
    }

    private List<Receiving> getFilteredReceivings(ReceivingStatus status) {
        if (status != null) {
            return receivingUseCase.getReceivingsByStatus(status);
        } else {
            return receivingUseCase.getAllReceivings();
        }
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
}
