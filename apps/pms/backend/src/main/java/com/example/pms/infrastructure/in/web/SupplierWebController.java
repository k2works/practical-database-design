package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.domain.model.supplier.SupplierType;
import com.example.pms.infrastructure.in.web.form.SupplierForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * 取引先マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/suppliers")
public class SupplierWebController {

    private final SupplierUseCase supplierUseCase;

    public SupplierWebController(SupplierUseCase supplierUseCase) {
        this.supplierUseCase = supplierUseCase;
    }

    /**
     * 取引先一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Supplier> pageResult = supplierUseCase.getSuppliers(page, size, keyword);

        model.addAttribute("suppliers", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "suppliers/list";
    }

    /**
     * 取引先登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new SupplierForm());
        model.addAttribute("supplierTypes", SupplierType.values());
        return "suppliers/new";
    }

    /**
     * 取引先を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") SupplierForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("supplierTypes", SupplierType.values());
            return "suppliers/new";
        }

        Supplier supplier = supplierUseCase.createSupplier(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "取引先「" + supplier.getSupplierCode() + " - " + supplier.getSupplierName() + "」を登録しました");
        return "redirect:/suppliers";
    }

    /**
     * 取引先編集画面を表示する.
     */
    @GetMapping("/{supplierCode}/{effectiveFrom}/edit")
    public String editForm(
            @PathVariable String supplierCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            Model model,
            RedirectAttributes redirectAttributes) {
        return supplierUseCase.getSupplier(supplierCode, effectiveFrom)
            .map(supplier -> {
                SupplierForm form = new SupplierForm();
                form.setSupplierCode(supplier.getSupplierCode());
                form.setEffectiveFrom(supplier.getEffectiveFrom());
                form.setEffectiveTo(supplier.getEffectiveTo());
                form.setSupplierName(supplier.getSupplierName());
                form.setSupplierNameKana(supplier.getSupplierNameKana());
                form.setSupplierType(supplier.getSupplierType());
                form.setPostalCode(supplier.getPostalCode());
                form.setAddress(supplier.getAddress());
                form.setPhoneNumber(supplier.getPhoneNumber());
                form.setFaxNumber(supplier.getFaxNumber());
                form.setContactPerson(supplier.getContactPerson());
                model.addAttribute("form", form);
                model.addAttribute("supplierCode", supplierCode);
                model.addAttribute("effectiveFrom", effectiveFrom);
                model.addAttribute("supplierTypes", SupplierType.values());
                return "suppliers/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "取引先が見つかりません");
                return "redirect:/suppliers";
            });
    }

    /**
     * 取引先を更新する.
     */
    @PostMapping("/{supplierCode}/{effectiveFrom}")
    public String update(
            @PathVariable String supplierCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            @Valid @ModelAttribute("form") SupplierForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("supplierCode", supplierCode);
            model.addAttribute("effectiveFrom", effectiveFrom);
            model.addAttribute("supplierTypes", SupplierType.values());
            return "suppliers/edit";
        }

        Supplier supplier = supplierUseCase.updateSupplier(supplierCode, effectiveFrom, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "取引先「" + supplier.getSupplierCode() + " - " + supplier.getSupplierName() + "」を更新しました");
        return "redirect:/suppliers";
    }

    /**
     * 取引先を削除する.
     */
    @PostMapping("/{supplierCode}/{effectiveFrom}/delete")
    public String delete(
            @PathVariable String supplierCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            RedirectAttributes redirectAttributes) {
        supplierUseCase.deleteSupplier(supplierCode, effectiveFrom);
        redirectAttributes.addFlashAttribute("successMessage", "取引先を削除しました");
        return "redirect:/suppliers";
    }
}
