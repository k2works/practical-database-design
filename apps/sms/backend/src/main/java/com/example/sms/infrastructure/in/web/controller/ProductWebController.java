package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.CustomerProductPriceUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductClassificationUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.CustomerProductPrice;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.infrastructure.in.web.form.ProductForm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

/**
 * 商品マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/products")
public class ProductWebController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ProductUseCase productUseCase;
    private final ProductClassificationUseCase classificationUseCase;
    private final CustomerProductPriceUseCase customerProductPriceUseCase;
    private final PartnerUseCase partnerUseCase;

    public ProductWebController(ProductUseCase productUseCase,
                                ProductClassificationUseCase classificationUseCase,
                                CustomerProductPriceUseCase customerProductPriceUseCase,
                                PartnerUseCase partnerUseCase) {
        this.productUseCase = productUseCase;
        this.classificationUseCase = classificationUseCase;
        this.customerProductPriceUseCase = customerProductPriceUseCase;
        this.partnerUseCase = partnerUseCase;
    }

    /**
     * 商品一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Product> productPage = productUseCase.getProducts(page, size, category, keyword);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "products/list";
    }

    /**
     * 商品詳細画面を表示.
     */
    @GetMapping("/{productCode}")
    public String show(@PathVariable String productCode, Model model) {
        Product product = productUseCase.getProductByCode(productCode);
        List<CustomerProductPrice> customerPrices = customerProductPriceUseCase.getPricesByProduct(productCode);

        model.addAttribute("product", product);
        model.addAttribute("customerPrices", customerPrices);
        return "products/show";
    }

    /**
     * 商品登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(
            @RequestParam(required = false) String classificationCode,
            Model model) {
        ProductForm form = new ProductForm();
        if (classificationCode != null && !classificationCode.isBlank()) {
            form.setClassificationCode(classificationCode);
        }
        model.addAttribute("form", form);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("taxCategories", TaxCategory.values());
        model.addAttribute("classifications", classificationUseCase.getAllClassifications());
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
            model.addAttribute("classifications", classificationUseCase.getAllClassifications());
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
        model.addAttribute("classifications", classificationUseCase.getAllClassifications());
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
            model.addAttribute("classifications", classificationUseCase.getAllClassifications());
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

    // ===== 顧客別販売単価管理 =====

    /**
     * 顧客別販売単価登録画面を表示.
     */
    @GetMapping("/{productCode}/customer-prices/new")
    public String newCustomerPrice(@PathVariable String productCode, Model model) {
        Product product = productUseCase.getProductByCode(productCode);
        List<Partner> partners = partnerUseCase.getCustomers();
        CustomerProductPrice price = CustomerProductPrice.builder()
            .productCode(productCode)
            .startDate(LocalDate.now())
            .build();

        model.addAttribute("product", product);
        model.addAttribute("partners", partners);
        model.addAttribute("price", price);
        model.addAttribute("isNew", true);
        return "products/customer-price-form";
    }

    /**
     * 顧客別販売単価を登録.
     */
    @PostMapping("/{productCode}/customer-prices")
    public String createCustomerPrice(
            @PathVariable String productCode,
            @ModelAttribute CustomerProductPrice price,
            RedirectAttributes redirectAttributes) {
        price.setProductCode(productCode);
        customerProductPriceUseCase.createPrice(price);
        redirectAttributes.addFlashAttribute("successMessage",
            "顧客別販売単価を登録しました: " + price.getPartnerCode());
        return "redirect:/products/" + productCode;
    }

    /**
     * 顧客別販売単価編集画面を表示.
     */
    @GetMapping("/{productCode}/customer-prices/{partnerCode}/{startDate}/edit")
    public String editCustomerPrice(
            @PathVariable String productCode,
            @PathVariable String partnerCode,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            Model model) {
        Product product = productUseCase.getProductByCode(productCode);
        List<Partner> partners = partnerUseCase.getCustomers();
        CustomerProductPrice price = customerProductPriceUseCase.getPrice(productCode, partnerCode, startDate);

        model.addAttribute("product", product);
        model.addAttribute("partners", partners);
        model.addAttribute("price", price);
        model.addAttribute("isNew", false);
        return "products/customer-price-form";
    }

    /**
     * 顧客別販売単価を更新.
     */
    @PostMapping("/{productCode}/customer-prices/{partnerCode}/{startDate}")
    public String updateCustomerPrice(
            @PathVariable String productCode,
            @PathVariable String partnerCode,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ModelAttribute CustomerProductPrice price,
            RedirectAttributes redirectAttributes) {
        price.setProductCode(productCode);
        price.setPartnerCode(partnerCode);
        price.setStartDate(startDate);
        customerProductPriceUseCase.updatePrice(price);
        redirectAttributes.addFlashAttribute("successMessage",
            "顧客別販売単価を更新しました: " + partnerCode);
        return "redirect:/products/" + productCode;
    }

    /**
     * 顧客別販売単価を削除.
     */
    @PostMapping("/{productCode}/customer-prices/{partnerCode}/{startDate}/delete")
    public String deleteCustomerPrice(
            @PathVariable String productCode,
            @PathVariable String partnerCode,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            RedirectAttributes redirectAttributes) {
        customerProductPriceUseCase.deletePrice(productCode, partnerCode, startDate);
        redirectAttributes.addFlashAttribute("successMessage",
            "顧客別販売単価を削除しました: " + partnerCode);
        return "redirect:/products/" + productCode;
    }
}
