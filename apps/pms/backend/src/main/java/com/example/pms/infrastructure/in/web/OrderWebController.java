package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.OrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.PlanStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

/**
 * オーダ情報画面コントローラー.
 */
@Controller
@RequestMapping("/orders")
public class OrderWebController {

    private final OrderUseCase orderUseCase;

    public OrderWebController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    /**
     * オーダ一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            Model model) {

        PlanStatus planStatus = parseStatus(status);

        PageResult<Order> pageResult = orderUseCase.getOrders(page, size, planStatus, keyword);

        model.addAttribute("orders", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuses", PlanStatus.values());
        return "orders/list";
    }

    /**
     * オーダ詳細画面を表示する.
     */
    @GetMapping("/{orderNumber}")
    public String show(
            @PathVariable String orderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return orderUseCase.getOrderWithRequirements(orderNumber)
            .map(order -> {
                model.addAttribute("order", order);
                return "orders/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "オーダが見つかりません");
                return "redirect:/orders";
            });
    }

    private PlanStatus parseStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        return Arrays.stream(PlanStatus.values())
            .filter(s -> s.name().equals(status))
            .findFirst()
            .orElse(null);
    }
}
