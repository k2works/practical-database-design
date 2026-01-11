package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.DepartmentUseCase;
import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.dto.JournalResponse;
import com.example.fas.domain.exception.AccountingException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.infrastructure.in.web.form.JournalForm;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
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
 * 仕訳画面コントローラー.
 */
@Controller
@RequestMapping("/journals")
public class JournalWebController {

    private final JournalUseCase journalUseCase;
    private final AccountUseCase accountUseCase;
    private final DepartmentUseCase departmentUseCase;

    public JournalWebController(JournalUseCase journalUseCase,
            AccountUseCase accountUseCase,
            DepartmentUseCase departmentUseCase) {
        this.journalUseCase = journalUseCase;
        this.accountUseCase = accountUseCase;
        this.departmentUseCase = departmentUseCase;
    }

    /**
     * 仕訳一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String keyword,
            Model model) {

        // ページサイズの上限を設定
        int pageSize = Math.min(size, 100);

        PageResult<JournalResponse> pageResult = journalUseCase.getJournals(
                page, pageSize, fromDate, toDate, keyword);

        model.addAttribute("journals", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("keyword", keyword);
        return "journals/list";
    }

    /**
     * 仕訳詳細画面を表示.
     */
    @GetMapping("/{voucherNumber}")
    public String show(@PathVariable String voucherNumber, Model model) {
        JournalResponse journal = journalUseCase.getJournal(voucherNumber);
        model.addAttribute("journal", journal);
        return "journals/show";
    }

    /**
     * 仕訳入力フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        JournalForm form = new JournalForm();
        form.initializeLines();
        model.addAttribute("form", form);
        model.addAttribute("accounts", accountUseCase.getAllAccounts());
        model.addAttribute("departments", departmentUseCase.getAllDepartments());
        return "journals/new";
    }

    /**
     * 仕訳を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") JournalForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountUseCase.getAllAccounts());
            model.addAttribute("departments", departmentUseCase.getAllDepartments());
            return "journals/new";
        }

        try {
            JournalResponse created = journalUseCase.createJournal(form.toCreateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "仕訳を登録しました");
            return "redirect:/journals/" + created.getJournalVoucherNumber();
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accounts", accountUseCase.getAllAccounts());
            model.addAttribute("departments", departmentUseCase.getAllDepartments());
            return "journals/new";
        }
    }

    /**
     * 仕訳を取消（赤黒処理）.
     */
    @PostMapping("/{voucherNumber}/cancel")
    public String cancel(
            @PathVariable String voucherNumber,
            RedirectAttributes redirectAttributes) {

        JournalResponse reversal = journalUseCase.cancelJournal(voucherNumber);
        redirectAttributes.addFlashAttribute("successMessage",
                "仕訳を取消しました（赤伝票番号: " + reversal.getJournalVoucherNumber() + "）");
        return "redirect:/journals/" + voucherNumber;
    }

    /**
     * 仕訳を削除.
     */
    @PostMapping("/{voucherNumber}/delete")
    public String delete(
            @PathVariable String voucherNumber,
            RedirectAttributes redirectAttributes) {

        journalUseCase.deleteJournal(voucherNumber);
        redirectAttributes.addFlashAttribute("successMessage", "仕訳を削除しました");
        return "redirect:/journals";
    }
}
