package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.ProcessInspectionUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.ProcessInspection;
import com.example.pms.infrastructure.in.web.form.ProcessInspectionForm;
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
 * 不良管理（工程検査）画面コントローラー.
 */
@Controller
@RequestMapping("/defect-management")
public class ProcessInspectionWebController {

    private final ProcessInspectionUseCase processInspectionUseCase;
    private final WorkOrderUseCase workOrderUseCase;
    private final ProcessUseCase processUseCase;
    private final ItemUseCase itemUseCase;
    private final DefectUseCase defectUseCase;

    public ProcessInspectionWebController(
            ProcessInspectionUseCase processInspectionUseCase,
            WorkOrderUseCase workOrderUseCase,
            ProcessUseCase processUseCase,
            ItemUseCase itemUseCase,
            DefectUseCase defectUseCase) {
        this.processInspectionUseCase = processInspectionUseCase;
        this.workOrderUseCase = workOrderUseCase;
        this.processUseCase = processUseCase;
        this.itemUseCase = itemUseCase;
        this.defectUseCase = defectUseCase;
    }

    /**
     * 不良管理一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<ProcessInspection> pageResult =
            processInspectionUseCase.getProcessInspectionList(page, size, keyword);

        model.addAttribute("inspectionList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "defect-management/list";
    }

    /**
     * 不良管理登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProcessInspectionForm());
        addFormAttributes(model);
        return "defect-management/new";
    }

    /**
     * 不良管理を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ProcessInspectionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "defect-management/new";
        }

        processInspectionUseCase.createProcessInspection(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "工程検査を登録しました");
        return "redirect:/defect-management";
    }

    /**
     * 不良管理詳細画面を表示する.
     */
    @GetMapping("/{inspectionNumber}")
    public String show(
            @PathVariable String inspectionNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return processInspectionUseCase.getProcessInspection(inspectionNumber)
            .map(inspection -> {
                model.addAttribute("inspection", inspection);
                return "defect-management/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "工程検査が見つかりません");
                return "redirect:/defect-management";
            });
    }

    /**
     * 不良管理編集画面を表示する.
     */
    @GetMapping("/{inspectionNumber}/edit")
    public String editForm(
            @PathVariable String inspectionNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return processInspectionUseCase.getProcessInspection(inspectionNumber)
            .map(inspection -> {
                model.addAttribute("form", ProcessInspectionForm.fromEntity(inspection));
                addFormAttributes(model);
                return "defect-management/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "工程検査が見つかりません");
                return "redirect:/defect-management";
            });
    }

    /**
     * 不良管理を更新する.
     */
    @PostMapping("/{inspectionNumber}")
    public String update(
            @PathVariable String inspectionNumber,
            @Valid @ModelAttribute("form") ProcessInspectionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "defect-management/edit";
        }

        processInspectionUseCase.updateProcessInspection(inspectionNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "工程検査を更新しました");
        return "redirect:/defect-management";
    }

    /**
     * 不良管理を削除する.
     */
    @PostMapping("/{inspectionNumber}/delete")
    public String delete(
            @PathVariable String inspectionNumber,
            RedirectAttributes redirectAttributes) {

        processInspectionUseCase.deleteProcessInspection(inspectionNumber);
        redirectAttributes.addFlashAttribute("successMessage", "工程検査を削除しました");
        return "redirect:/defect-management";
    }

    /**
     * フォーム用の共通属性を追加する.
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("workOrders", workOrderUseCase.getAllWorkOrders());
        model.addAttribute("processes", processUseCase.getAllProcesses());
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("defects", defectUseCase.getAllDefects());
    }
}
