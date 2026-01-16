package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.AcceptanceUseCase;
import com.example.pms.application.port.in.InspectionUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Acceptance;
import com.example.pms.infrastructure.in.web.form.AcceptanceForm;
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
 * 検収画面コントローラー.
 */
@Controller
@RequestMapping("/acceptances")
public class AcceptanceWebController {

    private final AcceptanceUseCase acceptanceUseCase;
    private final InspectionUseCase inspectionUseCase;
    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final ItemUseCase itemUseCase;
    private final StaffUseCase staffUseCase;
    private final SupplierUseCase supplierUseCase;

    public AcceptanceWebController(
            AcceptanceUseCase acceptanceUseCase,
            InspectionUseCase inspectionUseCase,
            PurchaseOrderUseCase purchaseOrderUseCase,
            ItemUseCase itemUseCase,
            StaffUseCase staffUseCase,
            SupplierUseCase supplierUseCase) {
        this.acceptanceUseCase = acceptanceUseCase;
        this.inspectionUseCase = inspectionUseCase;
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.itemUseCase = itemUseCase;
        this.staffUseCase = staffUseCase;
        this.supplierUseCase = supplierUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("staffList", staffUseCase.getAllStaff());
        model.addAttribute("purchaseOrders", purchaseOrderUseCase.getAllOrders());
        model.addAttribute("suppliers", supplierUseCase.getAllSuppliers());
        model.addAttribute("inspections", inspectionUseCase.getAllInspections());
    }

    /**
     * 検収一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Acceptance> pageResult = acceptanceUseCase.getAcceptanceList(page, size, keyword);

        model.addAttribute("acceptanceList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "acceptances/list";
    }

    /**
     * 検収登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AcceptanceForm());
        addMasterData(model);
        return "acceptances/new";
    }

    /**
     * 検収を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") AcceptanceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "acceptances/new";
        }

        acceptanceUseCase.createAcceptance(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "検収を登録しました");
        return "redirect:/acceptances";
    }

    /**
     * 検収詳細画面を表示する.
     */
    @GetMapping("/{acceptanceNumber}")
    public String show(
            @PathVariable String acceptanceNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return acceptanceUseCase.getAcceptance(acceptanceNumber)
            .map(acceptance -> {
                model.addAttribute("acceptance", acceptance);
                return "acceptances/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "検収が見つかりません");
                return "redirect:/acceptances";
            });
    }

    /**
     * 検収編集画面を表示する.
     */
    @GetMapping("/{acceptanceNumber}/edit")
    public String editForm(
            @PathVariable String acceptanceNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return acceptanceUseCase.getAcceptance(acceptanceNumber)
            .map(acceptance -> {
                model.addAttribute("form", AcceptanceForm.fromEntity(acceptance));
                addMasterData(model);
                return "acceptances/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "検収が見つかりません");
                return "redirect:/acceptances";
            });
    }

    /**
     * 検収を更新する.
     */
    @PostMapping("/{acceptanceNumber}")
    public String update(
            @PathVariable String acceptanceNumber,
            @Valid @ModelAttribute("form") AcceptanceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "acceptances/edit";
        }

        acceptanceUseCase.updateAcceptance(acceptanceNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "検収を更新しました");
        return "redirect:/acceptances";
    }

    /**
     * 検収を削除する.
     */
    @PostMapping("/{acceptanceNumber}/delete")
    public String delete(
            @PathVariable String acceptanceNumber,
            RedirectAttributes redirectAttributes) {

        acceptanceUseCase.deleteAcceptance(acceptanceNumber);
        redirectAttributes.addFlashAttribute("successMessage", "検収を削除しました");
        return "redirect:/acceptances";
    }
}
