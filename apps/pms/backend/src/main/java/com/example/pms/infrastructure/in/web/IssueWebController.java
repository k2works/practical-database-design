package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.IssueUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Issue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 払出履歴画面コントローラー.
 */
@Controller
@RequestMapping("/issues")
public class IssueWebController {

    private final IssueUseCase issueUseCase;

    public IssueWebController(IssueUseCase issueUseCase) {
        this.issueUseCase = issueUseCase;
    }

    /**
     * 払出履歴一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Issue> pageResult = issueUseCase.getIssueList(page, size, keyword);

        model.addAttribute("issueList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "issues/list";
    }

    /**
     * 払出詳細画面を表示する.
     */
    @GetMapping("/{issueNumber}")
    public String show(
            @PathVariable String issueNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return issueUseCase.getIssue(issueNumber)
            .map(issue -> {
                model.addAttribute("issue", issue);
                return "issues/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "払出情報が見つかりません");
                return "redirect:/issues";
            });
    }
}
