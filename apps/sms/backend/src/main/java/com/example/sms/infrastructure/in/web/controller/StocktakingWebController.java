package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.StocktakingUseCase;
import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import com.example.sms.infrastructure.in.web.form.StocktakingForm;
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

import java.util.List;
import java.util.Locale;

/**
 * 棚卸画面コントローラー.
 */
@Controller
@RequestMapping("/stocktakings")
public class StocktakingWebController {

    private final StocktakingUseCase stocktakingUseCase;
    private final WarehouseUseCase warehouseUseCase;
    private final ProductUseCase productUseCase;

    public StocktakingWebController(
            StocktakingUseCase stocktakingUseCase,
            WarehouseUseCase warehouseUseCase,
            ProductUseCase productUseCase) {
        this.stocktakingUseCase = stocktakingUseCase;
        this.warehouseUseCase = warehouseUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 棚卸一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) StocktakingStatus status,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Stocktaking> stocktakings = getFilteredStocktakings(status);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            stocktakings = stocktakings.stream()
                .filter(st -> (st.getStocktakingNumber() != null
                        && st.getStocktakingNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (st.getWarehouseCode() != null
                        && st.getWarehouseCode().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("stocktakings", stocktakings);
        model.addAttribute("statuses", StocktakingStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "stocktakings/list";
    }

    private List<Stocktaking> getFilteredStocktakings(StocktakingStatus status) {
        if (status != null) {
            return stocktakingUseCase.getStocktakingsByStatus(status);
        } else {
            return stocktakingUseCase.getAllStocktakings();
        }
    }

    /**
     * 棚卸詳細画面を表示.
     */
    @GetMapping("/{stocktakingNumber}")
    public String show(@PathVariable String stocktakingNumber, Model model) {
        Stocktaking stocktaking = stocktakingUseCase.getStocktakingWithDetails(stocktakingNumber);
        model.addAttribute("stocktaking", stocktaking);
        return "stocktakings/show";
    }

    /**
     * 棚卸登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new StocktakingForm());
        model.addAttribute("warehouses", warehouseUseCase.getAllWarehouses());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "stocktakings/new";
    }

    /**
     * 棚卸を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") StocktakingForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("warehouses", warehouseUseCase.getAllWarehouses());
            model.addAttribute("products", productUseCase.getAllProducts());
            return "stocktakings/new";
        }

        Stocktaking created = stocktakingUseCase.createStocktaking(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "棚卸を登録しました: " + created.getStocktakingNumber());
        return "redirect:/stocktakings/" + created.getStocktakingNumber();
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new StocktakingForm.StocktakingDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "stocktakings/fragments :: detailRow";
    }
}
