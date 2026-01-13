package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import com.example.pms.infrastructure.in.web.form.LocationForm;
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
 * 場所マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/locations")
public class LocationWebController {

    private final LocationUseCase locationUseCase;

    public LocationWebController(LocationUseCase locationUseCase) {
        this.locationUseCase = locationUseCase;
    }

    /**
     * 場所一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Location> pageResult = locationUseCase.getLocations(page, size, keyword);

        model.addAttribute("locations", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "locations/list";
    }

    /**
     * 場所登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new LocationForm());
        model.addAttribute("locationTypes", LocationType.values());
        model.addAttribute("parentLocations", locationUseCase.getAllLocations());
        return "locations/new";
    }

    /**
     * 場所を登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") LocationForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("locationTypes", LocationType.values());
            model.addAttribute("parentLocations", locationUseCase.getAllLocations());
            return "locations/new";
        }

        Location location = locationUseCase.createLocation(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "場所「" + location.getLocationCode() + " - " + location.getLocationName() + "」を登録しました");
        return "redirect:/locations";
    }

    /**
     * 場所編集画面を表示する.
     */
    @GetMapping("/{locationCode}/edit")
    public String editForm(@PathVariable String locationCode, Model model, RedirectAttributes redirectAttributes) {
        return locationUseCase.getLocation(locationCode)
            .map(location -> {
                LocationForm form = new LocationForm();
                form.setLocationCode(location.getLocationCode());
                form.setLocationName(location.getLocationName());
                form.setLocationType(location.getLocationType());
                form.setParentLocationCode(location.getParentLocationCode());
                model.addAttribute("form", form);
                model.addAttribute("locationCode", locationCode);
                model.addAttribute("locationTypes", LocationType.values());
                model.addAttribute("parentLocations", locationUseCase.getAllLocations());
                return "locations/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "場所が見つかりません");
                return "redirect:/locations";
            });
    }

    /**
     * 場所を更新する.
     */
    @PostMapping("/{locationCode}")
    public String update(
            @PathVariable String locationCode,
            @Valid @ModelAttribute("form") LocationForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("locationCode", locationCode);
            model.addAttribute("locationTypes", LocationType.values());
            model.addAttribute("parentLocations", locationUseCase.getAllLocations());
            return "locations/edit";
        }

        Location location = locationUseCase.updateLocation(locationCode, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "場所「" + location.getLocationCode() + " - " + location.getLocationName() + "」を更新しました");
        return "redirect:/locations";
    }

    /**
     * 場所を削除する.
     */
    @PostMapping("/{locationCode}/delete")
    public String delete(@PathVariable String locationCode, RedirectAttributes redirectAttributes) {
        locationUseCase.deleteLocation(locationCode);
        redirectAttributes.addFlashAttribute("successMessage", "場所を削除しました");
        return "redirect:/locations";
    }
}
