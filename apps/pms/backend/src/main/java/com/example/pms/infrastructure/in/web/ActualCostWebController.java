package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ActualCostUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.ActualCost;
import com.example.pms.infrastructure.in.web.form.ActualCostForm;
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
 * 製造原価画面コントローラー.
 */
@Controller
@RequestMapping("/manufacturing-costs")
public class ActualCostWebController {

    private final ActualCostUseCase actualCostUseCase;
    private final ItemUseCase itemUseCase;
    private final WorkOrderUseCase workOrderUseCase;

    public ActualCostWebController(
            ActualCostUseCase actualCostUseCase,
            ItemUseCase itemUseCase,
            WorkOrderUseCase workOrderUseCase) {
        this.actualCostUseCase = actualCostUseCase;
        this.itemUseCase = itemUseCase;
        this.workOrderUseCase = workOrderUseCase;
    }

    /**
     * 製造原価一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<ActualCost> pageResult =
            actualCostUseCase.getActualCostList(page, size, keyword);

        model.addAttribute("actualCostList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "manufacturing-costs/list";
    }

    /**
     * 製造原価登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ActualCostForm());
        addFormAttributes(model);
        return "manufacturing-costs/new";
    }

    /**
     * 製造原価を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ActualCostForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "manufacturing-costs/new";
        }

        actualCostUseCase.createActualCost(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "製造原価を登録しました");
        return "redirect:/manufacturing-costs";
    }

    /**
     * 製造原価詳細画面を表示する.
     */
    @GetMapping("/{workOrderNumber}")
    public String show(
            @PathVariable String workOrderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return actualCostUseCase.getActualCost(workOrderNumber)
            .map(actualCost -> {
                model.addAttribute("actualCost", actualCost);
                return "manufacturing-costs/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "製造原価が見つかりません");
                return "redirect:/manufacturing-costs";
            });
    }

    /**
     * 製造原価編集画面を表示する.
     */
    @GetMapping("/{workOrderNumber}/edit")
    public String editForm(
            @PathVariable String workOrderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return actualCostUseCase.getActualCost(workOrderNumber)
            .map(actualCost -> {
                model.addAttribute("form", ActualCostForm.fromEntity(actualCost));
                addFormAttributes(model);
                return "manufacturing-costs/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "製造原価が見つかりません");
                return "redirect:/manufacturing-costs";
            });
    }

    /**
     * 製造原価を更新する.
     */
    @PostMapping("/{workOrderNumber}")
    public String update(
            @PathVariable String workOrderNumber,
            @Valid @ModelAttribute("form") ActualCostForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "manufacturing-costs/edit";
        }

        actualCostUseCase.updateActualCost(workOrderNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "製造原価を更新しました");
        return "redirect:/manufacturing-costs";
    }

    /**
     * 製造原価を削除する.
     */
    @PostMapping("/{workOrderNumber}/delete")
    public String delete(
            @PathVariable String workOrderNumber,
            RedirectAttributes redirectAttributes) {

        actualCostUseCase.deleteActualCost(workOrderNumber);
        redirectAttributes.addFlashAttribute("successMessage", "製造原価を削除しました");
        return "redirect:/manufacturing-costs";
    }

    /**
     * フォーム用の共通属性を追加する.
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("workOrders", workOrderUseCase.getAllWorkOrders());
    }
}
