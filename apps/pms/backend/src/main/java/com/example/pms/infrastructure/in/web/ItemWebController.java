package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.domain.exception.DuplicateItemException;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.infrastructure.in.web.form.ItemForm;
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
 * 品目マスタ画面 Controller（モノリス版）.
 */
@Controller
@RequestMapping("/items")
public class ItemWebController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final ItemUseCase itemUseCase;

    public ItemWebController(ItemUseCase itemUseCase) {
        this.itemUseCase = itemUseCase;
    }

    /**
     * 品目一覧画面.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) String keyword,
            Model model) {

        // ページサイズの上限を設定
        int pageSize = Math.min(size, MAX_PAGE_SIZE);
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        PageResult<Item> pageResult = itemUseCase.getItems(page, pageSize, category, keyword);

        model.addAttribute("items", pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("categories", ItemCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);

        return "items/list";
    }

    /**
     * 品目詳細画面.
     */
    @GetMapping("/{itemCode}")
    public String show(@PathVariable String itemCode, Model model) {
        Item item = itemUseCase.getItem(itemCode);
        model.addAttribute("item", item);
        return "items/show";
    }

    /**
     * 品目登録画面.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ItemForm());
        model.addAttribute("categories", ItemCategory.values());
        return "items/new";
    }

    /**
     * 品目登録処理.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ItemForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ItemCategory.values());
            return "items/new";
        }

        try {
            itemUseCase.createItem(form.toCreateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "品目を登録しました");
            return "redirect:/items";
        } catch (DuplicateItemException e) {
            bindingResult.rejectValue("itemCode", "duplicate", "この品目コードは既に使用されています");
            model.addAttribute("categories", ItemCategory.values());
            return "items/new";
        }
    }

    /**
     * 品目編集画面.
     */
    @GetMapping("/{itemCode}/edit")
    public String editForm(@PathVariable String itemCode, Model model) {
        Item item = itemUseCase.getItem(itemCode);
        model.addAttribute("form", ItemForm.from(item));
        model.addAttribute("categories", ItemCategory.values());
        model.addAttribute("itemCode", itemCode);
        return "items/edit";
    }

    /**
     * 品目更新処理.
     */
    @PostMapping("/{itemCode}")
    public String update(
            @PathVariable String itemCode,
            @Valid @ModelAttribute("form") ItemForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ItemCategory.values());
            model.addAttribute("itemCode", itemCode);
            return "items/edit";
        }

        try {
            itemUseCase.updateItem(itemCode, form.toUpdateCommand());
            redirectAttributes.addFlashAttribute("successMessage", "品目を更新しました");
            return "redirect:/items/" + itemCode;
        } catch (ItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "品目が見つかりませんでした");
            return "redirect:/items";
        }
    }

    /**
     * 品目削除処理.
     */
    @PostMapping("/{itemCode}/delete")
    public String delete(
            @PathVariable String itemCode,
            RedirectAttributes redirectAttributes) {

        try {
            itemUseCase.deleteItem(itemCode);
            redirectAttributes.addFlashAttribute("successMessage", "品目を削除しました");
        } catch (ItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "品目が見つかりませんでした");
        }
        return "redirect:/items";
    }
}
