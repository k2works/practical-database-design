package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.QuotationUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationStatus;
import com.example.sms.infrastructure.in.web.form.QuotationForm;
import com.example.sms.infrastructure.in.web.form.QuotationForm.QuotationDetailForm;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 見積画面コントローラー.
 */
@Controller
@RequestMapping("/estimates")
public class QuotationWebController {

    private final QuotationUseCase quotationUseCase;
    private final PartnerUseCase partnerUseCase;
    private final ProductUseCase productUseCase;

    public QuotationWebController(
            QuotationUseCase quotationUseCase,
            PartnerUseCase partnerUseCase,
            ProductUseCase productUseCase) {
        this.quotationUseCase = quotationUseCase;
        this.partnerUseCase = partnerUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 見積一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Quotation> quotationPage = quotationUseCase.getQuotations(page, size, keyword);

        model.addAttribute("quotations", quotationPage.getContent());
        model.addAttribute("page", quotationPage);
        model.addAttribute("statuses", QuotationStatus.values());
        model.addAttribute("customers", partnerUseCase.getCustomers());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "estimates/list";
    }

    /**
     * 見積詳細画面を表示.
     */
    @GetMapping("/{quotationNumber}")
    public String show(@PathVariable String quotationNumber, Model model) {
        Quotation quotation = quotationUseCase.getQuotationWithDetails(quotationNumber);
        model.addAttribute("quotation", quotation);
        return "estimates/show";
    }

    /**
     * 見積登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new QuotationForm());
        addFormAttributes(model);
        return "estimates/new";
    }

    /**
     * 見積を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") QuotationForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "estimates/new";
        }

        Quotation quotation = quotationUseCase.createQuotation(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "見積「" + quotation.getQuotationNumber() + "」を登録しました");
        return "redirect:/estimates";
    }

    /**
     * 見積編集フォームを表示.
     */
    @GetMapping("/{quotationNumber}/edit")
    public String editForm(@PathVariable String quotationNumber, Model model) {
        Quotation quotation = quotationUseCase.getQuotationWithDetails(quotationNumber);
        model.addAttribute("form", QuotationForm.from(quotation));
        addFormAttributes(model);
        return "estimates/edit";
    }

    /**
     * 見積を更新.
     */
    @PostMapping("/{quotationNumber}")
    public String update(
            @PathVariable String quotationNumber,
            @Valid @ModelAttribute("form") QuotationForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "estimates/edit";
        }

        quotationUseCase.updateQuotation(quotationNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "見積「" + quotationNumber + "」を更新しました");
        return "redirect:/estimates/" + quotationNumber;
    }

    /**
     * 見積を受注確定.
     */
    @PostMapping("/{quotationNumber}/confirm")
    public String confirm(
            @PathVariable String quotationNumber,
            @RequestParam Integer version,
            RedirectAttributes redirectAttributes) {

        quotationUseCase.confirmQuotation(quotationNumber, version);
        redirectAttributes.addFlashAttribute("successMessage",
            "見積「" + quotationNumber + "」を受注確定しました");
        return "redirect:/estimates/" + quotationNumber;
    }

    /**
     * 見積を失注.
     */
    @PostMapping("/{quotationNumber}/lose")
    public String lose(
            @PathVariable String quotationNumber,
            @RequestParam Integer version,
            RedirectAttributes redirectAttributes) {

        quotationUseCase.loseQuotation(quotationNumber, version);
        redirectAttributes.addFlashAttribute("successMessage",
            "見積「" + quotationNumber + "」を失注にしました");
        return "redirect:/estimates/" + quotationNumber;
    }

    /**
     * 見積を削除.
     */
    @PostMapping("/{quotationNumber}/delete")
    public String delete(
            @PathVariable String quotationNumber,
            RedirectAttributes redirectAttributes) {

        quotationUseCase.deleteQuotation(quotationNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "見積「" + quotationNumber + "」を削除しました");
        return "redirect:/estimates";
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new QuotationDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "estimates/fragments :: detailRow";
    }

    /**
     * 商品情報を取得（htmx用JSON）.
     */
    @GetMapping("/product-info")
    @ResponseBody
    public Product getProductInfo(@RequestParam String productCode) {
        return productUseCase.getProductByCode(productCode);
    }

    /**
     * フォームの共通属性を追加.
     */
    private void addFormAttributes(Model model) {
        List<Partner> customers = partnerUseCase.getCustomers();
        List<Product> products = productUseCase.getAllProducts();
        model.addAttribute("customers", customers);
        model.addAttribute("products", products);
        model.addAttribute("statuses", QuotationStatus.values());
    }
}
