package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DepartmentUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.department.Department;
import com.example.pms.infrastructure.in.web.form.DepartmentForm;
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
 * 部門マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/departments")
public class DepartmentWebController {

    private final DepartmentUseCase departmentUseCase;

    public DepartmentWebController(DepartmentUseCase departmentUseCase) {
        this.departmentUseCase = departmentUseCase;
    }

    /**
     * 部門一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Department> pageResult = departmentUseCase.getDepartments(page, size, keyword);

        model.addAttribute("departments", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "departments/list";
    }

    /**
     * 部門登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new DepartmentForm());
        return "departments/new";
    }

    /**
     * 部門を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") DepartmentForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "departments/new";
        }

        Department department = departmentUseCase.createDepartment(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "部門「" + department.getDepartmentCode() + " - " + department.getDepartmentName() + "」を登録しました");
        return "redirect:/departments";
    }

    /**
     * 部門編集画面を表示する.
     */
    @GetMapping("/{departmentCode}/edit")
    public String editForm(@PathVariable String departmentCode, Model model, RedirectAttributes redirectAttributes) {
        return departmentUseCase.getDepartment(departmentCode)
            .map(department -> {
                DepartmentForm form = new DepartmentForm();
                form.setDepartmentCode(department.getDepartmentCode());
                form.setDepartmentName(department.getDepartmentName());
                form.setValidFrom(department.getValidFrom());
                form.setValidTo(department.getValidTo());
                model.addAttribute("form", form);
                model.addAttribute("departmentCode", departmentCode);
                return "departments/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "部門が見つかりません");
                return "redirect:/departments";
            });
    }

    /**
     * 部門を更新する.
     */
    @PostMapping("/{departmentCode}")
    public String update(
            @PathVariable String departmentCode,
            @Valid @ModelAttribute("form") DepartmentForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentCode", departmentCode);
            return "departments/edit";
        }

        Department department = departmentUseCase.updateDepartment(departmentCode, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "部門「" + department.getDepartmentCode() + " - " + department.getDepartmentName() + "」を更新しました");
        return "redirect:/departments";
    }

    /**
     * 部門を削除する.
     */
    @PostMapping("/{departmentCode}/delete")
    public String delete(@PathVariable String departmentCode, RedirectAttributes redirectAttributes) {
        departmentUseCase.deleteDepartment(departmentCode);
        redirectAttributes.addFlashAttribute("successMessage", "部門を削除しました");
        return "redirect:/departments";
    }
}
