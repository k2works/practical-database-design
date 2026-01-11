package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.DepartmentUseCase;
import com.example.fas.application.port.in.dto.DepartmentResponse;
import com.example.fas.domain.exception.AccountingException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.infrastructure.in.web.form.DepartmentForm;
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
     * 部門一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer level,
            Model model) {

        int pageSize = Math.min(size, 100);

        PageResult<DepartmentResponse> pageResult = departmentUseCase.getDepartments(page, pageSize, keyword, level);

        model.addAttribute("departments", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedLevel", level);
        return "departments/list";
    }

    /**
     * 部門詳細画面を表示.
     */
    @GetMapping("/{departmentCode}")
    public String show(@PathVariable String departmentCode, Model model) {
        DepartmentResponse department = departmentUseCase.getDepartment(departmentCode);
        model.addAttribute("department", department);
        return "departments/show";
    }

    /**
     * 部門登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new DepartmentForm());
        return "departments/new";
    }

    /**
     * 部門を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") DepartmentForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "departments/new";
        }

        try {
            departmentUseCase.createDepartment(form.toCreateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "部門を登録しました");
            return "redirect:/departments";
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "departments/new";
        }
    }

    /**
     * 部門編集フォームを表示.
     */
    @GetMapping("/{departmentCode}/edit")
    public String editForm(@PathVariable String departmentCode, Model model) {
        DepartmentResponse department = departmentUseCase.getDepartment(departmentCode);
        model.addAttribute("form", DepartmentForm.from(department));
        return "departments/edit";
    }

    /**
     * 部門を更新.
     */
    @PostMapping("/{departmentCode}")
    public String update(
            @PathVariable String departmentCode,
            @Valid @ModelAttribute("form") DepartmentForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "departments/edit";
        }

        try {
            departmentUseCase.updateDepartment(departmentCode, form.toUpdateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "部門を更新しました");
            return "redirect:/departments/" + departmentCode;
        } catch (AccountingException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "departments/edit";
        }
    }

    /**
     * 部門を削除.
     */
    @PostMapping("/{departmentCode}/delete")
    public String delete(
            @PathVariable String departmentCode,
            RedirectAttributes redirectAttributes) {

        departmentUseCase.deleteDepartment(departmentCode);
        redirectAttributes.addFlashAttribute("successMessage", "部門を削除しました");
        return "redirect:/departments";
    }
}
