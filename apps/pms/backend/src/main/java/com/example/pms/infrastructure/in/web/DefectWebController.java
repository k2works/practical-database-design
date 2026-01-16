package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.defect.Defect;
import com.example.pms.infrastructure.in.web.form.DefectForm;
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
 * 欠点マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/defects")
public class DefectWebController {

    private final DefectUseCase defectUseCase;

    public DefectWebController(DefectUseCase defectUseCase) {
        this.defectUseCase = defectUseCase;
    }

    /**
     * 欠点一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Defect> pageResult = defectUseCase.getDefectList(page, size, keyword);

        model.addAttribute("defectList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "defects/list";
    }

    /**
     * 欠点登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new DefectForm());
        return "defects/new";
    }

    /**
     * 欠点を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") DefectForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "defects/new";
        }

        defectUseCase.createDefect(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "欠点を登録しました");
        return "redirect:/defects";
    }

    /**
     * 欠点詳細画面を表示する.
     */
    @GetMapping("/{defectCode}")
    public String show(
            @PathVariable String defectCode,
            Model model,
            RedirectAttributes redirectAttributes) {

        return defectUseCase.getDefect(defectCode)
            .map(defect -> {
                model.addAttribute("defect", defect);
                return "defects/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "欠点が見つかりません");
                return "redirect:/defects";
            });
    }

    /**
     * 欠点編集画面を表示する.
     */
    @GetMapping("/{defectCode}/edit")
    public String editForm(
            @PathVariable String defectCode,
            Model model,
            RedirectAttributes redirectAttributes) {

        return defectUseCase.getDefect(defectCode)
            .map(defect -> {
                model.addAttribute("form", DefectForm.fromEntity(defect));
                return "defects/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "欠点が見つかりません");
                return "redirect:/defects";
            });
    }

    /**
     * 欠点を更新する.
     */
    @PostMapping("/{defectCode}")
    public String update(
            @PathVariable String defectCode,
            @Valid @ModelAttribute("form") DefectForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "defects/edit";
        }

        defectUseCase.updateDefect(defectCode, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "欠点を更新しました");
        return "redirect:/defects";
    }

    /**
     * 欠点を削除する.
     */
    @PostMapping("/{defectCode}/delete")
    public String delete(
            @PathVariable String defectCode,
            RedirectAttributes redirectAttributes) {

        defectUseCase.deleteDefect(defectCode);
        redirectAttributes.addFlashAttribute("successMessage", "欠点を削除しました");
        return "redirect:/defects";
    }
}
