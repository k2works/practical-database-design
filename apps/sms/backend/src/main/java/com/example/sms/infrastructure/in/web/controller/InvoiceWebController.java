package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.InvoiceUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.infrastructure.in.web.form.InvoiceForm;
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
 * 請求画面コントローラー.
 */
@Controller
@RequestMapping("/invoices")
public class InvoiceWebController {

    private final InvoiceUseCase invoiceUseCase;
    private final PartnerUseCase partnerUseCase;

    public InvoiceWebController(
            InvoiceUseCase invoiceUseCase,
            PartnerUseCase partnerUseCase) {
        this.invoiceUseCase = invoiceUseCase;
        this.partnerUseCase = partnerUseCase;
    }

    /**
     * 請求一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Invoice> invoices = getFilteredInvoices(status, customerCode);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            invoices = invoices.stream()
                .filter(i -> i.getInvoiceNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                .toList();
        }

        model.addAttribute("invoices", invoices);
        model.addAttribute("statuses", InvoiceStatus.values());
        model.addAttribute("customers", partnerUseCase.getCustomers());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCustomerCode", customerCode);
        model.addAttribute("keyword", keyword);
        return "invoices/list";
    }

    private List<Invoice> getFilteredInvoices(InvoiceStatus status, String customerCode) {
        if (status != null) {
            return invoiceUseCase.getInvoicesByStatus(status);
        } else if (customerCode != null && !customerCode.isBlank()) {
            return invoiceUseCase.getInvoicesByCustomer(customerCode);
        } else {
            return invoiceUseCase.getAllInvoices();
        }
    }

    /**
     * 請求詳細画面を表示.
     */
    @GetMapping("/{invoiceNumber}")
    public String show(@PathVariable String invoiceNumber, Model model) {
        Invoice invoice = invoiceUseCase.getInvoiceByNumber(invoiceNumber);
        model.addAttribute("invoice", invoice);
        return "invoices/show";
    }

    /**
     * 請求登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new InvoiceForm());
        addFormAttributes(model);
        return "invoices/new";
    }

    /**
     * 請求を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") InvoiceForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "invoices/new";
        }

        Invoice invoice = invoiceUseCase.createInvoice(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "請求「" + invoice.getInvoiceNumber() + "」を登録しました");
        return "redirect:/invoices";
    }

    /**
     * 請求編集フォームを表示.
     */
    @GetMapping("/{invoiceNumber}/edit")
    public String editForm(@PathVariable String invoiceNumber, Model model) {
        Invoice invoice = invoiceUseCase.getInvoiceByNumber(invoiceNumber);
        model.addAttribute("form", InvoiceForm.from(invoice));
        addFormAttributes(model);
        return "invoices/edit";
    }

    /**
     * 請求を更新.
     */
    @PostMapping("/{invoiceNumber}")
    public String update(
            @PathVariable String invoiceNumber,
            @Valid @ModelAttribute("form") InvoiceForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "invoices/edit";
        }

        invoiceUseCase.updateInvoice(invoiceNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "請求「" + invoiceNumber + "」を更新しました");
        return "redirect:/invoices/" + invoiceNumber;
    }

    /**
     * 請求を発行.
     */
    @PostMapping("/{invoiceNumber}/issue")
    public String issue(
            @PathVariable String invoiceNumber,
            @RequestParam Integer version,
            RedirectAttributes redirectAttributes) {

        invoiceUseCase.issueInvoice(invoiceNumber, version);
        redirectAttributes.addFlashAttribute("successMessage",
            "請求「" + invoiceNumber + "」を発行しました");
        return "redirect:/invoices/" + invoiceNumber;
    }

    /**
     * 請求を削除.
     */
    @PostMapping("/{invoiceNumber}/delete")
    public String delete(
            @PathVariable String invoiceNumber,
            RedirectAttributes redirectAttributes) {

        invoiceUseCase.deleteInvoice(invoiceNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "請求「" + invoiceNumber + "」を削除しました");
        return "redirect:/invoices";
    }

    /**
     * フォームの共通属性を追加.
     */
    private void addFormAttributes(Model model) {
        List<Partner> customers = partnerUseCase.getCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("statuses", InvoiceStatus.values());
    }
}
