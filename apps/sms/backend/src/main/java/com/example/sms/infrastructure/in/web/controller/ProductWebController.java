package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.infrastructure.in.web.form.ProductForm;
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
 * 商品マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/products")
public class ProductWebController {

    private final ProductUseCase productUseCase;

    public ProductWebController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    /**
     * 商品一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Product> products = productUseCase.getAllProducts();

        // カテゴリでフィルタ
        if (category != null) {
            products = products.stream()
                .filter(p -> p.getProductCategory() == category)
                .toList();
        }

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            products = products.stream()
                .filter(p -> p.getProductCode().toLowerCase(Locale.ROOT).contains(lowerKeyword)
                    || p.getProductName().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                .toList();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);
        return "products/list";
    }

    /**
     * 商品詳細画面を表示.
     */
    @GetMapping("/{productCode}")
    public String show(@PathVariable String productCode, Model model) {
        Product product = productUseCase.getProductByCode(productCode);
        model.addAttribute("product", product);
        return "products/show";
    }

    /**
     * 商品登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProductForm());
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("taxCategories", TaxCategory.values());
        return "products/new";
    }

    /**
     * 商品を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ProductCategory.values());
            model.addAttribute("taxCategories", TaxCategory.values());
            return "products/new";
        }

        productUseCase.createProduct(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "商品を登録しました");
        return "redirect:/products";
    }

    /**
     * 商品編集フォームを表示.
     */
    @GetMapping("/{productCode}/edit")
    public String editForm(@PathVariable String productCode, Model model) {
        Product product = productUseCase.getProductByCode(productCode);
        model.addAttribute("form", ProductForm.from(product));
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("taxCategories", TaxCategory.values());
        return "products/edit";
    }

    /**
     * 商品を更新.
     */
    @PostMapping("/{productCode}")
    public String update(
            @PathVariable String productCode,
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ProductCategory.values());
            model.addAttribute("taxCategories", TaxCategory.values());
            return "products/edit";
        }

        productUseCase.updateProduct(productCode, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました");
        return "redirect:/products/" + productCode;
    }

    /**
     * 商品を削除.
     */
    @PostMapping("/{productCode}/delete")
    public String delete(
            @PathVariable String productCode,
            RedirectAttributes redirectAttributes) {

        productUseCase.deleteProduct(productCode);
        redirectAttributes.addFlashAttribute("successMessage", "商品を削除しました");
        return "redirect:/products";
    }
}
