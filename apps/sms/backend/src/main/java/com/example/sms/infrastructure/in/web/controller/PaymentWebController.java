package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PaymentUseCase;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * 支払画面コントローラー.
 */
@Controller
@RequestMapping("/payments")
public class PaymentWebController {

    private final PaymentUseCase paymentUseCase;

    public PaymentWebController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    /**
     * 支払一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Payment> payments = getFilteredPayments(status);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            payments = payments.stream()
                .filter(p -> (p.getPaymentNumber() != null
                        && p.getPaymentNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (p.getSupplierCode() != null
                        && p.getSupplierCode().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("payments", payments);
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "payments/list";
    }

    private List<Payment> getFilteredPayments(PaymentStatus status) {
        if (status != null) {
            return paymentUseCase.getPaymentsByStatus(status);
        } else {
            return paymentUseCase.getAllPayments();
        }
    }

    /**
     * 支払詳細画面を表示.
     */
    @GetMapping("/{paymentNumber}")
    public String show(@PathVariable String paymentNumber, Model model) {
        Payment payment = paymentUseCase.getPaymentWithDetails(paymentNumber);
        model.addAttribute("payment", payment);
        return "payments/show";
    }
}
