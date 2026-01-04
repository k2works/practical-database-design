package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ReceiptUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptMethod;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import com.example.sms.infrastructure.in.web.form.ReceiptForm;
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

/**
 * 入金画面コントローラー.
 */
@Controller
@RequestMapping("/receipts")
public class ReceiptWebController {

    private final ReceiptUseCase receiptUseCase;
    private final PartnerUseCase partnerUseCase;

    public ReceiptWebController(
            ReceiptUseCase receiptUseCase,
            PartnerUseCase partnerUseCase) {
        this.receiptUseCase = receiptUseCase;
        this.partnerUseCase = partnerUseCase;
    }

    /**
     * 入金一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Receipt> receiptPage = receiptUseCase.getReceipts(page, size, keyword);

        model.addAttribute("receipts", receiptPage.getContent());
        model.addAttribute("page", receiptPage);
        model.addAttribute("statuses", ReceiptStatus.values());
        model.addAttribute("customers", partnerUseCase.getCustomers());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "receipts/list";
    }

    /**
     * 入金詳細画面を表示.
     */
    @GetMapping("/{receiptNumber}")
    public String show(@PathVariable String receiptNumber, Model model) {
        Receipt receipt = receiptUseCase.getReceiptByNumber(receiptNumber);
        model.addAttribute("receipt", receipt);
        return "receipts/show";
    }

    /**
     * 入金登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ReceiptForm());
        addFormAttributes(model);
        return "receipts/new";
    }

    /**
     * 入金を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ReceiptForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "receipts/new";
        }

        Receipt receipt = receiptUseCase.createReceipt(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "入金「" + receipt.getReceiptNumber() + "」を登録しました");
        return "redirect:/receipts";
    }

    /**
     * 入金編集フォームを表示.
     */
    @GetMapping("/{receiptNumber}/edit")
    public String editForm(@PathVariable String receiptNumber, Model model) {
        Receipt receipt = receiptUseCase.getReceiptByNumber(receiptNumber);
        model.addAttribute("form", ReceiptForm.from(receipt));
        addFormAttributes(model);
        return "receipts/edit";
    }

    /**
     * 入金を更新.
     */
    @PostMapping("/{receiptNumber}")
    public String update(
            @PathVariable String receiptNumber,
            @Valid @ModelAttribute("form") ReceiptForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "receipts/edit";
        }

        receiptUseCase.updateReceipt(receiptNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "入金「" + receiptNumber + "」を更新しました");
        return "redirect:/receipts/" + receiptNumber;
    }

    /**
     * 入金を削除.
     */
    @PostMapping("/{receiptNumber}/delete")
    public String delete(
            @PathVariable String receiptNumber,
            RedirectAttributes redirectAttributes) {

        receiptUseCase.deleteReceipt(receiptNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "入金「" + receiptNumber + "」を削除しました");
        return "redirect:/receipts";
    }

    /**
     * フォームの共通属性を追加.
     */
    private void addFormAttributes(Model model) {
        List<Partner> customers = partnerUseCase.getCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("statuses", ReceiptStatus.values());
        model.addAttribute("methods", ReceiptMethod.values());
    }
}
