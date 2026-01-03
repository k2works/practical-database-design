package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.DepartmentUseCase;
import com.example.sms.application.port.in.EmployeeUseCase;
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
import java.util.Locale;

/**
 * 社員マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/employees")
public class EmployeeWebController {

    private final EmployeeUseCase employeeUseCase;
    private final DepartmentUseCase departmentUseCase;

    public EmployeeWebController(EmployeeUseCase employeeUseCase, DepartmentUseCase departmentUseCase) {
        this.employeeUseCase = employeeUseCase;
        this.departmentUseCase = departmentUseCase;
    }

    /**
     * 社員一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String departmentCode,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Employee> employees = getFilteredEmployees(departmentCode);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            employees = employees.stream()
                .filter(e -> (e.getEmployeeCode() != null
                        && e.getEmployeeCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (e.getEmployeeName() != null
                        && e.getEmployeeName().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (e.getEmployeeNameKana() != null
                        && e.getEmployeeNameKana().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        List<Department> departments = departmentUseCase.getAllDepartments();

        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);
        model.addAttribute("selectedDepartmentCode", departmentCode);
        model.addAttribute("keyword", keyword);
        return "employees/list";
    }

    private List<Employee> getFilteredEmployees(String departmentCode) {
        if (departmentCode != null && !departmentCode.isBlank()) {
            return employeeUseCase.getEmployeesByDepartment(departmentCode);
        } else {
            return employeeUseCase.getAllEmployees();
        }
    }

    /**
     * 社員詳細画面を表示.
     */
    @GetMapping("/{employeeCode}")
    public String show(@PathVariable String employeeCode, Model model) {
        Employee employee = employeeUseCase.getEmployeeByCode(employeeCode);

        // 所属部門情報を取得
        Department department = null;
        if (employee.getDepartmentCode() != null) {
            try {
                department = departmentUseCase.getDepartmentByCode(employee.getDepartmentCode());
            } catch (Exception e) {
                // 部門が見つからない場合は無視
            }
        }

        model.addAttribute("employee", employee);
        model.addAttribute("department", department);
        return "employees/show";
    }

    /**
     * 社員登録画面を表示.
     */
    @GetMapping("/new")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentUseCase.getAllDepartments());
        model.addAttribute("isNew", true);
        return "employees/form";
    }

    /**
     * 社員を登録.
     */
    @PostMapping
    public String create(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        employeeUseCase.createEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "社員を登録しました: " + employee.getEmployeeCode());
        return "redirect:/employees";
    }

    /**
     * 社員編集画面を表示.
     */
    @GetMapping("/{employeeCode}/edit")
    public String edit(@PathVariable String employeeCode, Model model) {
        Employee employee = employeeUseCase.getEmployeeByCode(employeeCode);
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentUseCase.getAllDepartments());
        model.addAttribute("isNew", false);
        return "employees/form";
    }

    /**
     * 社員を更新.
     */
    @PostMapping("/{employeeCode}")
    public String update(@PathVariable String employeeCode,
                         @ModelAttribute Employee employee,
                         RedirectAttributes redirectAttributes) {
        employee.setEmployeeCode(employeeCode);
        employeeUseCase.updateEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "社員を更新しました: " + employeeCode);
        return "redirect:/employees/" + employeeCode;
    }
}
