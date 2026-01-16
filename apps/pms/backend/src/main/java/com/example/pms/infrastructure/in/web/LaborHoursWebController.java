package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DepartmentUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LaborHoursUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.LaborHours;
import com.example.pms.infrastructure.in.web.form.LaborHoursForm;
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
 * 工数実績画面コントローラー.
 */
@Controller
@RequestMapping("/labor-hours")
public class LaborHoursWebController {

    private final LaborHoursUseCase laborHoursUseCase;
    private final WorkOrderUseCase workOrderUseCase;
    private final ItemUseCase itemUseCase;
    private final ProcessUseCase processUseCase;
    private final DepartmentUseCase departmentUseCase;
    private final StaffUseCase staffUseCase;

    public LaborHoursWebController(
            LaborHoursUseCase laborHoursUseCase,
            WorkOrderUseCase workOrderUseCase,
            ItemUseCase itemUseCase,
            ProcessUseCase processUseCase,
            DepartmentUseCase departmentUseCase,
            StaffUseCase staffUseCase) {
        this.laborHoursUseCase = laborHoursUseCase;
        this.workOrderUseCase = workOrderUseCase;
        this.itemUseCase = itemUseCase;
        this.processUseCase = processUseCase;
        this.departmentUseCase = departmentUseCase;
        this.staffUseCase = staffUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("workOrders", workOrderUseCase.getAllWorkOrders());
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("processes", processUseCase.getAllProcesses());
        model.addAttribute("departments", departmentUseCase.getAllDepartments());
        model.addAttribute("staffList", staffUseCase.getAllStaff());
    }

    /**
     * 工数実績一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<LaborHours> pageResult = laborHoursUseCase.getLaborHoursList(page, size, keyword);

        model.addAttribute("laborHoursList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "labor-hours/list";
    }

    /**
     * 工数実績登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new LaborHoursForm());
        addMasterData(model);
        return "labor-hours/new";
    }

    /**
     * 工数実績を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") LaborHoursForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "labor-hours/new";
        }

        laborHoursUseCase.createLaborHours(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "工数実績を登録しました");
        return "redirect:/labor-hours";
    }

    /**
     * 工数実績詳細画面を表示する.
     */
    @GetMapping("/{laborHoursNumber}")
    public String show(
            @PathVariable String laborHoursNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return laborHoursUseCase.getLaborHours(laborHoursNumber)
            .map(laborHours -> {
                model.addAttribute("laborHours", laborHours);
                return "labor-hours/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "工数実績が見つかりません");
                return "redirect:/labor-hours";
            });
    }

    /**
     * 工数実績編集画面を表示する.
     */
    @GetMapping("/{laborHoursNumber}/edit")
    public String editForm(
            @PathVariable String laborHoursNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return laborHoursUseCase.getLaborHours(laborHoursNumber)
            .map(laborHours -> {
                model.addAttribute("form", LaborHoursForm.fromEntity(laborHours));
                addMasterData(model);
                return "labor-hours/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "工数実績が見つかりません");
                return "redirect:/labor-hours";
            });
    }

    /**
     * 工数実績を更新する.
     */
    @PostMapping("/{laborHoursNumber}")
    public String update(
            @PathVariable String laborHoursNumber,
            @Valid @ModelAttribute("form") LaborHoursForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "labor-hours/edit";
        }

        laborHoursUseCase.updateLaborHours(laborHoursNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "工数実績を更新しました");
        return "redirect:/labor-hours";
    }

    /**
     * 工数実績を削除する.
     */
    @PostMapping("/{laborHoursNumber}/delete")
    public String delete(
            @PathVariable String laborHoursNumber,
            RedirectAttributes redirectAttributes) {

        laborHoursUseCase.deleteLaborHours(laborHoursNumber);
        redirectAttributes.addFlashAttribute("successMessage", "工数実績を削除しました");
        return "redirect:/labor-hours";
    }
}
