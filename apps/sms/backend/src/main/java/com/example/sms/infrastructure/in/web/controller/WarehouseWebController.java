package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.domain.model.inventory.Warehouse;
import com.example.sms.domain.model.inventory.WarehouseType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

/**
 * 倉庫マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/warehouses")
public class WarehouseWebController {

    private final WarehouseUseCase warehouseUseCase;

    public WarehouseWebController(WarehouseUseCase warehouseUseCase) {
        this.warehouseUseCase = warehouseUseCase;
    }

    /**
     * 倉庫一覧画面を表示.
     */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Warehouse> warehouses = warehouseUseCase.getAllWarehouses();

        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            warehouses = warehouses.stream()
                .filter(w -> (w.getWarehouseCode() != null
                        && w.getWarehouseCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (w.getWarehouseName() != null
                        && w.getWarehouseName().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (w.getWarehouseNameKana() != null
                        && w.getWarehouseNameKana().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("warehouses", warehouses);
        model.addAttribute("keyword", keyword);
        return "warehouses/list";
    }

    /**
     * 倉庫詳細画面を表示.
     */
    @GetMapping("/{warehouseCode}")
    public String show(@PathVariable String warehouseCode, Model model) {
        Warehouse warehouse = warehouseUseCase.getWarehouseByCode(warehouseCode);
        model.addAttribute("warehouse", warehouse);
        return "warehouses/show";
    }

    /**
     * 倉庫登録画面を表示.
     */
    @GetMapping("/new")
    public String newWarehouse(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        model.addAttribute("warehouseTypes", WarehouseType.values());
        model.addAttribute("isNew", true);
        return "warehouses/form";
    }

    /**
     * 倉庫を登録.
     */
    @PostMapping
    public String create(@ModelAttribute Warehouse warehouse, RedirectAttributes redirectAttributes) {
        warehouseUseCase.createWarehouse(warehouse);
        redirectAttributes.addFlashAttribute("successMessage",
            "倉庫を登録しました: " + warehouse.getWarehouseCode());
        return "redirect:/warehouses";
    }

    /**
     * 倉庫編集画面を表示.
     */
    @GetMapping("/{warehouseCode}/edit")
    public String edit(@PathVariable String warehouseCode, Model model) {
        Warehouse warehouse = warehouseUseCase.getWarehouseByCode(warehouseCode);
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("warehouseTypes", WarehouseType.values());
        model.addAttribute("isNew", false);
        return "warehouses/form";
    }

    /**
     * 倉庫を更新.
     */
    @PostMapping("/{warehouseCode}")
    public String update(@PathVariable String warehouseCode,
                         @ModelAttribute Warehouse warehouse,
                         RedirectAttributes redirectAttributes) {
        warehouseUseCase.updateWarehouse(warehouseCode, warehouse);
        redirectAttributes.addFlashAttribute("successMessage",
            "倉庫を更新しました: " + warehouseCode);
        return "redirect:/warehouses/" + warehouseCode;
    }

    /**
     * 倉庫を削除.
     */
    @PostMapping("/{warehouseCode}/delete")
    public String delete(@PathVariable String warehouseCode,
                         RedirectAttributes redirectAttributes) {
        warehouseUseCase.deleteWarehouse(warehouseCode);
        redirectAttributes.addFlashAttribute("successMessage",
            "倉庫を削除しました: " + warehouseCode);
        return "redirect:/warehouses";
    }
}
