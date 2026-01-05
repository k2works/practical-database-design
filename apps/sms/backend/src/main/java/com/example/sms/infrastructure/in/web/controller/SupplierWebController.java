package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.SupplierUseCase;
import com.example.sms.application.port.in.command.CreateSupplierCommand;
import com.example.sms.application.port.in.command.UpdateSupplierCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.partner.Supplier;
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

/**
 * 仕入先マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/suppliers")
public class SupplierWebController {

    private final SupplierUseCase supplierUseCase;
    private final PartnerUseCase partnerUseCase;

    public SupplierWebController(SupplierUseCase supplierUseCase, PartnerUseCase partnerUseCase) {
        this.supplierUseCase = supplierUseCase;
        this.partnerUseCase = partnerUseCase;
    }

    /**
     * 仕入先一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Supplier> supplierPage = supplierUseCase.getSuppliers(page, size, keyword);
        List<Partner> partners = partnerUseCase.getSuppliers();

        model.addAttribute("suppliers", supplierPage.getContent());
        model.addAttribute("page", supplierPage);
        model.addAttribute("partners", partners);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "suppliers/list";
    }

    /**
     * 仕入先詳細画面を表示.
     */
    @GetMapping("/{supplierCode}/{branchNumber}")
    public String show(@PathVariable String supplierCode,
                       @PathVariable String branchNumber,
                       Model model) {
        Supplier supplier = supplierUseCase.getSupplierByCodeAndBranch(supplierCode, branchNumber);
        Partner partner = partnerUseCase.getPartnerByCode(supplierCode);

        model.addAttribute("supplier", supplier);
        model.addAttribute("partner", partner);
        return "suppliers/show";
    }

    /**
     * 仕入先登録画面を表示.
     */
    @GetMapping("/new")
    public String newSupplier(Model model) {
        List<Partner> partners = partnerUseCase.getSuppliers();
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("partners", partners);
        model.addAttribute("isNew", true);
        return "suppliers/form";
    }

    /**
     * 仕入先を登録.
     */
    @PostMapping
    public String create(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        CreateSupplierCommand command = new CreateSupplierCommand(
            supplier.getSupplierCode(),
            supplier.getSupplierBranchNumber(),
            supplier.getRepresentativeName(),
            supplier.getDepartmentName(),
            supplier.getPhone(),
            supplier.getFax(),
            supplier.getEmail()
        );
        supplierUseCase.createSupplier(command);
        redirectAttributes.addFlashAttribute("successMessage",
            "仕入先を登録しました: " + supplier.getSupplierCode());
        return "redirect:/suppliers";
    }

    /**
     * 仕入先編集画面を表示.
     */
    @GetMapping("/{supplierCode}/{branchNumber}/edit")
    public String edit(@PathVariable String supplierCode,
                       @PathVariable String branchNumber,
                       Model model) {
        Supplier supplier = supplierUseCase.getSupplierByCodeAndBranch(supplierCode, branchNumber);
        List<Partner> partners = partnerUseCase.getSuppliers();
        model.addAttribute("supplier", supplier);
        model.addAttribute("partners", partners);
        model.addAttribute("isNew", false);
        return "suppliers/form";
    }

    /**
     * 仕入先を更新.
     */
    @PostMapping("/{supplierCode}/{branchNumber}")
    public String update(@PathVariable String supplierCode,
                         @PathVariable String branchNumber,
                         @ModelAttribute Supplier supplier,
                         RedirectAttributes redirectAttributes) {
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            supplier.getRepresentativeName(),
            supplier.getDepartmentName(),
            supplier.getPhone(),
            supplier.getFax(),
            supplier.getEmail()
        );
        supplierUseCase.updateSupplier(supplierCode, branchNumber, command);
        redirectAttributes.addFlashAttribute("successMessage",
            "仕入先を更新しました: " + supplierCode);
        return "redirect:/suppliers/" + supplierCode + "/" + branchNumber;
    }

    /**
     * 仕入先を削除.
     */
    @PostMapping("/{supplierCode}/{branchNumber}/delete")
    public String delete(@PathVariable String supplierCode,
                         @PathVariable String branchNumber,
                         RedirectAttributes redirectAttributes) {
        supplierUseCase.deleteSupplier(supplierCode, branchNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "仕入先を削除しました: " + supplierCode);
        return "redirect:/suppliers";
    }
}
