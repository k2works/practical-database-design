package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.DailyBalanceUseCase;
import com.example.fas.application.port.in.dto.DailyBalanceResponse;
import com.example.fas.domain.model.common.PageResult;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 日次残高照会画面コントローラー.
 */
@Controller
@RequestMapping("/balances/daily")
public class DailyBalanceWebController {

    private final DailyBalanceUseCase dailyBalanceUseCase;

    public DailyBalanceWebController(DailyBalanceUseCase dailyBalanceUseCase) {
        this.dailyBalanceUseCase = dailyBalanceUseCase;
    }

    /**
     * 日次残高一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String accountCode,
            Model model) {

        // ページサイズの上限を設定
        int pageSize = Math.min(size, 100);

        PageResult<DailyBalanceResponse> pageResult = dailyBalanceUseCase.getDailyBalances(
                page, pageSize, fromDate, toDate, accountCode);

        model.addAttribute("balances", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("accountCode", accountCode);
        return "balances/daily/list";
    }
}
