package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.ProductClassificationUseCase;
import com.example.sms.domain.model.product.ProductClassification;
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
 * 商品分類マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/product-classifications")
public class ProductClassificationWebController {

    private final ProductClassificationUseCase classificationUseCase;

    public ProductClassificationWebController(ProductClassificationUseCase classificationUseCase) {
        this.classificationUseCase = classificationUseCase;
    }

    /**
     * 商品分類一覧画面を表示.
     */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<ProductClassification> classifications = classificationUseCase.getAllClassifications();

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            classifications = classifications.stream()
                .filter(c -> (c.getClassificationCode() != null
                        && c.getClassificationCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (c.getClassificationName() != null
                        && c.getClassificationName().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("classifications", classifications);
        model.addAttribute("keyword", keyword);
        return "product-classifications/list";
    }

    /**
     * 商品分類詳細画面を表示.
     */
    @GetMapping("/{classificationCode}")
    public String show(@PathVariable String classificationCode, Model model) {
        ProductClassification classification = classificationUseCase.getClassificationByCode(classificationCode);
        model.addAttribute("classification", classification);
        return "product-classifications/show";
    }

    /**
     * 商品分類登録画面を表示.
     */
    @GetMapping("/new")
    public String newClassification(Model model) {
        model.addAttribute("classification", new ProductClassification());
        model.addAttribute("isNew", true);
        return "product-classifications/form";
    }

    /**
     * 商品分類を登録.
     */
    @PostMapping
    public String create(@ModelAttribute ProductClassification classification,
                         RedirectAttributes redirectAttributes) {
        classificationUseCase.createClassification(classification);
        redirectAttributes.addFlashAttribute("successMessage",
            "商品分類を登録しました: " + classification.getClassificationCode());
        return "redirect:/product-classifications";
    }

    /**
     * 商品分類編集画面を表示.
     */
    @GetMapping("/{classificationCode}/edit")
    public String edit(@PathVariable String classificationCode, Model model) {
        ProductClassification classification = classificationUseCase.getClassificationByCode(classificationCode);
        model.addAttribute("classification", classification);
        model.addAttribute("isNew", false);
        return "product-classifications/form";
    }

    /**
     * 商品分類を更新.
     */
    @PostMapping("/{classificationCode}")
    public String update(@PathVariable String classificationCode,
                         @ModelAttribute ProductClassification classification,
                         RedirectAttributes redirectAttributes) {
        classification.setClassificationCode(classificationCode);
        classificationUseCase.updateClassification(classification);
        redirectAttributes.addFlashAttribute("successMessage",
            "商品分類を更新しました: " + classificationCode);
        return "redirect:/product-classifications/" + classificationCode;
    }
}
