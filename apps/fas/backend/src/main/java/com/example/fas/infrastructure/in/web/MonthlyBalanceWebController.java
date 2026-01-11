package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.MonthlyBalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.domain.model.common.PageResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 月次残高照会画面コントローラー.
 */
@Controller
@RequestMapping("/balances/monthly")
public class MonthlyBalanceWebController {

    private final MonthlyBalanceUseCase monthlyBalanceUseCase;

    public MonthlyBalanceWebController(MonthlyBalanceUseCase monthlyBalanceUseCase) {
        this.monthlyBalanceUseCase = monthlyBalanceUseCase;
    }

    /**
     * 月次残高一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer fiscalYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String accountCode,
            Model model) {

        // ページサイズの上限を設定
        int pageSize = Math.min(size, 100);

        PageResult<MonthlyBalanceResponse> pageResult = monthlyBalanceUseCase.getMonthlyBalances(
                page, pageSize, fiscalYear, month, accountCode);

        model.addAttribute("balances", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("fiscalYear", fiscalYear);
        model.addAttribute("month", month);
        model.addAttribute("accountCode", accountCode);
        return "balances/monthly/list";
    }
}
