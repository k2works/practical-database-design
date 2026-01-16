package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.ReceivingUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;
import com.example.pms.infrastructure.in.web.form.ReceivingForm;
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

import java.util.Arrays;

/**
 * 入荷受入画面コントローラー.
 */
@Controller
@RequestMapping("/receivings")
public class ReceivingWebController {

    private final ReceivingUseCase receivingUseCase;
    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final ItemUseCase itemUseCase;
    private final StaffUseCase staffUseCase;

    public ReceivingWebController(
            ReceivingUseCase receivingUseCase,
            PurchaseOrderUseCase purchaseOrderUseCase,
            ItemUseCase itemUseCase,
            StaffUseCase staffUseCase) {
        this.receivingUseCase = receivingUseCase;
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.itemUseCase = itemUseCase;
        this.staffUseCase = staffUseCase;
    }

    /**
     * マスタデータをモデルに追加する.
     */
    private void addMasterData(Model model) {
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("staffList", staffUseCase.getAllStaff());
        model.addAttribute("purchaseOrders", purchaseOrderUseCase.getAllOrders());
        model.addAttribute("receivingTypes", ReceivingType.values());
    }

    /**
     * 入荷受入一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String receivingType,
            @RequestParam(required = false) String keyword,
            Model model) {

        ReceivingType type = parseReceivingType(receivingType);
        PageResult<Receiving> pageResult = receivingUseCase.getReceivingList(page, size, type, keyword);

        model.addAttribute("receivingList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("receivingType", receivingType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("receivingTypes", ReceivingType.values());
        return "receivings/list";
    }

    /**
     * 入荷受入登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ReceivingForm());
        addMasterData(model);
        return "receivings/new";
    }

    /**
     * 入荷受入を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ReceivingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "receivings/new";
        }

        receivingUseCase.createReceiving(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "入荷受入を登録しました");
        return "redirect:/receivings";
    }

    /**
     * 入荷受入詳細画面を表示する.
     */
    @GetMapping("/{receivingNumber}")
    public String show(
            @PathVariable String receivingNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return receivingUseCase.getReceivingWithInspections(receivingNumber)
            .map(receiving -> {
                model.addAttribute("receiving", receiving);
                return "receivings/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "入荷受入が見つかりません");
                return "redirect:/receivings";
            });
    }

    /**
     * 入荷受入編集画面を表示する.
     */
    @GetMapping("/{receivingNumber}/edit")
    public String editForm(
            @PathVariable String receivingNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return receivingUseCase.getReceiving(receivingNumber)
            .map(receiving -> {
                model.addAttribute("form", ReceivingForm.fromEntity(receiving));
                addMasterData(model);
                return "receivings/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "入荷受入が見つかりません");
                return "redirect:/receivings";
            });
    }

    /**
     * 入荷受入を更新する.
     */
    @PostMapping("/{receivingNumber}")
    public String update(
            @PathVariable String receivingNumber,
            @Valid @ModelAttribute("form") ReceivingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addMasterData(model);
            return "receivings/edit";
        }

        receivingUseCase.updateReceiving(receivingNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "入荷受入を更新しました");
        return "redirect:/receivings";
    }

    /**
     * 入荷受入を削除する.
     */
    @PostMapping("/{receivingNumber}/delete")
    public String delete(
            @PathVariable String receivingNumber,
            RedirectAttributes redirectAttributes) {

        receivingUseCase.deleteReceiving(receivingNumber);
        redirectAttributes.addFlashAttribute("successMessage", "入荷受入を削除しました");
        return "redirect:/receivings";
    }

    private ReceivingType parseReceivingType(String receivingType) {
        if (receivingType == null || receivingType.isEmpty()) {
            return null;
        }
        return Arrays.stream(ReceivingType.values())
            .filter(t -> t.name().equals(receivingType))
            .findFirst()
            .orElse(null);
    }
}
