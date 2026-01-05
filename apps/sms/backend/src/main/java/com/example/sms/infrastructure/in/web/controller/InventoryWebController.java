package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.InventoryUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.WarehouseUseCase;
import com.example.sms.domain.model.common.PageResult;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Inventory> inventoryPage = inventoryUseCase.getInventories(page, size, keyword, warehouseCode);

        model.addAttribute("inventories", inventoryPage.getContent());
        model.addAttribute("page", inventoryPage);
        model.addAttribute("selectedWarehouse", warehouseCode);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "inventories/list";
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String productCode,
            Model model) {

        PageResult<StockMovement> movementPage = inventoryUseCase.getStockMovements(
                page, size, warehouseCode, productCode);

        model.addAttribute("movements", movementPage.getContent());
        model.addAttribute("page", movementPage);
        model.addAttribute("selectedWarehouse", warehouseCode);
        model.addAttribute("selectedProduct", productCode);
        model.addAttribute("currentSize", size);
        return "inventories/movements";
    }
}
