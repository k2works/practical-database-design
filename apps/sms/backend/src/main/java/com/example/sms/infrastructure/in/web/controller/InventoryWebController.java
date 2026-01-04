package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.InventoryUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.inventory.StockMovement;
import com.example.sms.infrastructure.in.web.form.InventoryForm;
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

import java.util.List;
import java.util.Locale;

/**
 * 在庫画面コントローラー.
 */
@Controller
@RequestMapping("/inventories")
public class InventoryWebController {

    private final InventoryUseCase inventoryUseCase;
    private final WarehouseUseCase warehouseUseCase;
    private final ProductUseCase productUseCase;

    public InventoryWebController(
            InventoryUseCase inventoryUseCase,
            WarehouseUseCase warehouseUseCase,
            ProductUseCase productUseCase) {
        this.inventoryUseCase = inventoryUseCase;
        this.warehouseUseCase = warehouseUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 在庫一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Inventory> inventories = getFilteredInventories(warehouseCode);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            inventories = inventories.stream()
                .filter(inv -> (inv.getProductCode() != null
                        && inv.getProductCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (inv.getWarehouseCode() != null
                        && inv.getWarehouseCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (inv.getLocationCode() != null
                        && inv.getLocationCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (inv.getLotNumber() != null
                        && inv.getLotNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("inventories", inventories);
        model.addAttribute("selectedWarehouse", warehouseCode);
        model.addAttribute("keyword", keyword);
        return "inventories/list";
    }

    private List<Inventory> getFilteredInventories(String warehouseCode) {
        if (warehouseCode != null && !warehouseCode.isBlank()) {
            return inventoryUseCase.getInventoriesByWarehouse(warehouseCode);
        } else {
            return inventoryUseCase.getAllInventories();
        }
    }

    /**
     * 在庫詳細画面を表示.
     */
    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Inventory inventory = inventoryUseCase.getInventoryById(id);
        model.addAttribute("inventory", inventory);
        return "inventories/show";
    }

    /**
     * 在庫登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new InventoryForm());
        model.addAttribute("warehouses", warehouseUseCase.getAllWarehouses());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "inventories/new";
    }

    /**
     * 在庫を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") InventoryForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("warehouses", warehouseUseCase.getAllWarehouses());
            model.addAttribute("products", productUseCase.getAllProducts());
            return "inventories/new";
        }

        Inventory created = inventoryUseCase.createInventory(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "在庫を登録しました");
        return "redirect:/inventories/" + created.getId();
    }

    /**
     * 入出庫履歴一覧画面を表示.
     */
    @GetMapping("/movements")
    public String movementList(
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String productCode,
            Model model) {

        List<StockMovement> movements = getFilteredMovements(warehouseCode, productCode);

        model.addAttribute("movements", movements);
        model.addAttribute("selectedWarehouse", warehouseCode);
        model.addAttribute("selectedProduct", productCode);
        return "inventories/movements";
    }

    private List<StockMovement> getFilteredMovements(String warehouseCode, String productCode) {
        if (warehouseCode != null && !warehouseCode.isBlank()) {
            return inventoryUseCase.getStockMovementsByWarehouse(warehouseCode);
        } else if (productCode != null && !productCode.isBlank()) {
            return inventoryUseCase.getStockMovementsByProduct(productCode);
        } else {
            return inventoryUseCase.getAllStockMovements();
        }
    }
}
