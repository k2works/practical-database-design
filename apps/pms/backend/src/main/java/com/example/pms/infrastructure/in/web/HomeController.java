package com.example.pms.infrastructure.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * ホーム画面 Controller.
 */
@Controller
public class HomeController {

    /**
     * ホーム画面を表示.
     *
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/")
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("currentDate", today);
        return "index";
    }
}
