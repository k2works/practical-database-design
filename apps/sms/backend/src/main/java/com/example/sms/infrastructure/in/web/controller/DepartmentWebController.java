package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.DepartmentUseCase;
import com.example.sms.application.port.in.EmployeeUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.department.Department;
import com.example.sms.domain.model.employee.Employee;
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

/**
 * 部門マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/departments")
public class DepartmentWebController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final DepartmentUseCase departmentUseCase;
    private final EmployeeUseCase employeeUseCase;

    public DepartmentWebController(DepartmentUseCase departmentUseCase, EmployeeUseCase employeeUseCase) {
        this.departmentUseCase = departmentUseCase;
        this.employeeUseCase = employeeUseCase;
    }

    /**
     * 部門一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Department> departmentPage = departmentUseCase.getDepartments(page, size, level, keyword);

        model.addAttribute("departments", departmentPage.getContent());
        model.addAttribute("page", departmentPage);
        model.addAttribute("selectedLevel", level);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "departments/list";
    }

    /**
     * 部門詳細画面を表示.
     */
    @GetMapping("/{departmentCode}")
    public String show(@PathVariable String departmentCode, Model model) {
        Department department = departmentUseCase.getDepartmentByCode(departmentCode);
        List<Employee> employees = employeeUseCase.getEmployeesByDepartment(departmentCode);
        model.addAttribute("department", department);
        model.addAttribute("employees", employees);
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
