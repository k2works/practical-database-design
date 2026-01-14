package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CostVarianceUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.CostVariance;
import com.example.pms.infrastructure.in.web.form.CostVarianceForm;
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
 * 原価差異画面コントローラー.
 */
@Controller
@RequestMapping("/cost-variances")
public class CostVarianceWebController {

    private final CostVarianceUseCase costVarianceUseCase;
    private final ItemUseCase itemUseCase;
    private final WorkOrderUseCase workOrderUseCase;

    public CostVarianceWebController(
            CostVarianceUseCase costVarianceUseCase,
            ItemUseCase itemUseCase,
            WorkOrderUseCase workOrderUseCase) {
        this.costVarianceUseCase = costVarianceUseCase;
        this.itemUseCase = itemUseCase;
        this.workOrderUseCase = workOrderUseCase;
    }

    /**
     * 原価差異一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<CostVariance> pageResult =
            costVarianceUseCase.getCostVarianceList(page, size, keyword);

        model.addAttribute("varianceList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "cost-variances/list";
    }

    /**
     * 原価差異登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new CostVarianceForm());
        addFormAttributes(model);
        return "cost-variances/new";
    }

    /**
     * 原価差異を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") CostVarianceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "cost-variances/new";
        }

        costVarianceUseCase.createCostVariance(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "原価差異を登録しました");
        return "redirect:/cost-variances";
    }

    /**
     * 原価差異詳細画面を表示する.
     */
    @GetMapping("/{workOrderNumber}")
    public String show(
            @PathVariable String workOrderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return costVarianceUseCase.getCostVariance(workOrderNumber)
            .map(variance -> {
                model.addAttribute("variance", variance);
                return "cost-variances/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "原価差異が見つかりません");
                return "redirect:/cost-variances";
            });
    }

    /**
     * 原価差異編集画面を表示する.
     */
    @GetMapping("/{workOrderNumber}/edit")
    public String editForm(
            @PathVariable String workOrderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return costVarianceUseCase.getCostVariance(workOrderNumber)
            .map(variance -> {
                model.addAttribute("form", CostVarianceForm.fromEntity(variance));
                addFormAttributes(model);
                return "cost-variances/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "原価差異が見つかりません");
                return "redirect:/cost-variances";
            });
    }

    /**
     * 原価差異を更新する.
     */
    @PostMapping("/{workOrderNumber}")
    public String update(
            @PathVariable String workOrderNumber,
            @Valid @ModelAttribute("form") CostVarianceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "cost-variances/edit";
        }

        costVarianceUseCase.updateCostVariance(workOrderNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "原価差異を更新しました");
        return "redirect:/cost-variances";
    }

    /**
     * 原価差異を削除する.
     */
    @PostMapping("/{workOrderNumber}/delete")
    public String delete(
            @PathVariable String workOrderNumber,
            RedirectAttributes redirectAttributes) {

        costVarianceUseCase.deleteCostVariance(workOrderNumber);
        redirectAttributes.addFlashAttribute("successMessage", "原価差異を削除しました");
        return "redirect:/cost-variances";
    }

    /**
     * フォーム用の共通属性を追加する.
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("workOrders", workOrderUseCase.getAllWorkOrders());
    }
}
