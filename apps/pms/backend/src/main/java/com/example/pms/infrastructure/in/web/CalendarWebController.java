package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CalendarUseCase;
import com.example.pms.domain.model.calendar.DateType;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.infrastructure.in.web.form.CalendarForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * カレンダーマスタ画面コントローラー.
 */
@Controller
@RequestMapping("/calendars")
public class CalendarWebController {

    private final CalendarUseCase calendarUseCase;

    public CalendarWebController(CalendarUseCase calendarUseCase) {
        this.calendarUseCase = calendarUseCase;
    }

    /**
     * カレンダー一覧画面を表示する.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<WorkCalendar> pageResult = calendarUseCase.getCalendars(page, size, keyword);

        model.addAttribute("calendars", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        return "calendars/list";
    }

    /**
     * カレンダー登録画面を表示する.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new CalendarForm());
        model.addAttribute("dateTypes", DateType.values());
        return "calendars/new";
    }

    /**
     * カレンダーを登録する.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") CalendarForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("dateTypes", DateType.values());
            return "calendars/new";
        }

        WorkCalendar calendar = calendarUseCase.createCalendar(form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "カレンダー「" + calendar.getCalendarCode() + " - " + calendar.getDate() + "」を登録しました");
        return "redirect:/calendars";
    }

    /**
     * カレンダー編集画面を表示する.
     */
    @GetMapping("/{calendarCode}/{date}/edit")
    public String editForm(
            @PathVariable String calendarCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Model model,
            RedirectAttributes redirectAttributes) {
        return calendarUseCase.getCalendar(calendarCode, date)
            .map(calendar -> {
                CalendarForm form = new CalendarForm();
                form.setCalendarCode(calendar.getCalendarCode());
                form.setDate(calendar.getDate());
                form.setDateType(calendar.getDateType());
                form.setWorkingHours(calendar.getWorkingHours());
                form.setNote(calendar.getNote());
                model.addAttribute("form", form);
                model.addAttribute("calendarCode", calendarCode);
                model.addAttribute("date", date);
                model.addAttribute("dateTypes", DateType.values());
                return "calendars/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "カレンダーが見つかりません");
                return "redirect:/calendars";
            });
    }

    /**
     * カレンダーを更新する.
     */
    @PostMapping("/{calendarCode}/{date}")
    public String update(
            @PathVariable String calendarCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Valid @ModelAttribute("form") CalendarForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("calendarCode", calendarCode);
            model.addAttribute("date", date);
            model.addAttribute("dateTypes", DateType.values());
            return "calendars/edit";
        }

        WorkCalendar calendar = calendarUseCase.updateCalendar(calendarCode, date, form.toEntity());
        redirectAttributes.addFlashAttribute("successMessage",
            "カレンダー「" + calendar.getCalendarCode() + " - " + calendar.getDate() + "」を更新しました");
        return "redirect:/calendars";
    }

    /**
     * カレンダーを削除する.
     */
    @PostMapping("/{calendarCode}/{date}/delete")
    public String delete(
            @PathVariable String calendarCode,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            RedirectAttributes redirectAttributes) {
        calendarUseCase.deleteCalendar(calendarCode, date);
        redirectAttributes.addFlashAttribute("successMessage", "カレンダーを削除しました");
        return "redirect:/calendars";
    }
}
