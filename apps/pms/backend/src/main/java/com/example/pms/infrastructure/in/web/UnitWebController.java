package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.UnitUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unit.Unit;
import com.example.pms.infrastructure.in.web.form.UnitForm;
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
 * 単位マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/units")
public class UnitWebController {

    private final UnitUseCase unitUseCase;

    public UnitWebController(UnitUseCase unitUseCase) {
        this.unitUseCase = unitUseCase;
    }

    /**
     * 単位一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Unit> pageResult = unitUseCase.getUnits(page, size, keyword);

        model.addAttribute("units", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "units/list";
    }

    /**
     * 単位登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new UnitForm());
        return "units/new";
    }

    /**
     * 単位を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") UnitForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "units/new";
        }

        Unit unit = unitUseCase.createUnit(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "単位「" + unit.getUnitCode() + " - " + unit.getUnitName() + "」を登録しました");
        return "redirect:/units";
    }

    /**
     * 単位編集画面を表示する.
     */
    @GetMapping("/{unitCode}/edit")
    public String editForm(@PathVariable String unitCode, Model model, RedirectAttributes redirectAttributes) {
        return unitUseCase.getUnit(unitCode)
            .map(unit -> {
                UnitForm form = new UnitForm();
                form.setUnitCode(unit.getUnitCode());
                form.setUnitSymbol(unit.getUnitSymbol());
                form.setUnitName(unit.getUnitName());
                model.addAttribute("form", form);
                model.addAttribute("unitCode", unitCode);
                return "units/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "単位が見つかりません");
                return "redirect:/units";
            });
    }

    /**
     * 単位を更新する.
     */
    @PostMapping("/{unitCode}")
    public String update(
            @PathVariable String unitCode,
            @Valid @ModelAttribute("form") UnitForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("unitCode", unitCode);
            return "units/edit";
        }

        Unit unit = unitUseCase.updateUnit(unitCode, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "単位「" + unit.getUnitCode() + " - " + unit.getUnitName() + "」を更新しました");
        return "redirect:/units";
    }

    /**
     * 単位を削除する.
     */
    @PostMapping("/{unitCode}/delete")
    public String delete(@PathVariable String unitCode, RedirectAttributes redirectAttributes) {
        unitUseCase.deleteUnit(unitCode);
        redirectAttributes.addFlashAttribute("successMessage", "単位を削除しました");
        return "redirect:/units";
    }
}
