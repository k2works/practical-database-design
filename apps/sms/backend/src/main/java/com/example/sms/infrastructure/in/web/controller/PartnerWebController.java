package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.infrastructure.in.web.form.PartnerForm;
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
 * 取引先マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/partners")
public class PartnerWebController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final PartnerUseCase partnerUseCase;

    public PartnerWebController(PartnerUseCase partnerUseCase) {
        this.partnerUseCase = partnerUseCase;
    }

    /**
     * 取引先一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Partner> partnerPage = partnerUseCase.getPartners(page, size, type, keyword);

        model.addAttribute("partners", partnerPage.getContent());
        model.addAttribute("page", partnerPage);
        model.addAttribute("selectedType", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "partners/list";
    }

    /**
     * 取引先詳細画面を表示.
     */
    @GetMapping("/{partnerCode}")
    public String show(@PathVariable String partnerCode, Model model) {
        Partner partner = partnerUseCase.getPartnerByCode(partnerCode);
        model.addAttribute("partner", partner);
        return "partners/show";
    }

    /**
     * 取引先登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new PartnerForm());
        return "partners/new";
    }

    /**
     * 取引先を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") PartnerForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "partners/new";
        }

        partnerUseCase.createPartner(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "取引先を登録しました");
        return "redirect:/partners";
    }

    /**
     * 取引先編集フォームを表示.
     */
    @GetMapping("/{partnerCode}/edit")
    public String editForm(@PathVariable String partnerCode, Model model) {
        Partner partner = partnerUseCase.getPartnerByCode(partnerCode);
        model.addAttribute("form", PartnerForm.from(partner));
        return "partners/edit";
    }

    /**
     * 取引先を更新.
     */
    @PostMapping("/{partnerCode}")
    public String update(
            @PathVariable String partnerCode,
            @Valid @ModelAttribute("form") PartnerForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "partners/edit";
        }

        partnerUseCase.updatePartner(partnerCode, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "取引先を更新しました");
        return "redirect:/partners/" + partnerCode;
    }

    /**
     * 取引先を削除.
     */
    @PostMapping("/{partnerCode}/delete")
    public String delete(
            @PathVariable String partnerCode,
            RedirectAttributes redirectAttributes) {

        partnerUseCase.deletePartner(partnerCode);
        redirectAttributes.addFlashAttribute("successMessage", "取引先を削除しました");
        return "redirect:/partners";
    }
}
