package com.example.pms.application.service;

import com.example.pms.application.port.in.CalendarUseCase;
import com.example.pms.application.port.in.command.CreateCalendarCommand;
import com.example.pms.application.port.in.command.UpdateCalendarCommand;
import com.example.pms.application.port.out.CalendarRepository;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.domain.model.common.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * カレンダーサービス（Application Service）.
 */
@Service
@Transactional
public class CalendarService implements CalendarUseCase {

    private final CalendarRepository calendarRepository;

    public CalendarService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<WorkCalendar> getCalendars(int page, int size, String keyword) {
        int offset = page * size;
        List<WorkCalendar> calendars = calendarRepository.findWithPagination(keyword, size, offset);
        long totalElements = calendarRepository.count(keyword);
        return new PageResult<>(calendars, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCalendar> getAllCalendars() {
        return calendarRepository.findAll();
    }

    @Override
    public WorkCalendar createCalendar(CreateCalendarCommand command) {
        WorkCalendar calendar = WorkCalendar.builder()
            .calendarCode(command.calendarCode())
            .date(command.date())
            .dateType(command.dateType())
            .workingHours(command.workingHours())
            .note(command.note())
            .build();
        calendarRepository.save(calendar);
        return calendar;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkCalendar> getCalendar(String calendarCode, LocalDate date) {
        return calendarRepository.findByCalendarCodeAndDate(calendarCode, date);
    }

    @Override
    public WorkCalendar updateCalendar(String calendarCode, LocalDate date, UpdateCalendarCommand command) {
        WorkCalendar calendar = WorkCalendar.builder()
            .calendarCode(calendarCode)
            .date(date)
            .dateType(command.dateType())
            .workingHours(command.workingHours())
            .note(command.note())
            .build();
        calendarRepository.update(calendar);
        return calendar;
    }

    @Override
    public void deleteCalendar(String calendarCode, LocalDate date) {
        calendarRepository.deleteByCalendarCodeAndDate(calendarCode, date);
    }
}
