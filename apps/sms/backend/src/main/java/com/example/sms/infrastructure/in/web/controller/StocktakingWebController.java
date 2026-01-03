package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.StocktakingUseCase;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * 棚卸画面コントローラー.
 */
@Controller
@RequestMapping("/stocktakings")
public class StocktakingWebController {

    private final StocktakingUseCase stocktakingUseCase;

    public StocktakingWebController(StocktakingUseCase stocktakingUseCase) {
        this.stocktakingUseCase = stocktakingUseCase;
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
}
