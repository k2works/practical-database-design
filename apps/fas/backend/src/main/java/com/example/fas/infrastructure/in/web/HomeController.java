package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.out.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * ホーム画面 Controller（モノリス版）.
 */
@Controller
public class HomeController {

    private final AccountUseCase accountUseCase;
    private final JournalUseCase journalUseCase;
    private final DepartmentRepository departmentRepository;

    public HomeController(
            AccountUseCase accountUseCase,
            JournalUseCase journalUseCase,
            DepartmentRepository departmentRepository) {
        this.accountUseCase = accountUseCase;
        this.journalUseCase = journalUseCase;
        this.departmentRepository = departmentRepository;
    }

    /**
     * ホーム画面を表示.
     *
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/")
    public String home(Model model) {
        LocalDate today = LocalDate.now();

        // 現在日付
        model.addAttribute("currentDate", today);

        // 勘定科目数
        int accountCount = accountUseCase.getAllAccounts().size();
        model.addAttribute("accountCount", accountCount);

        // 部門数
        int departmentCount = departmentRepository.findAll().size();
        model.addAttribute("departmentCount", departmentCount);

        // 今月の仕訳件数
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        long monthlyJournalCount = journalUseCase.countJournalsByDateRange(firstDayOfMonth, today);
        model.addAttribute("monthlyJournalCount", monthlyJournalCount);

        return "home";
    }
}
