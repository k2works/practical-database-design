package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.TaxTransactionUseCase;
import com.example.fas.application.port.in.dto.TaxTransactionResponse;
import com.example.fas.domain.exception.AccountingException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.infrastructure.in.web.form.TaxTransactionForm;
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

/**
 * 課税取引マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/tax-transactions")
public class TaxTransactionWebController {

    private final TaxTransactionUseCase taxTransactionUseCase;

    public TaxTransactionWebController(TaxTransactionUseCase taxTransactionUseCase) {
        this.taxTransactionUseCase = taxTransactionUseCase;
    }

    /**
     * 課税取引一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        int pageSize = Math.min(size, 100);

        PageResult<TaxTransactionResponse> pageResult = taxTransactionUseCase.getTaxTransactions(page, pageSize, keyword);

        model.addAttribute("taxTransactions", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("keyword", keyword);
        return "tax-transactions/list";
    }

    /**
     * 課税取引詳細画面を表示.
     */
    @GetMapping("/{taxCode}")
    public String show(@PathVariable String taxCode, Model model) {
        TaxTransactionResponse taxTransaction = taxTransactionUseCase.getTaxTransaction(taxCode);
        model.addAttribute("taxTransaction", taxTransaction);
        return "tax-transactions/show";
    }

    /**
     * 課税取引登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new TaxTransactionForm());
        return "tax-transactions/new";
    }

    /**
     * 課税取引を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") TaxTransactionForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "tax-transactions/new";
        }

        try {
            taxTransactionUseCase.createTaxTransaction(form.toCreateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "課税取引を登録しました");
            return "redirect:/tax-transactions";
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "tax-transactions/new";
        }
    }

    /**
     * 課税取引編集フォームを表示.
     */
    @GetMapping("/{taxCode}/edit")
    public String editForm(@PathVariable String taxCode, Model model) {
        TaxTransactionResponse taxTransaction = taxTransactionUseCase.getTaxTransaction(taxCode);
        model.addAttribute("form", TaxTransactionForm.from(taxTransaction));
        return "tax-transactions/edit";
    }

    /**
     * 課税取引を更新.
     */
    @PostMapping("/{taxCode}")
    public String update(
            @PathVariable String taxCode,
            @Valid @ModelAttribute("form") TaxTransactionForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "tax-transactions/edit";
        }

        try {
            taxTransactionUseCase.updateTaxTransaction(taxCode, form.toUpdateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "課税取引を更新しました");
            return "redirect:/tax-transactions/" + taxCode;
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "tax-transactions/edit";
        }
    }

    /**
     * 課税取引を削除.
     */
    @PostMapping("/{taxCode}/delete")
    public String delete(
            @PathVariable String taxCode,
            RedirectAttributes redirectAttributes) {

        taxTransactionUseCase.deleteTaxTransaction(taxCode);
        redirectAttributes.addFlashAttribute("successMessage", "課税取引を削除しました");
        return "redirect:/tax-transactions";
    }
}
