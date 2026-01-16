package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.ShipmentInspectionUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.ShipmentInspection;
import com.example.pms.infrastructure.in.web.form.ShipmentInspectionForm;
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
 * 出荷検査実績画面コントローラー.
 */
@Controller
@RequestMapping("/shipment-inspections")
public class ShipmentInspectionWebController {

    private final ShipmentInspectionUseCase shipmentInspectionUseCase;
    private final ItemUseCase itemUseCase;
    private final DefectUseCase defectUseCase;

    public ShipmentInspectionWebController(
            ShipmentInspectionUseCase shipmentInspectionUseCase,
            ItemUseCase itemUseCase,
            DefectUseCase defectUseCase) {
        this.shipmentInspectionUseCase = shipmentInspectionUseCase;
        this.itemUseCase = itemUseCase;
        this.defectUseCase = defectUseCase;
    }

    /**
     * 出荷検査実績一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<ShipmentInspection> pageResult =
            shipmentInspectionUseCase.getShipmentInspectionList(page, size, keyword);

        model.addAttribute("inspectionList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "shipment-inspections/list";
    }

    /**
     * 出荷検査実績登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ShipmentInspectionForm());
        addFormAttributes(model);
        return "shipment-inspections/new";
    }

    /**
     * 出荷検査実績を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ShipmentInspectionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "shipment-inspections/new";
        }

        shipmentInspectionUseCase.createShipmentInspection(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "出荷検査実績を登録しました");
        return "redirect:/shipment-inspections";
    }

    /**
     * 出荷検査実績詳細画面を表示する.
     */
    @GetMapping("/{inspectionNumber}")
    public String show(
            @PathVariable String inspectionNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return shipmentInspectionUseCase.getShipmentInspection(inspectionNumber)
            .map(inspection -> {
                model.addAttribute("inspection", inspection);
                return "shipment-inspections/show";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "出荷検査実績が見つかりません");
                return "redirect:/shipment-inspections";
            });
    }

    /**
     * 出荷検査実績編集画面を表示する.
     */
    @GetMapping("/{inspectionNumber}/edit")
    public String editForm(
            @PathVariable String inspectionNumber,
            Model model,
            RedirectAttributes redirectAttributes) {

        return shipmentInspectionUseCase.getShipmentInspection(inspectionNumber)
            .map(inspection -> {
                model.addAttribute("form", ShipmentInspectionForm.fromEntity(inspection));
                addFormAttributes(model);
                return "shipment-inspections/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "出荷検査実績が見つかりません");
                return "redirect:/shipment-inspections";
            });
    }

    /**
     * 出荷検査実績を更新する.
     */
    @PostMapping("/{inspectionNumber}")
    public String update(
            @PathVariable String inspectionNumber,
            @Valid @ModelAttribute("form") ShipmentInspectionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "shipment-inspections/edit";
        }

        shipmentInspectionUseCase.updateShipmentInspection(inspectionNumber, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage", "出荷検査実績を更新しました");
        return "redirect:/shipment-inspections";
    }

    /**
     * 出荷検査実績を削除する.
     */
    @PostMapping("/{inspectionNumber}/delete")
    public String delete(
            @PathVariable String inspectionNumber,
            RedirectAttributes redirectAttributes) {

        shipmentInspectionUseCase.deleteShipmentInspection(inspectionNumber);
        redirectAttributes.addFlashAttribute("successMessage", "出荷検査実績を削除しました");
        return "redirect:/shipment-inspections";
    }

    /**
     * フォーム用の共通属性を追加する.
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("defects", defectUseCase.getAllDefects());
    }
}
