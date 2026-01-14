package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.OrderUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import com.example.pms.infrastructure.in.web.form.WorkOrderForm;
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
 * 作業指示画面コントローラー.
 */
@Controller
@RequestMapping("/work-orders")
public class WorkOrderWebController {

    private final WorkOrderUseCase workOrderUseCase;
    private final OrderUseCase orderUseCase;
    private final ItemUseCase itemUseCase;
    private final StaffUseCase staffUseCase;
    private final LocationUseCase locationUseCase;

    public WorkOrderWebController(
            WorkOrderUseCase workOrderUseCase,
            OrderUseCase orderUseCase,
            ItemUseCase itemUseCase,
            StaffUseCase staffUseCase,
            LocationUseCase locationUseCase) {
        this.workOrderUseCase = workOrderUseCase;
        this.orderUseCase = orderUseCase;
        this.itemUseCase = itemUseCase;
        this.staffUseCase = staffUseCase;
        this.locationUseCase = locationUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("orders", orderUseCase.getAllOrders());
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("staffList", staffUseCase.getAllStaff());
        model.addAttribute("locations", locationUseCase.getAllLocations());
        model.addAttribute("statuses", WorkOrderStatus.values());
    }

    /**
     * 作業指示一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<WorkOrder> pageResult = workOrderUseCase.getWorkOrderList(page, size, keyword);

        model.addAttribute("workOrderList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "work-orders/list";
    }

    /**
     * 作業指示登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new WorkOrderForm());
        addMasterData(model);
        return "work-orders/new";
    }

    /**
     * 作業指示を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") WorkOrderForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "work-orders/new";
        }

        workOrderUseCase.createWorkOrder(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "作業指示を登録しました");
        return "redirect:/work-orders";
    }

    /**
     * 作業指示詳細画面を表示する.
     */
    @GetMapping("/{workOrderNumber}")
    public String show(
            @PathVariable String workOrderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return workOrderUseCase.getWorkOrder(workOrderNumber)
            .map(workOrder -> {
                model.addAttribute("workOrder", workOrder);
                return "work-orders/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "作業指示が見つかりません");
                return "redirect:/work-orders";
            });
    }

    /**
     * 作業指示編集画面を表示する.
     */
    @GetMapping("/{workOrderNumber}/edit")
    public String editForm(
            @PathVariable String workOrderNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return workOrderUseCase.getWorkOrder(workOrderNumber)
            .map(workOrder -> {
                model.addAttribute("form", WorkOrderForm.fromEntity(workOrder));
                addMasterData(model);
                return "work-orders/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "作業指示が見つかりません");
                return "redirect:/work-orders";
            });
    }

    /**
     * 作業指示を更新する.
     */
    @PostMapping("/{workOrderNumber}")
    public String update(
            @PathVariable String workOrderNumber,
            @Valid @ModelAttribute("form") WorkOrderForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "work-orders/edit";
        }

        workOrderUseCase.updateWorkOrder(workOrderNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "作業指示を更新しました");
        return "redirect:/work-orders";
    }

    /**
     * 作業指示を削除する.
     */
    @PostMapping("/{workOrderNumber}/delete")
    public String delete(
            @PathVariable String workOrderNumber,
            RedirectAttributes redirectAttributes) {

        workOrderUseCase.deleteWorkOrder(workOrderNumber);
        redirectAttributes.addFlashAttribute("successMessage", "作業指示を削除しました");
        return "redirect:/work-orders";
    }
}
