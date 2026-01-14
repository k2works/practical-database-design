package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.MpsUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
import com.example.pms.infrastructure.in.web.form.MpsForm;
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

import java.util.Arrays;

/**
 * 基準生産計画画面コントローラー.
 */
@Controller
@RequestMapping("/mps")
public class MpsWebController {

    private final MpsUseCase mpsUseCase;
    private final ItemUseCase itemUseCase;
    private final LocationUseCase locationUseCase;

    public MpsWebController(MpsUseCase mpsUseCase, ItemUseCase itemUseCase, LocationUseCase locationUseCase) {
        this.mpsUseCase = mpsUseCase;
        this.itemUseCase = itemUseCase;
        this.locationUseCase = locationUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("locations", locationUseCase.getAllLocations());
    }

    /**
     * 基準生産計画一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            Model model) {

        PlanStatus planStatus = parseStatus(status);
        PageResult<MasterProductionSchedule> pageResult = mpsUseCase.getMpsList(page, size, planStatus, keyword);

        model.addAttribute("mpsList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuses", PlanStatus.values());
        return "mps/list";
    }

    /**
     * 基準生産計画登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new MpsForm());
        addMasterData(model);
        return "mps/new";
    }

    /**
     * 基準生産計画を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") MpsForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "mps/new";
        }

        mpsUseCase.createMps(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "基準生産計画を登録しました");
        return "redirect:/mps";
    }

    /**
     * 基準生産計画詳細画面を表示する.
     */
    @GetMapping("/{mpsNumber}")
    public String show(
            @PathVariable String mpsNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return mpsUseCase.getMpsWithOrders(mpsNumber)
            .map(mps -> {
                model.addAttribute("mps", mps);
                return "mps/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "基準生産計画が見つかりません");
                return "redirect:/mps";
            });
    }

    /**
     * 基準生産計画編集画面を表示する.
     */
    @GetMapping("/{mpsNumber}/edit")
    public String editForm(
            @PathVariable String mpsNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return mpsUseCase.getMps(mpsNumber)
            .map(mps -> {
                model.addAttribute("form", MpsForm.fromEntity(mps));
                addMasterData(model);
                return "mps/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "基準生産計画が見つかりません");
                return "redirect:/mps";
            });
    }

    /**
     * 基準生産計画を更新する.
     */
    @PostMapping("/{mpsNumber}")
    public String update(
            @PathVariable String mpsNumber,
            @Valid @ModelAttribute("form") MpsForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "mps/edit";
        }

        mpsUseCase.updateMps(mpsNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "基準生産計画を更新しました");
        return "redirect:/mps";
    }

    /**
     * 基準生産計画を確定する.
     */
    @PostMapping("/{mpsNumber}/confirm")
    public String confirm(
            @PathVariable String mpsNumber,
            RedirectAttributes redirectAttributes) {

        mpsUseCase.confirmMps(mpsNumber);
        redirectAttributes.addFlashAttribute("successMessage", "基準生産計画を確定しました");
        return "redirect:/mps";
    }

    /**
     * 基準生産計画を取消する.
     */
    @PostMapping("/{mpsNumber}/cancel")
    public String cancel(
            @PathVariable String mpsNumber,
            RedirectAttributes redirectAttributes) {

        mpsUseCase.cancelMps(mpsNumber);
        redirectAttributes.addFlashAttribute("successMessage", "基準生産計画を取消しました");
        return "redirect:/mps";
    }

    private PlanStatus parseStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        return Arrays.stream(PlanStatus.values())
            .filter(s -> s.name().equals(status))
            .findFirst()
            .orElse(null);
    }
}
