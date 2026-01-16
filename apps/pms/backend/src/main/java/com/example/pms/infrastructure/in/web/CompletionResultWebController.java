package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CompletionResultUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.CompletionResult;
import com.example.pms.infrastructure.in.web.form.CompletionResultForm;
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
 * 完成実績画面コントローラー.
 */
@Controller
@RequestMapping("/completion-results")
public class CompletionResultWebController {

    private final CompletionResultUseCase completionResultUseCase;
    private final WorkOrderUseCase workOrderUseCase;
    private final ItemUseCase itemUseCase;

    public CompletionResultWebController(
            CompletionResultUseCase completionResultUseCase,
            WorkOrderUseCase workOrderUseCase,
            ItemUseCase itemUseCase) {
        this.completionResultUseCase = completionResultUseCase;
        this.workOrderUseCase = workOrderUseCase;
        this.itemUseCase = itemUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("workOrders", workOrderUseCase.getAllWorkOrders());
        model.addAttribute("items", itemUseCase.getAllItems());
    }

    /**
     * 完成実績一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<CompletionResult> pageResult = completionResultUseCase.getCompletionResultList(page, size, keyword);

        model.addAttribute("completionResultList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "completion-results/list";
    }

    /**
     * 完成実績登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new CompletionResultForm());
        addMasterData(model);
        return "completion-results/new";
    }

    /**
     * 完成実績を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") CompletionResultForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "completion-results/new";
        }

        completionResultUseCase.createCompletionResult(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "完成実績を登録しました");
        return "redirect:/completion-results";
    }

    /**
     * 完成実績詳細画面を表示する.
     */
    @GetMapping("/{completionResultNumber}")
    public String show(
            @PathVariable String completionResultNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return completionResultUseCase.getCompletionResult(completionResultNumber)
            .map(completionResult -> {
                model.addAttribute("completionResult", completionResult);
                return "completion-results/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "完成実績が見つかりません");
                return "redirect:/completion-results";
            });
    }

    /**
     * 完成実績編集画面を表示する.
     */
    @GetMapping("/{completionResultNumber}/edit")
    public String editForm(
            @PathVariable String completionResultNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return completionResultUseCase.getCompletionResult(completionResultNumber)
            .map(completionResult -> {
                model.addAttribute("form", CompletionResultForm.fromEntity(completionResult));
                addMasterData(model);
                return "completion-results/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "完成実績が見つかりません");
                return "redirect:/completion-results";
            });
    }

    /**
     * 完成実績を更新する.
     */
    @PostMapping("/{completionResultNumber}")
    public String update(
            @PathVariable String completionResultNumber,
            @Valid @ModelAttribute("form") CompletionResultForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "completion-results/edit";
        }

        completionResultUseCase.updateCompletionResult(completionResultNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "完成実績を更新しました");
        return "redirect:/completion-results";
    }

    /**
     * 完成実績を削除する.
     */
    @PostMapping("/{completionResultNumber}/delete")
    public String delete(
            @PathVariable String completionResultNumber,
            RedirectAttributes redirectAttributes) {

        completionResultUseCase.deleteCompletionResult(completionResultNumber);
        redirectAttributes.addFlashAttribute("successMessage", "完成実績を削除しました");
        return "redirect:/completion-results";
    }
}
