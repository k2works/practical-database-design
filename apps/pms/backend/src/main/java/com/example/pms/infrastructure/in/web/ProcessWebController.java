package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.Process;
import com.example.pms.infrastructure.in.web.form.ProcessForm;
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
     * 工程登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProcessForm());
        return "processes/new";
    }

    /**
     * 工程を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ProcessForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "processes/new";
        }

        Process process = processUseCase.createProcess(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "工程「" + process.getProcessCode() + "」を登録しました");
        return "redirect:/processes";
    }

    /**
     * 工程詳細画面を表示する.
     */
    @GetMapping("/{processCode}")
    public String show(@PathVariable String processCode, Model model, RedirectAttributes redirectAttributes) {
        try {
            Process process = processUseCase.getProcess(processCode);
            model.addAttribute("process", process);
            return "processes/show";
        } catch (com.example.pms.domain.exception.ProcessNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "工程が見つかりません");
            return "redirect:/processes";
        }
    }

    /**
     * 工程編集画面を表示する.
     */
    @GetMapping("/{processCode}/edit")
    public String editForm(@PathVariable String processCode, Model model, RedirectAttributes redirectAttributes) {
        try {
            Process process = processUseCase.getProcess(processCode);
            ProcessForm form = new ProcessForm();
            form.setProcessCode(process.getProcessCode());
            form.setProcessName(process.getProcessName());
            form.setProcessType(process.getProcessType());
            form.setLocationCode(process.getLocationCode());
            model.addAttribute("form", form);
            model.addAttribute("processCode", processCode);
            return "processes/edit";
        } catch (com.example.pms.domain.exception.ProcessNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "工程が見つかりません");
            return "redirect:/processes";
        }
    }

    /**
     * 工程を更新する.
     */
    @PostMapping("/{processCode}")
    public String update(
            @PathVariable String processCode,
            @Valid @ModelAttribute("form") ProcessForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("processCode", processCode);
            return "processes/edit";
        }

        Process process = processUseCase.updateProcess(processCode, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "工程「" + process.getProcessCode() + "」を更新しました");
        return "redirect:/processes";
    }

    /**
     * 工程を削除する.
     */
    @PostMapping("/{processCode}/delete")
    public String delete(@PathVariable String processCode, RedirectAttributes redirectAttributes) {
        processUseCase.deleteProcess(processCode);
        redirectAttributes.addFlashAttribute("successMessage", "工程を削除しました");
        return "redirect:/processes";
    }
}
