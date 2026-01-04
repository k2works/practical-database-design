package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PaymentUseCase;
import com.example.sms.application.port.in.SupplierUseCase;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentMethod;
import com.example.sms.domain.model.payment.PaymentStatus;
import com.example.sms.infrastructure.in.web.form.PaymentForm;
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
 * 支払画面コントローラー.
 */
@Controller
@RequestMapping("/payments")
public class PaymentWebController {

    private final PaymentUseCase paymentUseCase;
    private final SupplierUseCase supplierUseCase;

    public PaymentWebController(
            PaymentUseCase paymentUseCase,
            SupplierUseCase supplierUseCase) {
        this.paymentUseCase = paymentUseCase;
        this.supplierUseCase = supplierUseCase;
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

    /**
     * 支払登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PaymentForm());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "payments/new";
    }

    /**
     * 支払を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") PaymentForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "payments/new";
        }

        Payment created = paymentUseCase.createPayment(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "支払を登録しました: " + created.getPaymentNumber());
        return "redirect:/payments/" + created.getPaymentNumber();
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new PaymentForm.PaymentDetailForm());
        return "payments/fragments :: detailRow";
    }
}
