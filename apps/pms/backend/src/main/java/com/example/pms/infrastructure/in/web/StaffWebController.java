package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.staff.Staff;
import com.example.pms.infrastructure.in.web.form.StaffForm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

/**
 * 担当者マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/staff")
public class StaffWebController {

    private final StaffUseCase staffUseCase;

    public StaffWebController(StaffUseCase staffUseCase) {
        this.staffUseCase = staffUseCase;
    }

    /**
     * 担当者一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Staff> pageResult = staffUseCase.getStaffList(page, size, keyword);

        model.addAttribute("staffList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "staff/list";
    }

    /**
     * 担当者登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new StaffForm());
        return "staff/new";
    }

    /**
     * 担当者を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") StaffForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "staff/new";
        }

        Staff staff = staffUseCase.createStaff(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "担当者「" + staff.getStaffCode() + "」を登録しました");
        return "redirect:/staff";
    }

    /**
     * 担当者編集画面を表示する.
     */
    @GetMapping("/{staffCode}/{effectiveFrom}/edit")
    public String editForm(
            @PathVariable String staffCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            Model model,
            RedirectAttributes redirectAttributes) {

        return staffUseCase.getStaff(staffCode, effectiveFrom)
            .map(staff -> {
                StaffForm form = new StaffForm();
                form.setStaffCode(staff.getStaffCode());
                form.setStaffName(staff.getStaffName());
                form.setEffectiveFrom(staff.getEffectiveFrom());
                form.setEffectiveTo(staff.getEffectiveTo());
                form.setDepartmentCode(staff.getDepartmentCode());
                form.setEmail(staff.getEmail());
                form.setPhoneNumber(staff.getPhoneNumber());
                model.addAttribute("form", form);
                model.addAttribute("staffCode", staffCode);
                model.addAttribute("effectiveFrom", effectiveFrom);
                return "staff/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "担当者が見つかりません");
                return "redirect:/staff";
            });
    }

    /**
     * 担当者を更新する.
     */
    @PostMapping("/{staffCode}/{effectiveFrom}")
    public String update(
            @PathVariable String staffCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            @Valid @ModelAttribute("form") StaffForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("staffCode", staffCode);
            model.addAttribute("effectiveFrom", effectiveFrom);
            return "staff/edit";
        }

        Staff staff = staffUseCase.updateStaff(staffCode, effectiveFrom, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "担当者「" + staff.getStaffCode() + "」を更新しました");
        return "redirect:/staff";
    }

    /**
     * 担当者を削除する.
     */
    @PostMapping("/{staffCode}/{effectiveFrom}/delete")
    public String delete(
            @PathVariable String staffCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            RedirectAttributes redirectAttributes) {

        staffUseCase.deleteStaff(staffCode, effectiveFrom);
        redirectAttributes.addFlashAttribute("successMessage", "担当者を削除しました");
        return "redirect:/staff";
    }
}
