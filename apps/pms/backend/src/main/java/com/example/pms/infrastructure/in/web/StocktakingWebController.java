package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.StocktakingUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import com.example.pms.infrastructure.in.web.form.StocktakingForm;
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
 * 棚卸画面コントローラー.
 */
@Controller
@RequestMapping("/inventory-counts")
public class StocktakingWebController {

    private final StocktakingUseCase stocktakingUseCase;
    private final LocationUseCase locationUseCase;

    public StocktakingWebController(
            StocktakingUseCase stocktakingUseCase,
            LocationUseCase locationUseCase) {
        this.stocktakingUseCase = stocktakingUseCase;
        this.locationUseCase = locationUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("locations", locationUseCase.getAllLocations());
        model.addAttribute("statusList", Arrays.asList(StocktakingStatus.values()));
    }

    /**
     * 棚卸一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Stocktaking> pageResult = stocktakingUseCase.getStocktakingList(page, size, keyword);

        model.addAttribute("stocktakingList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "inventory-counts/list";
    }

    /**
     * 棚卸登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new StocktakingForm());
        addMasterData(model);
        return "inventory-counts/new";
    }

    /**
     * 棚卸を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") StocktakingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "inventory-counts/new";
        }

        stocktakingUseCase.createStocktaking(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "棚卸を登録しました");
        return "redirect:/inventory-counts";
    }

    /**
     * 棚卸詳細画面を表示する.
     */
    @GetMapping("/{stocktakingNumber}")
    public String show(
            @PathVariable String stocktakingNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return stocktakingUseCase.getStocktakingWithDetails(stocktakingNumber)
            .map(stocktaking -> {
                model.addAttribute("stocktaking", stocktaking);
                return "inventory-counts/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "棚卸が見つかりません");
                return "redirect:/inventory-counts";
            });
    }

    /**
     * 棚卸編集画面を表示する.
     */
    @GetMapping("/{stocktakingNumber}/edit")
    public String editForm(
            @PathVariable String stocktakingNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return stocktakingUseCase.getStocktaking(stocktakingNumber)
            .map(stocktaking -> {
                model.addAttribute("form", StocktakingForm.fromEntity(stocktaking));
                addMasterData(model);
                return "inventory-counts/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "棚卸が見つかりません");
                return "redirect:/inventory-counts";
            });
    }

    /**
     * 棚卸を更新する.
     */
    @PostMapping("/{stocktakingNumber}")
    public String update(
            @PathVariable String stocktakingNumber,
            @Valid @ModelAttribute("form") StocktakingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "inventory-counts/edit";
        }

        stocktakingUseCase.updateStocktaking(stocktakingNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "棚卸を更新しました");
        return "redirect:/inventory-counts";
    }

    /**
     * 棚卸を削除する.
     */
    @PostMapping("/{stocktakingNumber}/delete")
    public String delete(
            @PathVariable String stocktakingNumber,
            RedirectAttributes redirectAttributes) {

        stocktakingUseCase.deleteStocktaking(stocktakingNumber);
        redirectAttributes.addFlashAttribute("successMessage", "棚卸を削除しました");
        return "redirect:/inventory-counts";
    }
}
