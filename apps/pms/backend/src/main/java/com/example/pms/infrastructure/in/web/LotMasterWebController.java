package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LotMasterUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.LotMaster;
import com.example.pms.domain.model.quality.LotType;
import com.example.pms.infrastructure.in.web.form.LotMasterForm;
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

import java.util.Arrays;
import java.util.Optional;

/**
 * ロットマスタ画面コントローラー.
 */
@Controller
@RequestMapping("/lots")
public class LotMasterWebController {

    private final LotMasterUseCase lotMasterUseCase;
    private final ItemUseCase itemUseCase;

    public LotMasterWebController(LotMasterUseCase lotMasterUseCase, ItemUseCase itemUseCase) {
        this.lotMasterUseCase = lotMasterUseCase;
        this.itemUseCase = itemUseCase;
    }

    /**
     * ロットマスタ一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<LotMaster> pageResult = lotMasterUseCase.getLotMasterList(page, size, keyword);

        model.addAttribute("lotList", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);

        return "lots/list";
    }

    /**
     * ロットマスタ登録画面を表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new LotMasterForm());
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("lotTypes", Arrays.asList(LotType.values()));
        return "lots/new";
    }

    /**
     * ロットマスタを登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") LotMasterForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("items", itemUseCase.getAllItems());
            model.addAttribute("lotTypes", Arrays.asList(LotType.values()));
            return "lots/new";
        }

        lotMasterUseCase.createLotMaster(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "ロットを登録しました。");
        return "redirect:/lots";
    }

    /**
     * ロットマスタ詳細画面を表示.
     */
    @GetMapping("/{lotNumber}")
    public String show(@PathVariable String lotNumber, Model model, RedirectAttributes redirectAttributes) {
        Optional<LotMaster> lotMaster = lotMasterUseCase.getLotMaster(lotNumber);
        if (lotMaster.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ロットが見つかりません。");
            return "redirect:/lots";
        }

        model.addAttribute("lot", lotMaster.get());
        return "lots/show";
    }

    /**
     * ロットマスタ編集画面を表示.
     */
    @GetMapping("/{lotNumber}/edit")
    public String editForm(@PathVariable String lotNumber, Model model, RedirectAttributes redirectAttributes) {
        Optional<LotMaster> lotMaster = lotMasterUseCase.getLotMaster(lotNumber);
        if (lotMaster.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ロットが見つかりません。");
            return "redirect:/lots";
        }

        model.addAttribute("form", LotMasterForm.fromEntity(lotMaster.get()));
        model.addAttribute("items", itemUseCase.getAllItems());
        model.addAttribute("lotTypes", Arrays.asList(LotType.values()));
        return "lots/edit";
    }

    /**
     * ロットマスタを更新.
     */
    @PostMapping("/{lotNumber}")
    public String update(
            @PathVariable String lotNumber,
            @Valid @ModelAttribute("form") LotMasterForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("items", itemUseCase.getAllItems());
            model.addAttribute("lotTypes", Arrays.asList(LotType.values()));
            return "lots/edit";
        }

        lotMasterUseCase.updateLotMaster(lotNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage", "ロットを更新しました。");
        return "redirect:/lots";
    }

    /**
     * ロットマスタを削除.
     */
    @PostMapping("/{lotNumber}/delete")
    public String delete(@PathVariable String lotNumber, RedirectAttributes redirectAttributes) {
        lotMasterUseCase.deleteLotMaster(lotNumber);
        redirectAttributes.addFlashAttribute("successMessage", "ロットを削除しました。");
        return "redirect:/lots";
    }
}
