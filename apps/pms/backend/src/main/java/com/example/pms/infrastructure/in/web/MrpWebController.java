package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.MrpUseCase;
import com.example.pms.infrastructure.in.web.form.MrpExecuteForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MRP 実行画面コントローラー.
 */
@Controller
@RequestMapping("/mrp")
public class MrpWebController {

    private final MrpUseCase mrpUseCase;

    public MrpWebController(MrpUseCase mrpUseCase) {
        this.mrpUseCase = mrpUseCase;
    }

    /**
     * MRP 実行画面を表示する.
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("form", new MrpExecuteForm());
        return "mrp/index";
    }

    /**
     * MRP を実行する.
     */
    @PostMapping("/execute")
    public String execute(
            @Valid @ModelAttribute("form") MrpExecuteForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "mrp/index";
        }

        MrpUseCase.MrpResult result = mrpUseCase.execute(form.getStartDate(), form.getEndDate());
        model.addAttribute("form", form);
        model.addAttribute("result", result);
        return "mrp/result";
    }
}
