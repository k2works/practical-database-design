package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.DepartmentUseCase;
import com.example.sms.domain.model.department.Department;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

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
     * 部門一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Department> departments = getFilteredDepartments(level);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            departments = departments.stream()
                .filter(d -> (d.getDepartmentCode() != null
                        && d.getDepartmentCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (d.getDepartmentName() != null
                        && d.getDepartmentName().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("departments", departments);
        model.addAttribute("selectedLevel", level);
        model.addAttribute("keyword", keyword);
        return "departments/list";
    }

    private List<Department> getFilteredDepartments(Integer level) {
        if (level != null) {
            return departmentUseCase.getDepartmentsByHierarchyLevel(level);
        } else {
            return departmentUseCase.getAllDepartments();
        }
    }

    /**
     * 部門詳細画面を表示.
     */
    @GetMapping("/{departmentCode}")
    public String show(@PathVariable String departmentCode, Model model) {
        Department department = departmentUseCase.getDepartmentByCode(departmentCode);
        model.addAttribute("department", department);
        return "departments/show";
    }

    /**
     * 部門登録画面を表示.
     */
    @GetMapping("/new")
    public String newDepartment(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("isNew", true);
        return "departments/form";
    }

    /**
     * 部門を登録.
     */
    @PostMapping
    public String create(@ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        departmentUseCase.createDepartment(department);
        redirectAttributes.addFlashAttribute("successMessage", "部門を登録しました: " + department.getDepartmentCode());
        return "redirect:/departments";
    }

    /**
     * 部門編集画面を表示.
     */
    @GetMapping("/{departmentCode}/edit")
    public String edit(@PathVariable String departmentCode, Model model) {
        Department department = departmentUseCase.getDepartmentByCode(departmentCode);
        model.addAttribute("department", department);
        model.addAttribute("isNew", false);
        return "departments/form";
    }

    /**
     * 部門を更新.
     */
    @PostMapping("/{departmentCode}")
    public String update(@PathVariable String departmentCode,
                         @ModelAttribute Department department,
                         RedirectAttributes redirectAttributes) {
        department.setDepartmentCode(departmentCode);
        departmentUseCase.updateDepartment(department);
        redirectAttributes.addFlashAttribute("successMessage", "部門を更新しました: " + departmentCode);
        return "redirect:/departments/" + departmentCode;
    }
}
