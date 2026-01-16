package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ProcessRouteUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.ProcessRoute;
import com.example.pms.infrastructure.in.web.form.ProcessRouteForm;
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
 * 工程表画面コントローラー.
 */
@Controller
@RequestMapping("/process-routes")
public class ProcessRouteWebController {

    private final ProcessRouteUseCase processRouteUseCase;
    private final ProcessUseCase processUseCase;

    public ProcessRouteWebController(
            ProcessRouteUseCase processRouteUseCase,
            ProcessUseCase processUseCase) {
        this.processRouteUseCase = processRouteUseCase;
        this.processUseCase = processUseCase;
    }

    /**
     * 工程表一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String itemCode,
            Model model) {

        PageResult<ProcessRoute> pageResult = processRouteUseCase.getProcessRoutes(page, size, itemCode);

        model.addAttribute("routes", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("itemCode", itemCode);
        return "process-routes/list";
    }

    /**
     * 工程表登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProcessRouteForm());
        model.addAttribute("processes", processUseCase.getAllProcesses());
        return "process-routes/new";
    }

    /**
     * 工程表を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ProcessRouteForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("processes", processUseCase.getAllProcesses());
            return "process-routes/new";
        }

        ProcessRoute route = processRouteUseCase.createProcessRoute(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "工程表「" + route.getItemCode() + " - 工順" + route.getSequence() + "」を登録しました");
        return "redirect:/process-routes";
    }

    /**
     * 工程表編集画面を表示する.
     */
    @GetMapping("/{itemCode}/{sequence}/edit")
    public String editForm(
            @PathVariable String itemCode,
            @PathVariable Integer sequence,
            Model model,
            RedirectAttributes redirectAttributes) {

        return processRouteUseCase.getProcessRoute(itemCode, sequence)
            .map(route -> {
                ProcessRouteForm form = new ProcessRouteForm();
                form.setItemCode(route.getItemCode());
                form.setSequence(route.getSequence());
                form.setProcessCode(route.getProcessCode());
                form.setStandardTime(route.getStandardTime());
                form.setSetupTime(route.getSetupTime());
                model.addAttribute("form", form);
                model.addAttribute("itemCode", itemCode);
                model.addAttribute("sequence", sequence);
                model.addAttribute("processes", processUseCase.getAllProcesses());
                return "process-routes/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "工程表が見つかりません");
                return "redirect:/process-routes";
            });
    }

    /**
     * 工程表を更新する.
     */
    @PostMapping("/{itemCode}/{sequence}")
    public String update(
            @PathVariable String itemCode,
            @PathVariable Integer sequence,
            @Valid @ModelAttribute("form") ProcessRouteForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("itemCode", itemCode);
            model.addAttribute("sequence", sequence);
            model.addAttribute("processes", processUseCase.getAllProcesses());
            return "process-routes/edit";
        }

        ProcessRoute route = processRouteUseCase.updateProcessRoute(itemCode, sequence, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "工程表「" + route.getItemCode() + " - 工順" + route.getSequence() + "」を更新しました");
        return "redirect:/process-routes";
    }

    /**
     * 工程表を削除する.
     */
    @PostMapping("/{itemCode}/{sequence}/delete")
    public String delete(
            @PathVariable String itemCode,
            @PathVariable Integer sequence,
            RedirectAttributes redirectAttributes) {
        processRouteUseCase.deleteProcessRoute(itemCode, sequence);
        redirectAttributes.addFlashAttribute("successMessage", "工程表を削除しました");
        return "redirect:/process-routes";
    }
}
