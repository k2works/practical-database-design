package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.StockUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stock;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 在庫照会画面コントローラー.
 */
@Controller
@RequestMapping("/stocks")
public class StockWebController {

    private final StockUseCase stockUseCase;

    public StockWebController(StockUseCase stockUseCase) {
        this.stockUseCase = stockUseCase;
    }

    /**
     * 在庫一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Stock> pageResult = stockUseCase.getStockList(page, size, keyword);

        model.addAttribute("stockList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "stocks/list";
    }

    /**
     * 在庫詳細画面を表示する.
     */
    @GetMapping("/{id}")
    public String show(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return stockUseCase.getStock(id)
            .map(stock -> {
                model.addAttribute("stock", stock);
                return "stocks/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "在庫情報が見つかりません");
                return "redirect:/stocks";
            });
    }
}
