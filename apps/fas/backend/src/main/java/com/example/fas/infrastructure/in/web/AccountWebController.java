package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.domain.exception.AccountingException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.infrastructure.in.web.form.AccountForm;
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
 * 勘定科目マスタ画面コントローラー（モノリス版）.
 */
@Controller
@RequestMapping("/accounts")
public class AccountWebController {

    private final AccountUseCase accountUseCase;

    public AccountWebController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    /**
     * 勘定科目一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String bsPlType,
            @RequestParam(required = false) String keyword,
            Model model) {

        // ページサイズの上限を設定
        int pageSize = Math.min(size, 100);

        PageResult<AccountResponse> pageResult = accountUseCase.getAccounts(page, pageSize, bsPlType, keyword);

        model.addAttribute("accounts", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("selectedBsPlType", bsPlType);
        model.addAttribute("keyword", keyword);
        return "accounts/list";
    }

    /**
     * 勘定科目詳細画面を表示.
     */
    @GetMapping("/{accountCode}")
    public String show(@PathVariable String accountCode, Model model) {
        AccountResponse account = accountUseCase.getAccount(accountCode);
        model.addAttribute("account", account);
        return "accounts/show";
    }

    /**
     * 勘定科目登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AccountForm());
        return "accounts/new";
    }

    /**
     * 勘定科目を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") AccountForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "accounts/new";
        }

        try {
            accountUseCase.createAccount(form.toCreateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "勘定科目を登録しました");
            return "redirect:/accounts";
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "accounts/new";
        }
    }

    /**
     * 勘定科目編集フォームを表示.
     */
    @GetMapping("/{accountCode}/edit")
    public String editForm(@PathVariable String accountCode, Model model) {
        AccountResponse account = accountUseCase.getAccount(accountCode);
        model.addAttribute("form", AccountForm.from(account));
        return "accounts/edit";
    }

    /**
     * 勘定科目を更新.
     */
    @PostMapping("/{accountCode}")
    public String update(
            @PathVariable String accountCode,
            @Valid @ModelAttribute("form") AccountForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "accounts/edit";
        }

        try {
            accountUseCase.updateAccount(accountCode, form.toUpdateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "勘定科目を更新しました");
            return "redirect:/accounts/" + accountCode;
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "accounts/edit";
        }
    }

    /**
     * 勘定科目を削除.
     */
    @PostMapping("/{accountCode}/delete")
    public String delete(
            @PathVariable String accountCode,
            RedirectAttributes redirectAttributes) {

        accountUseCase.deleteAccount(accountCode);
        redirectAttributes.addFlashAttribute("successMessage", "勘定科目を削除しました");
        return "redirect:/accounts";
    }
}
