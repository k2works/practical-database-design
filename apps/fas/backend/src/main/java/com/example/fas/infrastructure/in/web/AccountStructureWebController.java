package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountStructureUseCase;
import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.dto.AccountStructureResponse;
import com.example.fas.domain.exception.AccountingException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.infrastructure.in.web.form.AccountStructureForm;
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
 * 勘定科目構成マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/account-structures")
public class AccountStructureWebController {

    private final AccountStructureUseCase accountStructureUseCase;
    private final AccountUseCase accountUseCase;

    public AccountStructureWebController(AccountStructureUseCase accountStructureUseCase,
                                          AccountUseCase accountUseCase) {
        this.accountStructureUseCase = accountStructureUseCase;
        this.accountUseCase = accountUseCase;
    }

    /**
     * 勘定科目構成一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        int pageSize = Math.min(size, 100);

        PageResult<AccountStructureResponse> pageResult = accountStructureUseCase
                .getAccountStructures(page, pageSize, keyword);

        model.addAttribute("structures", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("keyword", keyword);
        return "account-structures/list";
    }

    /**
     * 勘定科目構成詳細画面を表示.
     */
    @GetMapping("/{accountCode}")
    public String show(@PathVariable String accountCode, Model model) {
        AccountStructureResponse structure = accountStructureUseCase.getAccountStructure(accountCode);
        model.addAttribute("structure", structure);
        return "account-structures/show";
    }

    /**
     * 勘定科目構成登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AccountStructureForm());
        model.addAttribute("accounts", accountUseCase.getAllAccounts());
        return "account-structures/new";
    }

    /**
     * 勘定科目構成を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") AccountStructureForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountUseCase.getAllAccounts());
            return "account-structures/new";
        }

        try {
            accountStructureUseCase.createAccountStructure(form.toCreateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "勘定科目構成を登録しました");
            return "redirect:/account-structures";
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accounts", accountUseCase.getAllAccounts());
            return "account-structures/new";
        }
    }

    /**
     * 勘定科目構成編集フォームを表示.
     */
    @GetMapping("/{accountCode}/edit")
    public String editForm(@PathVariable String accountCode, Model model) {
        AccountStructureResponse structure = accountStructureUseCase.getAccountStructure(accountCode);
        model.addAttribute("form", AccountStructureForm.from(structure));
        model.addAttribute("accounts", accountUseCase.getAllAccounts());
        return "account-structures/edit";
    }

    /**
     * 勘定科目構成を更新.
     */
    @PostMapping("/{accountCode}")
    public String update(
            @PathVariable String accountCode,
            @Valid @ModelAttribute("form") AccountStructureForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountUseCase.getAllAccounts());
            return "account-structures/edit";
        }

        try {
            accountStructureUseCase.updateAccountStructure(accountCode, form.toUpdateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "勘定科目構成を更新しました");
            return "redirect:/account-structures/" + accountCode;
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accounts", accountUseCase.getAllAccounts());
            return "account-structures/edit";
        }
    }

    /**
     * 勘定科目構成を削除.
     */
    @PostMapping("/{accountCode}/delete")
    public String delete(
            @PathVariable String accountCode,
            RedirectAttributes redirectAttributes) {

        accountStructureUseCase.deleteAccountStructure(accountCode);
        redirectAttributes.addFlashAttribute("successMessage", "勘定科目構成を削除しました");
        return "redirect:/account-structures";
    }
}
