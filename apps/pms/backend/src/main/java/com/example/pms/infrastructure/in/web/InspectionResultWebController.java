package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CompletionResultUseCase;
import com.example.pms.application.port.in.InspectionResultUseCase;
import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.InspectionResult;
import com.example.pms.infrastructure.in.web.form.InspectionResultForm;
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
 * 検査実績画面コントローラー.
 */
@Controller
@RequestMapping("/inspection-results")
public class InspectionResultWebController {

    private final InspectionResultUseCase inspectionResultUseCase;
    private final CompletionResultUseCase completionResultUseCase;
    private final DefectRepository defectRepository;

    public InspectionResultWebController(
            InspectionResultUseCase inspectionResultUseCase,
            CompletionResultUseCase completionResultUseCase,
            DefectRepository defectRepository) {
        this.inspectionResultUseCase = inspectionResultUseCase;
        this.completionResultUseCase = completionResultUseCase;
        this.defectRepository = defectRepository;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("completionResults", completionResultUseCase.getAllCompletionResults());
        model.addAttribute("defects", defectRepository.findAll());
    }

    /**
     * 検査実績一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<InspectionResult> pageResult = inspectionResultUseCase.getInspectionResultList(page, size, keyword);

        model.addAttribute("inspectionResultList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "inspection-results/list";
    }

    /**
     * 検査実績登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new InspectionResultForm());
        addMasterData(model);
        return "inspection-results/new";
    }

    /**
     * 検査実績を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") InspectionResultForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "inspection-results/new";
        }

        inspectionResultUseCase.createInspectionResult(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "検査実績を登録しました");
        return "redirect:/inspection-results";
    }

    /**
     * 検査実績詳細画面を表示する.
     */
    @GetMapping("/{id}")
    public String show(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return inspectionResultUseCase.getInspectionResult(id)
            .map(inspectionResult -> {
                model.addAttribute("inspectionResult", inspectionResult);
                return "inspection-results/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "検査実績が見つかりません");
                return "redirect:/inspection-results";
            });
    }

    /**
     * 検査実績編集画面を表示する.
     */
    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return inspectionResultUseCase.getInspectionResult(id)
            .map(inspectionResult -> {
                model.addAttribute("form", InspectionResultForm.fromEntity(inspectionResult));
                addMasterData(model);
                return "inspection-results/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "検査実績が見つかりません");
                return "redirect:/inspection-results";
            });
    }

    /**
     * 検査実績を更新する.
     */
    @PostMapping("/{id}")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") InspectionResultForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "inspection-results/edit";
        }

        inspectionResultUseCase.updateInspectionResult(id, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "検査実績を更新しました");
        return "redirect:/inspection-results";
    }

    /**
     * 検査実績を削除する.
     */
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        inspectionResultUseCase.deleteInspectionResult(id);
        redirectAttributes.addFlashAttribute("successMessage", "検査実績を削除しました");
        return "redirect:/inspection-results";
    }
}
