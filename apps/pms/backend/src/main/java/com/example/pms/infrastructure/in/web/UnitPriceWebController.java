package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.application.port.in.UnitPriceUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unitprice.UnitPrice;
import com.example.pms.infrastructure.in.web.form.UnitPriceForm;
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
 * 単価マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/prices")
public class UnitPriceWebController {

    private final UnitPriceUseCase unitPriceUseCase;
    private final ItemUseCase itemUseCase;
    private final SupplierUseCase supplierUseCase;

    public UnitPriceWebController(
            UnitPriceUseCase unitPriceUseCase,
            ItemUseCase itemUseCase,
            SupplierUseCase supplierUseCase) {
        this.unitPriceUseCase = unitPriceUseCase;
        this.itemUseCase = itemUseCase;
        this.supplierUseCase = supplierUseCase;
    }

    /**
     * 単価一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String itemCode,
            Model model) {

        PageResult<UnitPrice> pageResult = unitPriceUseCase.getUnitPrices(page, size, itemCode);

        model.addAttribute("prices", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("itemCode", itemCode);
        return "prices/list";
    }

    /**
     * 単価登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new UnitPriceForm());
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        return "prices/new";
    }

    /**
     * 単価を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") UnitPriceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("items", itemUseCase.getAllItems());
            model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
            return "prices/new";
        }

        UnitPrice price = unitPriceUseCase.createUnitPrice(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "単価「" + price.getItemCode() + " - " + price.getSupplierCode() + "」を登録しました");
        return "redirect:/prices";
    }

    /**
     * 単価編集画面を表示する.
     */
    @GetMapping("/{itemCode}/{supplierCode}/{effectiveFrom}/edit")
    public String editForm(
            @PathVariable String itemCode,
            @PathVariable String supplierCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            Model model,
            RedirectAttributes redirectAttributes) {
        return unitPriceUseCase.getUnitPrice(itemCode, supplierCode, effectiveFrom)
            .map(price -> {
                UnitPriceForm form = new UnitPriceForm();
                form.setItemCode(price.getItemCode());
                form.setSupplierCode(price.getSupplierCode());
                form.setEffectiveFrom(price.getEffectiveFrom());
                form.setEffectiveTo(price.getEffectiveTo());
                form.setPrice(price.getPrice());
                form.setCurrencyCode(price.getCurrencyCode());
                model.addAttribute("form", form);
                model.addAttribute("itemCode", itemCode);
                model.addAttribute("supplierCode", supplierCode);
                model.addAttribute("effectiveFrom", effectiveFrom);
                model.addAttribute("items", itemUseCase.getAllItems());
                model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
                return "prices/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "単価が見つかりません");
                return "redirect:/prices";
            });
    }

    /**
     * 単価を更新する.
     */
    @PostMapping("/{itemCode}/{supplierCode}/{effectiveFrom}")
    public String update(
            @PathVariable String itemCode,
            @PathVariable String supplierCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            @Valid @ModelAttribute("form") UnitPriceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("itemCode", itemCode);
            model.addAttribute("supplierCode", supplierCode);
            model.addAttribute("effectiveFrom", effectiveFrom);
            model.addAttribute("items", itemUseCase.getAllItems());
            model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
            return "prices/edit";
        }

        UnitPrice price = unitPriceUseCase.updateUnitPrice(itemCode, supplierCode, effectiveFrom, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "単価「" + price.getItemCode() + " - " + price.getSupplierCode() + "」を更新しました");
        return "redirect:/prices";
    }

    /**
     * 単価を削除する.
     */
    @PostMapping("/{itemCode}/{supplierCode}/{effectiveFrom}/delete")
    public String delete(
            @PathVariable String itemCode,
            @PathVariable String supplierCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate effectiveFrom,
            RedirectAttributes redirectAttributes) {
        unitPriceUseCase.deleteUnitPrice(itemCode, supplierCode, effectiveFrom);
        redirectAttributes.addFlashAttribute("successMessage", "単価を削除しました");
        return "redirect:/prices";
    }
}
