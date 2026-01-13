package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.Process;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 工程マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/processes")
public class ProcessWebController {

    private final ProcessUseCase processUseCase;

    public ProcessWebController(ProcessUseCase processUseCase) {
        this.processUseCase = processUseCase;
    }

    /**
     * 工程一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Process> pageResult = processUseCase.getProcesses(page, size, keyword);

        model.addAttribute("processes", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "processes/list";
    }

    /**
     * 工程詳細画面を表示する.
     */
    @GetMapping("/{processCode}")
    public String show(@PathVariable String processCode, Model model) {
        Process process = processUseCase.getProcess(processCode);
        model.addAttribute("process", process);
        return "processes/show";
    }
}
